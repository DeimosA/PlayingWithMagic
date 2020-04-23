package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.*
import ktx.inject.*
import no.group15.playmagic.commands.*
import no.group15.playmagic.ecs.components.PlayerComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.entities.EntityFactory


/**
 * Manages creation and destruction of entities
 */
class EntityManagementSystem(
	priority: Int,
	private val injectContext: Context
) : EntitySystem(
	priority
), CommandReceiver {

	private lateinit var entities: ImmutableArray<Entity>
	private val transformMapper = mapperFor<TransformComponent>()
	private val playerMapper = mapperFor<PlayerComponent>()


	override fun addedToEngine(engine: Engine) {
		setProcessing(false)
		entities = engine.getEntitiesFor(
			allOf(PlayerComponent::class, TransformComponent::class).get()
		)
		// Register for commands
		Command.Type.CONFIG.receiver = this
		Command.Type.SPAWN_PLAYER.receiver = this
		Command.Type.REMOVE_PLAYER.receiver = this
	}

	override fun receive(command: Command) {
		when (command) {
			is ConfigCommand -> {
				// Spawn local player entity
				val entity = EntityFactory.makeEntity(injectContext.inject(), engine as PooledEngine, EntityFactory.Type.PLAYER)
				val player = playerMapper.get(entity)
				player.isLocalPlayer = true
				player.playerId = command.playerId
				transformMapper.get(entity).setPosition(command.spawnPosX, command.spawnPosY)
			}
			is SpawnPlayerCommand -> {
				// Spawn other player
				val entity = EntityFactory.makeEntity(injectContext.inject(), engine as PooledEngine, EntityFactory.Type.PLAYER)
				val player = playerMapper.get(entity)
				player.isLocalPlayer = false
				player.playerId = command.playerId
				transformMapper.get(entity).setPosition(command.posX, command.posY)
			}
			is RemovePlayerCommand -> {
				// Remove player from game
				entities.forEach {
					if (playerMapper[it].playerId == command.playerId) {
						engine.removeEntity(it)
					}
				}
			}
		}
	}
}
