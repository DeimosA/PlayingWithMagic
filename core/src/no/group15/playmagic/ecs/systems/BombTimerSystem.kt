package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.TimerComponent

class BombTimerSystem(
	priority: Int
) : EntitySystem(
	priority
) {

	private lateinit var entities : ImmutableArray<Entity>
	private val timerMapper = mapperFor<TimerComponent>()

	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(
			Family.all(TimerComponent::class.java).get()
		)
	}

	override fun update ( deltaTime : Float ) {

		for ( entity in entities ) {
			val timer = timerMapper.get(entity)

			timer.timeLeft =- deltaTime

		}
	}
}

