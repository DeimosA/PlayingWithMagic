package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.MovementComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.move


// reference https://github.com/libgdx/ashley/wiki/How-to-use-Ashley

class MovementSystem(
	priority: Int,
	private val viewport: Viewport
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
			movement.velocity.x += movement.acceleration * deltaTime * (movement.maxSpeed - movement.velocity.len())
			// If not decelerate

			when {
				movement.moveDown -> transform.position = move(Input.Keys.DOWN, transform.position)
				movement.moveUp -> transform.position = move(Input.Keys.UP, transform.position)
				movement.moveLeft -> transform.position = move(Input.Keys.LEFT, transform.position)
				movement.moveRight -> transform.position = move(Input.Keys.RIGHT, transform.position)
			}

			if (transform.position.x > viewport.worldWidth / 2) transform.position.copy(x = -viewport.worldWidth / 2) // remove

		}
	}

}
