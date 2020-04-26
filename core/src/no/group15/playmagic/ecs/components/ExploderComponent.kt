package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class ExploderComponent : Component, Pool.Poolable {

	var range: Float = 0F
	val damage = 150
	var isExploded: Boolean = false


	override fun reset() {
		range = 0f
		isExploded = false
	}

}
