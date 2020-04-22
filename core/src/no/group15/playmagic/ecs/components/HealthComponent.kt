package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class HealthComponent: Component, Pool.Poolable {

	val maxPoints: Int = 100
	var points: Int = maxPoints


	override fun reset() {
		points = maxPoints
	}
}
