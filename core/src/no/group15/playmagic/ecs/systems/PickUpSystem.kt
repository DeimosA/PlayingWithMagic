package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.assets.AssetManager
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.inject.Context
import no.group15.playmagic.ecs.components.PickupComponent
import no.group15.playmagic.ecs.components.PlayerComponent
import no.group15.playmagic.events.CollisionEvent

class PickUpSystem (
	priority: Int,
	injectContext: Context
) : EntitySystem(
	priority
), Listener<CollisionEvent> {

	private val pickUp = mapperFor<PickupComponent>()
	private val player = mapperFor<PlayerComponent>()

	override fun receive(signal: Signal<CollisionEvent>, event: CollisionEvent) {

		val pickUp = if (event.entity1.has(pickUp)) event.entity1 else event.entity2
		val player = if (event.entity1.has(player)) event.entity1 else event.entity2
		engine.removeEntity(pickUp)

	}

}
