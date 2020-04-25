package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.MathUtils
import ktx.ashley.has
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.DestructibleComponent
import ktx.ashley.*
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.entities.EntityFactory
import no.group15.playmagic.ecs.events.CollisionEvent


class RockDropSystem (
	priority: Int,
	private val assetManager: AssetManager
) : EntitySystem(
	priority
), Listener<CollisionEvent> {
	private val destructibleMapper = mapperFor<DestructibleComponent>()
	private val transformMapper = mapperFor<TransformComponent>()


	override fun receive(signal: Signal<CollisionEvent>, event: CollisionEvent) {

		val destructible = if(event.entity1.has(destructibleMapper))event.entity1 else event.entity2
		val rockPos = destructible[transformMapper]!!.position
		if(MathUtils.random() > 0.5f) {
			val pickUp = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.PICKUP)
			transformMapper[pickUp].setPosition(rockPos.x, rockPos.y)
		}
	}
}
