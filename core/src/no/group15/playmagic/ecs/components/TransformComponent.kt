package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool


class TransformComponent : Component, Pool.Poolable {

	var position : Vector2 = Vector2(0f, 0f)
	var scale : Vector2 = Vector2(1f, 1f)
	var rotation : Float = 0f


	override fun reset() {
		position.set(0f, 0f)
		scale.set(1f, 1f)
		rotation = 0f
	}
}
