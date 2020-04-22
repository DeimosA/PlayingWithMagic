package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector2
import ktx.ashley.*
import ktx.inject.*
import no.group15.playmagic.commands.*
import no.group15.playmagic.ecs.GameMap
import no.group15.playmagic.ecs.components.MovementComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.entities.EntityFactory


class MovementSystem(
	priority: Int,
	private val injectContext: Context,
	private val gameMap: GameMap
) : EntitySystem(
	priority
), CommandReceiver {

	private lateinit var entities: ImmutableArray<Entity>
	private val transformMapper = mapperFor<TransformComponent>()
	private val movementMapper = mapperFor<MovementComponent>()

	private val commandDispatcher: CommandDispatcher = injectContext.inject()
	private var localPlayerId = 0
	private var moveCommand: MoveCommand? = null


	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(MovementComponent::class, TransformComponent::class).get()
		)
		Command.Type.MOVE.receiver = this
		Command.Type.CONFIG.receiver = this
		Command.Type.SPAWN_PLAYER.receiver = this
	}

	override fun update(deltaTime: Float) {

		// TODO
		//  -move command only for local player
		//  -send position command to network
		//  -handle position commands for other players
		for (entity in entities) {
			val transform = transformMapper.get(entity)
			val movement = movementMapper.get(entity)

			val command = moveCommand
			if (command != null) {
				val deltaX = command.x * movement.maxSpeed * deltaTime
				val deltaY = command.y * movement.maxSpeed * deltaTime

				// do the movement only if there will be no overlapping with walls
				if (deltaX != 0f) {
					// x is deltaX + either left side or right side of bounding box
					val x = deltaX + transform.boundingBox.x + if (deltaX > 0f) transform.boundingBox.width else 0f
					if (!gameMap.willCollideX(
							x,
							transform.boundingBox.y,
							transform.boundingBox.y + transform.boundingBox.height
						)) {
						transform.position.x += deltaX
					}
				}

				if (deltaY != 0f) {
					val y = deltaY + transform.boundingBox.y + if (deltaY > 0f) transform.boundingBox.height else 0f
					if (!gameMap.willCollideY(
							y,
							transform.boundingBox.x,
							transform.boundingBox.x + transform.boundingBox.width
						)) {
						transform.position.y += deltaY
					}
				}

				transform.boundingBox.setCenter(transform.position)

				// Send position command for local player
				val sendCommand = commandDispatcher.createCommand(Command.Type.SEND_POSITION) as SendPositionCommand
				sendCommand.x = transform.position.x
				sendCommand.y = transform.position.y
				sendCommand.playerId = localPlayerId
				commandDispatcher.send(sendCommand)
				// Move commands are relative and transient so clean up
				command.free()
				moveCommand = null
			}
		}
	}

	override fun receive(command: Command) {
		// TODO several input devices can be active so check if exists, and choose one (largest movement?), discard the other. remember to clean up the unused one
		when (command) {
			is MoveCommand -> {
				moveCommand = command
			}
			is ConfigCommand -> {
				localPlayerId = command.playerId
				// Spawn local player entity here
				val entity = EntityFactory.makeEntity(injectContext.inject(), engine as PooledEngine, EntityFactory.Type.PLAYER)
				movementMapper.get(entity).playerId = command.playerId
				// TODO set position when spawn points are ready
//				transformMapper.get(entity).setPosition(command.spawnPosX, command.spawnPosY)
			}
			is SpawnPlayerCommand -> {
				// Spawn other player
				val entity = EntityFactory.makeEntity(injectContext.inject(), engine as PooledEngine, EntityFactory.Type.PLAYER)
				movementMapper.get(entity).playerId = command.playerId
				transformMapper.get(entity).setPosition(command.posX, command.posY)
			}
		}
	}

	fun localPlayerPosition(): Vector2 {
		var position: Vector2? = null

		for (entity in entities) {
			if (entity[movementMapper]!!.playerId == localPlayerId) {
				position = entity[transformMapper]!!.position
			}
		}

		println(position)
		return position!!
	}
}
