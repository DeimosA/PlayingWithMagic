package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import ktx.ashley.*
import ktx.inject.*
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.CommandDispatcher
import no.group15.playmagic.commandstream.CommandReceiver
import no.group15.playmagic.commandstream.commands.*
import no.group15.playmagic.ecs.GameMap
import no.group15.playmagic.ecs.components.DestructibleComponent
import no.group15.playmagic.ecs.components.PlayerComponent
import no.group15.playmagic.ecs.components.StateComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.entities.EntityFactory
import no.group15.playmagic.ecs.events.CollisionEvent
import kotlin.math.abs


/**
 * Manages creation and destruction of entities
 */
class EntityManagementSystem(
	priority: Int,
	injectContext: Context,
	private val gameMap: GameMap
) : EntitySystem(
	priority
), CommandReceiver,
	Listener<CollisionEvent> {

	private val assetManager: AssetManager = injectContext.inject()
	private val commandDispatcher: CommandDispatcher = injectContext.inject()

	private lateinit var playerEntities: ImmutableArray<Entity>
	private val transformMapper = mapperFor<TransformComponent>()
	private val playerMapper = mapperFor<PlayerComponent>()
	private val stateMapper = mapperFor<StateComponent>()


	override fun addedToEngine(engine: Engine) {
		setProcessing(false)
		playerEntities = engine.getEntitiesFor(
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
				gameMap.makeEntities(engine as PooledEngine, assetManager)
				// Spawn local player entity
				val entity = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.PLAYER)
				val player = playerMapper[entity]
				player.isLocalPlayer = true
				player.playerId = command.playerId
				transformMapper[entity].setPosition(command.spawnPosX, command.spawnPosY)
			}
			is SpawnPlayerCommand -> {
				// Spawn other player
				val entity = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.PLAYER)
				val player = playerMapper[entity]
				player.isLocalPlayer = false
				player.playerId = command.playerId
				transformMapper[entity].setPosition(command.posX, command.posY)
			}
			is RemovePlayerCommand -> {
				// Remove player from game
				playerEntities.forEach {
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
				val rocks = engine.getEntitiesFor(
					allOf(DestructibleComponent::class, TransformComponent::class).get()
				)
				// Find rock
				for (rock in rocks) {
					val position = transformMapper[rock].position
					val delta = 0.1f
					if (abs(position.x - command.x) < delta && abs(position.y - command.y) < delta) {
						// Destroy rock
						gameMap.destroyRock(position.x, position.y)
						engine.removeEntity(rock)
					}
				}
			}
		}
	}

	private fun killPlayer(id: Int) {
		playerEntities.forEach {
			if (playerMapper[it].playerId == id) {
				stateMapper[it].setNewState("DEAD_RIGHT", false)
				it.remove<PlayerComponent>()
			}
		}
	}

	override fun receive(signal: Signal<CollisionEvent>, event: CollisionEvent) {
		val destructible = mapperFor<DestructibleComponent>()
		val rock = if (event.entity1.has(destructible)) event.entity1 else event.entity2
		val rockPosition = transformMapper[rock].position

		gameMap.destroyRock(rockPosition.x, rockPosition.y)
		engine.removeEntity(rock)

		// TODO send destroy command
		val command = commandDispatcher.createCommand(Command.Type.SEND_DESTROY) as SendDestroyCommand
		command.x = rockPosition.x
		command.y = rockPosition.y
		commandDispatcher.send(command)
	}
}
