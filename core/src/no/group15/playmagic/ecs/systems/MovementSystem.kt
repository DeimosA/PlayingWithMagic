package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Input
import ktx.ashley.mapperFor
import ktx.inject.Context
import no.group15.playmagic.commands.Command
import no.group15.playmagic.commands.CommandReceiver
import no.group15.playmagic.commands.MoveCommand
import no.group15.playmagic.ecs.components.MovementComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.move


class MovementSystem(
	priority: Int,
	private val injectContext: Context
) : EntitySystem(
	priority
), CommandReceiver {

	private lateinit var entities: ImmutableArray<Entity>
	private val movementMapper = mapperFor<MovementComponent>()
	private val transformMapper = mapperFor<TransformComponent>()

	private var localPlayerId = 0
	private var moveCommand: MoveCommand? = null


	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(
			Family.all(MovementComponent::class.java, TransformComponent::class.java).get()
		)
		Command.Type.MOVE.receiver = this
	}

	override fun update(deltaTime: Float) {

		// TODO move command only for local player
		// TODO handle position commands
		for (entity in entities) {
			val movement = movementMapper.get(entity)
			val transform = transformMapper.get(entity)

			val command = moveCommand
			if (command != null) {
				transform.position = transform.position.plus(
					command.x * movement.maxSpeed * deltaTime,
					command.y * movement.maxSpeed * deltaTime
				)
				// Move commands are relative and transient so clean up
				command.free()
				moveCommand = null
			}

			// If move command, accelerate (also, this is demo stuff, so adapt or remove)
//			movement.velocity.x += movement.acceleration * deltaTime * (movement.maxSpeed - movement.velocity.len())
			// If not decelerate

//			when {
//				movement.moveDown -> transform.position = move(Input.Keys.DOWN, transform.position)
//				movement.moveUp -> transform.position = move(Input.Keys.UP, transform.position)
//				movement.moveLeft -> transform.position = move(Input.Keys.LEFT, transform.position)
//				movement.moveRight -> transform.position = move(Input.Keys.RIGHT, transform.position)
//			}
		}
	}

	override fun receive(command: Command) {
		// TODO several input devices can be active so check if exists, and choose one (largest movement?)
		if (command is MoveCommand) moveCommand = command
	}
}
