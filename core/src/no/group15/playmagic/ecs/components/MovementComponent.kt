package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool


class MovementComponent : Component, Pool.Poolable {

	var velocity : Vector2 = Vector2(0F, 0F)
	// Constants
	val acceleration = 1f
	val maxSpeed = 1f


	override fun reset() {
		velocity.set(0f, 0f)
	}

}
