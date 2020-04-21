package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.CollisionComponent
import no.group15.playmagic.ecs.components.DestructibleComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.events.RockDestructionEvent

class RockDropSystem (
	priority: Int
	) : EntitySystem(
	priority
	){
	private val transform = mapperFor<TransformComponent>()
	private val texture = mapperFor<TextureComponent>()
	private lateinit var destructibleRocks: ImmutableArray<Entity>

	override fun addedToEngine ( engine : Engine) {
		destructibleRocks = engine.getEntitiesFor(
			Family.all(DestructibleComponent::class.java, TransformComponent::class.java,
				TransformComponent::class.java).get()
		)
	}

	override fun update(deltaTime: Float) {
		for (r in destructibleRocks) {
			var rock = r
			// TODO(): check if rockdestructionevent, use listener?
			engine.removeEntity(rock)
			//just a random drop chance
			if ((0..9).random() > 4) {
				// TODO(): create powerup entity
			}
		}
	}
}



