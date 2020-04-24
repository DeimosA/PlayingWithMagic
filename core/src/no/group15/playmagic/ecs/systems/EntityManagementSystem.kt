package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.*
import ktx.inject.*
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.CommandReceiver
import no.group15.playmagic.commandstream.commands.*
import no.group15.playmagic.ecs.GameMap
import no.group15.playmagic.ecs.components.DestructibleComponent
import no.group15.playmagic.ecs.components.PlayerComponent
import no.group15.playmagic.ecs.components.StateComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.entities.EntityFactory
import no.group15.playmagic.ecs.events.CollisionEvent


/**
 * Manages creation and destruction of entities
 */
class EntityManagementSystem(
	priority: Int,
	private val injectContext: Context,
	private val gameMap: GameMap
) : EntitySystem(
	priority
), CommandReceiver,
	Listener<CollisionEvent> {

	private lateinit var entities: ImmutableArray<Entity>
	private val transformMapper = mapperFor<TransformComponent>()
	private val playerMapper = mapperFor<PlayerComponent>()
	private val stateMapper = mapperFor<StateComponent>()


	override fun addedToEngine(engine: Engine) {
		setProcessing(false)
		entities = engine.getEntitiesFor(
			allOf(PlayerComponent::class, TransformComponent::class, StateComponent::class).get()
		)
		// Register for commands
		Command.Type.CONFIG.receiver = this
		Command.Type.SPAWN_PLAYER.receiver = this
		Command.Type.REMOVE_PLAYER.receiver = this
		Command.Type.RESET_GAME.receiver = this
		Command.Type.KILL_PLAYER.receiver = this
		Command.Type.DESTROY.receiver = this
	}

	override fun receive(command: Command) {
		when (command) {
			is ConfigCommand -> {
				// Spawn map
				gameMap.makeEntities(engine as PooledEngine, injectContext.inject())
				// Spawn local player entity
				val entity = EntityFactory.makeEntity(injectContext.inject(), engine as PooledEngine, EntityFactory.Type.PLAYER)
				val player = playerMapper[entity]
				player.isLocalPlayer = true
				player.playerId = command.playerId
				transformMapper[entity].setPosition(command.spawnPosX, command.spawnPosY)
			}
			is SpawnPlayerCommand -> {
				// Spawn other player
				val entity = EntityFactory.makeEntity(injectContext.inject(), engine as PooledEngine, EntityFactory.Type.PLAYER)
				val player = playerMapper[entity]
				player.isLocalPlayer = false
				player.playerId = command.playerId
				transformMapper[entity].setPosition(command.posX, command.posY)
			}
			is RemovePlayerCommand -> {
				// Remove player from game
				entities.forEach {
					if (playerMapper[it].playerId == command.playerId) {
						engine.removeEntity(it)
					}
				}
			}
			is ResetGameCommand -> {
				engine.removeAllEntities()
			}
			is KillPlayerCommand -> {
				killPlayer(command.playerId)
			}
			is DestroyCommand -> {
				// TODO awaiting rock destruction fix
			}
		}
	}

	private fun killPlayer(id: Int) {
		entities.forEach {
			if (playerMapper[it].playerId == id) {
				stateMapper[it].setNewState("DEAD_RIGHT", false)
				it.remove<PlayerComponent>()
			}
		}
	}



	override fun receive(signal: Signal<CollisionEvent>, event: CollisionEvent) {
		val destructible = mapperFor<DestructibleComponent>()
		val rock = if (event.entity1.has(destructible)) event.entity1 else event.entity2
		val rockPosition = rock[transformMapper]!!.position

		gameMap.setEmptyTile(rockPosition.x, rockPosition.y)
		engine.removeEntity(rock)
	}

}
