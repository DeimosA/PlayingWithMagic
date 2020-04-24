package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class PlayerComponent : Component, Pool.Poolable {

	var playerId = 0
	var isLocalPlayer = false
	var millisPreviousBombDrop: Long = 0

	// Max player movement speed
	val maxSpeed = 2f

	val bombCooldown = 3000 //ms


	override fun reset() {
		playerId = 0
		isLocalPlayer = false
		millisPreviousBombDrop = 0
	}
}
