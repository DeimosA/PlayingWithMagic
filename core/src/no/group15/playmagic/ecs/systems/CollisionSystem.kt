package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.*
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.CollisionComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.events.CollisionEvent
import java.util.*


class CollisionSystem(
	priority: Int
) : EntitySystem(
	priority
) {

	private lateinit var entities: ImmutableArray<Entity>
	private val collision = mapperFor<CollisionComponent>()
	private val transform = mapperFor<TransformComponent>()
	private val texture = mapperFor<TextureComponent>()
	private val familiesListenersMap: MutableMap<FamilyPair, Signal<CollisionEvent>> = HashMap()



	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(CollisionComponent::class, TransformComponent::class, TextureComponent::class).get()
		)
	}



	override fun update(deltaTime: Float) {

		for ( i in 0 until entities.size()) {
			for (j in i + 1 until entities.size()) {
				var entity1 = entities[i]
				var entity2 = entities[j]

				if (areColliding(entity1, entity2)) {

					if (isNewCollision(entity1, entity2)) {
						selectiveDispatch(CollisionEvent(entity1, entity2))
					}

					// component state update
					entity1[collision]!!.collidingWith.add(entity2)
					entity2[collision]!!.collidingWith.add(entity1)

				} else {
					// component state update
					entity1[collision]!!.collidingWith.remove(entity2)
					entity2[collision]!!.collidingWith.remove(entity1)
				}

			}
		}

	}



	fun registerListener(family1: Family, family2: Family, listener: Listener<CollisionEvent>) {
		// TODO: check if FamilyPair equality works
		familiesListenersMap.getOrPut(FamilyPair(family1, family2), {Signal()}).add(listener)
	}



	// -- IMPLEMENTATION --



	private fun isNewCollision(entity1: Entity, entity2: Entity) =
		! entity1[collision]!!.collidingWith.contains(entity2)



	private fun areColliding(entity1: Entity, entity2: Entity) =
		rectangleOf(entity1).overlaps(rectangleOf(entity2))



	private fun rectangleOf(entity: Entity): Rectangle {
		// TODO replace this with boundingbox in transform
		return Rectangle(
			entity[transform]!!.boundingBox.x,
			entity[transform]!!.boundingBox.y,
			entity[transform]!!.boundingBox.width * entity[transform]!!.scale.x,
			entity[transform]!!.boundingBox.height * entity[transform]!!.scale.y
		)
	}



	private fun selectiveDispatch(event: CollisionEvent) {
		for( (families, signal) in familiesListenersMap ) {
			if(families.matches(event.entity1, event.entity2)) {
				signal.dispatch(event)
			}
		}
	}



	private class FamilyPair( family1: Family, family2: Family ) {
		val firstFamily: Family = if (family1.index < family2.index) family1 else family2
		val secondFamily: Family = if (family1.index < family2.index) family2 else family1

		fun matches(entity1: Entity, entity2: Entity): Boolean {
			return ( firstFamily.matches(entity1)  && secondFamily.matches(entity2) )
				|| ( secondFamily.matches(entity1) && firstFamily.matches(entity2) )
		}
	}



}



// TEST
fun addCollisionEntity (engine: PooledEngine, i: Int) {
	val c1 = engine.createEntity()
	val collision1 = engine.createComponent(CollisionComponent::class.java)
	val texture = engine.createComponent(TextureComponent::class.java)
	val transform = engine.createComponent(TransformComponent::class.java)
	texture.src = TextureRegion(Texture("badlogic.jpg"))
	transform.position.x = 2f * i
	//collision1.boundingBox.set(Vector3(10f, 10f, 10f), Vector3(10f, 10f, 10f))
	//collision1.shape = ColliderShape.RECTANGLE
	c1.add(collision1)
	c1.add(texture)
	c1.add(transform)
	engine.addEntity(c1)
}
