package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import kotlin.collections.HashSet


class CollisionComponent : Component, Pool.Poolable {

	var collidingWith : MutableSet<Entity> = HashSet()


	override fun reset() {
		collidingWith.clear()
	}
}
