package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.HealthComponent
import no.group15.playmagic.events.ExplosionHitsPlayerEvent


const val BOMB_EXPLOSION_DAMAGE = 10 //TODO chose a good value (and a good place in the source code?)

class HealthSystem(
	priority: Int
) : EntitySystem(
	priority
), Listener<ExplosionHitsPlayerEvent> {

	private val health = mapperFor<HealthComponent>()


	override fun receive(signal: Signal<ExplosionHitsPlayerEvent>,
						 event: ExplosionHitsPlayerEvent) {

		assert( event.player.has(health) ) { "The entity hasn't a Health Component." }

		// !! required because player[health] type is nullable
		// ? gives error with -= operator
		event.player[health]!!.points -= BOMB_EXPLOSION_DAMAGE

	}

}
