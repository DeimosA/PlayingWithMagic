package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.GameMap
import no.group15.playmagic.ecs.components.MovementComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.move


// reference https://github.com/libgdx/ashley/wiki/How-to-use-Ashley

class MovementSystem(
	priority: Int,
	private val viewport: Viewport,
	private val gameMap: GameMap
) : EntitySystem(
	priority
) {

	private lateinit var entities: ImmutableArray<Entity>
	private val movementMapper = mapperFor<MovementComponent>()
	private val transformMapper = mapperFor<TransformComponent>()

	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(
			Family.all(MovementComponent::class.java, TransformComponent::class.java).get()
		)
	}

	override fun update(deltaTime: Float) {

		for (entity in entities) {
			val movement = movementMapper.get(entity)
			val transform = transformMapper.get(entity)

			// If move command, accelerate (also, this is demo stuff, so adapt or remove)
//			movement.velocity.x += movement.acceleration * deltaTime * (movement.maxSpeed - movement.velocity.len())
			// If not decelerate

			when {
				movement.moveDown -> {
					transform.position.add(0f, -deltaTime * movement.maxSpeed)
					transform.boundingBox.setCenter(transform.position)
				}
				movement.moveUp -> {
					transform.position.add(0f, deltaTime * movement.maxSpeed)
					transform.boundingBox.setCenter(transform.position)
				}
				movement.moveLeft -> {
					transform.position.add(-deltaTime * movement.maxSpeed, 0f)
					transform.boundingBox.setCenter(transform.position)
				}
				movement.moveRight -> {
					transform.position.add(deltaTime * movement.maxSpeed, 0f)
					transform.boundingBox.setCenter(transform.position)
				}
			}

			if (gameMap.overlappingWithWall(entity)) {
				//REVERT MOVEMENT
				when {
					movement.moveDown -> {
						transform.position.add(0f, deltaTime * movement.maxSpeed)
						transform.boundingBox.setCenter(transform.position)
					}
					movement.moveUp -> {
						transform.position.add(0f, -deltaTime * movement.maxSpeed)
						transform.boundingBox.setCenter(transform.position)
					}
					movement.moveLeft -> {
						transform.position.add(deltaTime * movement.maxSpeed, 0f)
						transform.boundingBox.setCenter(transform.position)
					}
					movement.moveRight -> {
						transform.position.add(-deltaTime * movement.maxSpeed, 0f)
						transform.boundingBox.setCenter(transform.position)
					}
				}
			}

//			if (transform.position.x > viewport.worldWidth / 2) transform.position.copy(x = -viewport.worldWidth / 2) // remove

		}
	}

}
