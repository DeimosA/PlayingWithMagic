package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector2
import ktx.ashley.*
import ktx.collections.*
import ktx.inject.*
import no.group15.playmagic.commands.*
import no.group15.playmagic.ecs.GameMap
import no.group15.playmagic.ecs.components.PlayerComponent
import no.group15.playmagic.ecs.components.TransformComponent


class MovementSystem(
	priority: Int,
	injectContext: Context,
	private val gameMap: GameMap
) : EntitySystem(
	priority
), CommandReceiver {

	private lateinit var entities: ImmutableArray<Entity>
	private val transformMapper = mapperFor<TransformComponent>()
	private val playerMapper = mapperFor<PlayerComponent>()

	private val commandDispatcher: CommandDispatcher = injectContext.inject()
	private var moveCommand: MoveCommand? = null
	private val positionCommands = gdxMapOf<Int, PositionCommand?>()


	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(PlayerComponent::class, TransformComponent::class).get()
		)
		// Register for commands
		Command.Type.MOVE.receiver = this
		Command.Type.POSITION.receiver = this
	}

	override fun update(deltaTime: Float) {

		for (entity in entities) {
			val transform = transformMapper.get(entity)
			val player = playerMapper.get(entity)

			if (player.isLocalPlayer) {
				// Local player
				val command = moveCommand
				moveCommand = null
				if (command != null) {

					val deltaX = command.x * player.maxSpeed * deltaTime
					val deltaY = command.y * player.maxSpeed * deltaTime
					// Move commands are relative and transient so clean up
					command.free()

					// - Check if moving along axis
					// - Find edge of bounding box in the direction of movement
					// - Check if we will collide with wall in this direction
					// Then move
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
					sendCommand.playerId = player.playerId
					commandDispatcher.send(sendCommand)
				}

			} else {
				// Other player
				val command = positionCommands[player.playerId]
				if (command != null) {
					transform.setPosition(command.x, command.y)
				}
			}
		}
	}

	override fun receive(command: Command) {
		// TODO several input devices can be active so check if exists, and choose one (largest movement?), discard the other. remember to clean up the unused one
		when (command) {
			is MoveCommand -> {
				moveCommand = command
			}
			is PositionCommand -> {
				positionCommands[command.playerId] = command
			}
		}
	}

}
