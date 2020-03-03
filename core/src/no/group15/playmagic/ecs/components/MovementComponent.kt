package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class MovementComponent : Component {
	var velocity : Vector2 = Vector2(0F, 0F)
	var acceleration: Vector2 = Vector2(0F, 0F)
}
