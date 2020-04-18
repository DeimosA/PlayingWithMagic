package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HealthComponent: Component, Pool.Poolable {
	var points: Int = 100 // TODO choose a good default value?
	var maxPoints: Int = 100

	override fun reset() {
		points = 100
		maxPoints = 100
	}
}
