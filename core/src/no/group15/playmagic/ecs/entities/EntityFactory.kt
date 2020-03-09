package no.group15.playmagic.ecs.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import no.group15.playmagic.ecs.components.*

class EntityFactory {
	companion object{
		fun makeEntity(engine: PooledEngine, type: Type){
			when(type){
				Type.ROCK -> makeRock(engine)
				Type.BOMB -> makeBomb(engine)
				Type.PICKUP -> makePickup(engine)
			}
		}

		private fun makeRock(engine: PooledEngine){
			val rock: Entity = engine.createEntity()

			val collisionComponent: CollisionComponent = engine.createComponent(CollisionComponent::class.java)
			val destructibleComponent: DestructibleComponent = engine.createComponent(DestructibleComponent::class.java)
			val transformComponent: TransformComponent = engine.createComponent(TransformComponent::class.java)
			val textureComponent: TextureComponent = engine.createComponent(TextureComponent::class.java)

			rock.add(collisionComponent)
			rock.add(destructibleComponent)
			rock.add(transformComponent)
			rock.add(textureComponent)

			engine.addEntity(rock)
		}

		private fun makeBomb(engine: PooledEngine){
			val bomb: Entity = engine.createEntity()

			val collisionComponent: CollisionComponent = engine.createComponent(CollisionComponent::class.java)
			val transformComponent: TransformComponent = engine.createComponent(TransformComponent::class.java)
			val textureComponent: TextureComponent = engine.createComponent(TextureComponent::class.java)
			val exploderComponent: ExploderComponent = engine.createComponent(ExploderComponent::class.java)
			val timerComponent: TimerComponent = engine.createComponent(TimerComponent::class.java)

			bomb.add(collisionComponent)
			bomb.add(transformComponent)
			bomb.add(textureComponent)
			bomb.add(exploderComponent)
			bomb.add(timerComponent)

			engine.addEntity(bomb)
		}

		private fun makePickup(engine: PooledEngine){
			val pickup: Entity = engine.createEntity()

			val collisionComponent: CollisionComponent = engine.createComponent(CollisionComponent::class.java)
			val transformComponent: TransformComponent = engine.createComponent(TransformComponent::class.java)
			val textureComponent: TextureComponent = engine.createComponent(TextureComponent::class.java)

			pickup.add(collisionComponent)
			pickup.add(transformComponent)
			pickup.add(textureComponent)

			engine.addEntity(pickup)
		}
	}


	enum class Type{
		ROCK,
		BOMB,
		PICKUP
	}
}
