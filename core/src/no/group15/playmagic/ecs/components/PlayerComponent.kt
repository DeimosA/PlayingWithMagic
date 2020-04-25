package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class PlayerComponent : Component, Pool.Poolable {

	var playerId = 0
	var isLocalPlayer = false
	// Timestamp of previous bomb drop in ms
	var previousBombDrop: Long = 0

	// Max player movement speed
	var maxSpeed = 2f

	val bombCoolDown = 3000 //ms


	override fun reset() {
		playerId = 0
		isLocalPlayer = false
		previousBombDrop = 0
	}
}
