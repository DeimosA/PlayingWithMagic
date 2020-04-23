package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.ExploderComponent
import no.group15.playmagic.ecs.components.HealthComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.events.CollisionEvent
import no.group15.playmagic.events.ExplosionHitsPlayerEvent


const val BOMB_EXPLOSION_DAMAGE = 10 //TODO chose a good value (and a good place in the source code?)

class HealthSystem(
	priority: Int
) : EntitySystem(
	priority
), Listener<CollisionEvent> {

	private val health = mapperFor<HealthComponent>()
	private val exploder = mapperFor<ExploderComponent>()
	private val transform = mapperFor<TransformComponent>()


	override fun receive(signal: Signal<CollisionEvent>,
						 event: CollisionEvent) {

		val bomb = if (event.entity1.has(exploder)) event.entity1 else event.entity2
		val player = if (event.entity1.has(health)) event.entity1 else event.entity2

		player[transform]!!.position.set(0f, 0f)
		player[transform]!!.boundingBox.setCenter(0f, 0f)
		//assert( event.player.has(health) ) { "The entity hasn't a Health Component." }

		// !! required because player[health] type is nullable
		// ? gives error with -= operator
		//event.player[health]!!.points -= BOMB_EXPLOSION_DAMAGE

	}




}
