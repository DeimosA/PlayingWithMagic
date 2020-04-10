package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import kotlin.collections.HashSet

class CollisionComponent : Component {
	var shape: ColliderShape = ColliderShape.RECTANGLE
	var collidingWith : MutableSet<Entity> = HashSet()
}

enum class ColliderShape {
	RECTANGLE, CIRCLE
}
