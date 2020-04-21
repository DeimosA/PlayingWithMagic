package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family

import com.badlogic.ashley.utils.ImmutableArray
import no.group15.playmagic.ecs.components.DestructibleComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TransformComponent

class RockDropSystem (
	priority: Int
	) : EntitySystem(
	priority
	){

	private lateinit var destructibleRocks: ImmutableArray<Entity>

	override fun addedToEngine ( engine : Engine) {
		destructibleRocks = engine.getEntitiesFor(
			Family.all(DestructibleComponent::class.java, TransformComponent::class.java,
				TextureComponent::class.java).get()
		)
	}

	override fun update(deltaTime: Float) {
		for (r in destructibleRocks) {
			var rock = r
			// TODO(): check for rockdestructionevent,
			//if rockdestructionevent:
			engine.removeEntity(rock)
			//just a random drop chance
			if ((0..9).random() > 4) {
				// TODO(): create powerup entity
			}
		}
	}
}



