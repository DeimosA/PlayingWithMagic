package no.group15.playmagic.ecs.events

import com.badlogic.ashley.core.Entity

class CollisionEvent(
	val entity1: Entity,
	val entity2: Entity
)
