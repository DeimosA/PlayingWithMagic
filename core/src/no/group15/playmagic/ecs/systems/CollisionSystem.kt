package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.*
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.ColliderShape
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

		for ( (entity1, entity2) in entityPairs() ) {

			if (areColliding(entity1, entity2)) {

				if (isNewCollision(entity1, entity2)) {
					selectiveDispatch(CollisionEvent(entity1, entity2))
				}

				// component state update
				entity1[collision]!!.collidingWith.add(entity2)
				entity2[collision]!!.collidingWith.add(entity1)

			}
			else {
				// component state update
				entity1[collision]!!.collidingWith.remove(entity2)
				entity2[collision]!!.collidingWith.remove(entity1)
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



	private fun areColliding(entity1: Entity, entity2: Entity): Boolean {
		val shape1: ColliderShape = entity1[collision]!!.shape
		val shape2: ColliderShape = entity2[collision]!!.shape

		return when (shape1) {

			ColliderShape.CIRCLE -> when (shape2) {
				ColliderShape.CIRCLE -> Intersector.overlaps(circleOf(entity1), circleOf(entity2))
				ColliderShape.RECTANGLE -> Intersector.overlaps(circleOf(entity1), rectangleOf(entity2))
			}

			ColliderShape.RECTANGLE -> when (shape2) {
				ColliderShape.CIRCLE -> Intersector.overlaps(circleOf(entity2), rectangleOf(entity1)) // swap order
				ColliderShape.RECTANGLE -> Intersector.overlaps(rectangleOf(entity1), rectangleOf(entity2))
			}

		}

	}



	private fun circleOf(entity: Entity): Circle {
		//TODO: check correctness of this values
		//TODO: how to handle rotation?
		return Circle(
			entity[transform]!!.position.x,
			entity[transform]!!.position.y,
			entity[texture]!!.size.x
		)
	}



	private fun rectangleOf(entity: Entity): Rectangle {
		//TODO: check correctness of this values
		//TODO: how to handle rotation?
		return Rectangle(
			entity[transform]!!.position.x,
			entity[transform]!!.position.y,
			entity[texture]!!.size.x * entity[transform]!!.scale.x,
			entity[texture]!!.size.y * entity[transform]!!.scale.y
		)
	}



	private fun selectiveDispatch(event: CollisionEvent) {
		for( (families, signal) in familiesListenersMap ) {
			if(families.matches(event.entity1, event.entity2)) {
				signal.dispatch(event)
			}
		}
	}



	private fun entityPairs(): Iterable<Pair<Entity, Entity>> {
		val pairs: MutableList<Pair<Entity, Entity>> = LinkedList()

		for ( i in 0 until entities.size()) {
			for ( j in i+1 until entities.size()) {
				pairs.add( Pair(entities[i], entities[j]) )
			}
		}

		return pairs
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
