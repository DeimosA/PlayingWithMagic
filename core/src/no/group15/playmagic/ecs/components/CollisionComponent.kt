package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

class CollisionComponent : Component {
	var boundingBox: BoundingBox = BoundingBox(Vector3(0F, 0F, 0F), Vector3(0F, 0F, 0F))
}
