package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.TimerComponent

class TimerSystem(
	priority: Int
) : IteratingSystem(
	Family.all(TimerComponent::class.java).get(),
	priority
) {

	private val timerMapper = mapperFor<TimerComponent>()

	override fun processEntity(entity: Entity, deltaTime: Float) {
		val timer = timerMapper.get(entity)

		timer.timeLeft -= deltaTime
	}

}

