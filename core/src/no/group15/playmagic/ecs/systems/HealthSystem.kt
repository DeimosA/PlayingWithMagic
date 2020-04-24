package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.ExploderComponent
import no.group15.playmagic.ecs.components.HealthComponent
import no.group15.playmagic.ecs.components.PlayerComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.events.CollisionEvent



class HealthSystem(
	priority: Int
) : EntitySystem(
	priority
), Listener<CollisionEvent> {

	private val health = mapperFor<HealthComponent>()
	private val exploder = mapperFor<ExploderComponent>()
	private val transform = mapperFor<TransformComponent>()
	private val player = mapperFor<PlayerComponent>()


	override fun receive(signal: Signal<CollisionEvent>,
						 event: CollisionEvent) {

		val bomb = if (event.entity1.has(exploder)) event.entity1 else event.entity2
		val player = if (event.entity1.has(player)) event.entity1 else event.entity2

		if (bomb[exploder]!!.isExploded) {
			onPlayerHitByExplosion(player)
		}

	}



	private fun onPlayerHitByExplosion(player: Entity) {
		// TODO remove, spawn point or something else?
		engine.removeEntity(player)
		//player[transform]!!.position.set(0f, 0f)
		//player[transform]!!.boundingBox.setCenter(0f, 0f)
	}




}
