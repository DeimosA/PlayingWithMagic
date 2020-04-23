package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class PlayerComponent : Component, Pool.Poolable {

	var playerId = 0
	var isLocalPlayer = false

	// Max player movement speed
	val maxSpeed = 2f


	override fun reset() {
		playerId = 0
	}
}
