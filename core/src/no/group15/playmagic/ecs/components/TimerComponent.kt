package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class TimerComponent : Component, Pool.Poolable {

	var timeLeft: Float = 0F


	override fun reset() {
		timeLeft = 0f
	}
}
