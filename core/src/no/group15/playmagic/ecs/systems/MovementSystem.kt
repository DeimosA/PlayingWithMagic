package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import no.group15.playmagic.ecs.components.MovementComponent
import no.group15.playmagic.ecs.components.TransformComponent


// reference https://github.com/libgdx/ashley/wiki/How-to-use-Ashley

class MovementSystem : EntitySystem() {

	private lateinit var entities : ImmutableArray<Entity>

	fun addToEngine ( engine : Engine ) {
		entities = engine.getEntitiesFor(Family.all(MovementComponent::class.java, TransformComponent::class.java).get())
	}

	override fun update ( deltaTime : Float ) {
		var movement : MovementComponent
		var transform : TransformComponent

		for ( entity in entities ) {
			movement = entity.getComponent(MovementComponent::class.java)
			transform = entity.getComponent(TransformComponent::class.java)

			transform.position.x += movement.velocity.x * deltaTime
			transform.position.y += movement.velocity.y * deltaTime
		}
	}

}
