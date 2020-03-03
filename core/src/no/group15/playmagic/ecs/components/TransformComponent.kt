package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class TransformComponent : Component {
	var position : Vector2 = Vector2(0F, 0F)
	var scale : Vector2 = Vector2(0F, 0F)
	var rotation : Float = 0F
}
