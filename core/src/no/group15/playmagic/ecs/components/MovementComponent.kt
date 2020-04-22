package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class MovementComponent : Component, Pool.Poolable {

	var playerId = 0

	// Constants
	val maxSpeed = 2f


	override fun reset() {
		playerId = 0
	}
}
