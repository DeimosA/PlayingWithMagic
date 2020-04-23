package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import ktx.ashley.has
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.DestructibleComponent

import no.group15.playmagic.events.RockDestructionEvent

// unsure if this is the right way to do this
class RockDropSystem (
	priority: Int
) : EntitySystem(
	priority
), Listener<RockDestructionEvent> {

	private val destructible = mapperFor<DestructibleComponent>()

	override fun receive(signal: Signal<RockDestructionEvent>,
						 event: RockDestructionEvent) {
		assert( event.rock.has(destructible))

		engine.removeEntity(event.rock)
		if((0 .. 9).random() > 4) { // just a random chance
			// TODO:  create pickup with same position as the rock
		}
	}
}
