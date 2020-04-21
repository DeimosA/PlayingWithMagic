package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector2
import ktx.ashley.*
import ktx.inject.*
import no.group15.playmagic.ecs.GameMap
import no.group15.playmagic.commands.Command
import no.group15.playmagic.commands.CommandReceiver
import no.group15.playmagic.commands.MoveCommand
import no.group15.playmagic.ecs.components.MovementComponent
import no.group15.playmagic.ecs.components.TransformComponent


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

	private var localPlayerId = 0
	private var moveCommand: MoveCommand? = null


	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(MovementComponent::class, TransformComponent::class).get()
		)
		Command.Type.MOVE.receiver = this
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

				transform.position.add(
					deltaX,
					deltaY
				)
				transform.boundingBox.setCenter(transform.position)

				// TODO the overlapping function could maybe just take in the boundingbox Rectangle and the delta position to check beforehand so we don't need to revert
				if (gameMap.willOverlapWithWall(transform.boundingBox, deltaX, deltaY)) {
					//REVERT MOVEMENT
					transform.position.add(
						-deltaX,
						-deltaY
					)
					transform.boundingBox.setCenter(transform.position)
				}

				// Move commands are relative and transient so clean up
				command.free()
				moveCommand = null
			}
		}
	}

	override fun receive(command: Command) {
		// TODO several input devices can be active so check if exists, and choose one (largest movement?), discard the other. remember to clean up the unused one
		if (command is MoveCommand) moveCommand = command
	}
}
