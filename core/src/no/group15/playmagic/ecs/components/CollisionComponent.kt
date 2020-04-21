package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import kotlin.collections.HashSet

class CollisionComponent : Component {
	var collidingWith : MutableSet<Entity> = HashSet()
}

