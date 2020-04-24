package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import ktx.ashley.has
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.DestructibleComponent
import no.group15.playmagic.events.CollisionEvent
import ktx.ashley.*
import no.group15.playmagic.ecs.components.ExploderComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.entities.EntityFactory


// unsure if this is the right way to do this
class RockDropSystem (
	priority: Int,
	private val assetManager: AssetManager
) : EntitySystem(
	priority
), Listener<CollisionEvent> {
	private val destructible = mapperFor<DestructibleComponent>()
	private val transform = mapperFor<TransformComponent>()
	private val exploder = mapperFor<ExploderComponent>()


	override fun receive(signal: Signal<CollisionEvent>,
						 event: CollisionEvent) {

		val bomb = if (event.entity1.has(exploder)) event.entity1 else event.entity2
		val destructible = if(event.entity1.has(destructible))event.entity1 else event.entity2
		if(bomb[exploder]!!.isExploded) {
			engine.removeEntity(destructible)


		}
		val rockPos = destructible[transform]!!.position
			if((0 .. 9).random() > 4) {  //just a random chance
		var pickUp = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.PICKUP)
		pickUp[transform]!!.position.set(rockPos.x, rockPos.y)
		pickUp[transform]!!.boundingBox.setCenter(pickUp[transform]!!.position)

		//	}
	}


}

