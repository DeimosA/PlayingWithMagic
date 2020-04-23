package no.group15.playmagic.ecs.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import no.group15.playmagic.ecs.components.*
import no.group15.playmagic.utils.assets.GameAssets

class EntityFactory {
	companion object{
		fun makeEntity(assetManager: AssetManager, engine: PooledEngine, type: Type) : Entity{
			return when(type){
				Type.PLAYER -> makePlayer(assetManager, engine)
				Type.ROCK ->  makeRock(assetManager, engine)
				Type.BOMB ->  makeBomb(assetManager, engine)
				Type.PICKUP ->  makePickup(assetManager, engine)
				Type.WALL -> makeWall(assetManager, engine)
			}
		}

		private fun makePlayer(assetManager: AssetManager, engine: PooledEngine) : Entity{
			val player: Entity = engine.createEntity()

			val collisionComponent: CollisionComponent = engine.createComponent(CollisionComponent::class.java)
			val transformComponent: TransformComponent = engine.createComponent(TransformComponent::class.java)
			val textureComponent: TextureComponent = engine.createComponent(TextureComponent::class.java)
			val playerComponent : PlayerComponent = engine.createComponent(PlayerComponent::class.java)

			textureComponent.src = TextureRegion(assetManager.get<Texture>(GameAssets.BADLOGIC.desc.fileName))
			transformComponent.boundingBox.setSize(0.9f)
			transformComponent.boundingBox.setCenter(transformComponent.position)

			player.add(collisionComponent)
			player.add(playerComponent)
			player.add(transformComponent)
			player.add(textureComponent)

			engine.addEntity(player)
			return player
		}

		private fun makeRock(assetManager: AssetManager, engine: PooledEngine) : Entity{
			val rock: Entity = engine.createEntity()

			val collisionComponent: CollisionComponent = engine.createComponent(CollisionComponent::class.java)
			val destructibleComponent: DestructibleComponent = engine.createComponent(DestructibleComponent::class.java)
			val transformComponent: TransformComponent = engine.createComponent(TransformComponent::class.java)
			val textureComponent: TextureComponent = engine.createComponent(TextureComponent::class.java)

			textureComponent.src = TextureRegion(assetManager.get<Texture>(GameAssets.DESTRUCTIBLE_WALL.desc.fileName))

			rock.add(collisionComponent)
			rock.add(destructibleComponent)
			rock.add(transformComponent)
			rock.add(textureComponent)

			engine.addEntity(rock)
			return rock
		}

		private fun makeBomb(assetManager: AssetManager, engine: PooledEngine) : Entity{
			val bomb: Entity = engine.createEntity()

			val collisionComponent: CollisionComponent = engine.createComponent(CollisionComponent::class.java)
			val transformComponent: TransformComponent = engine.createComponent(TransformComponent::class.java)
			val textureComponent: TextureComponent = engine.createComponent(TextureComponent::class.java)
			val exploderComponent: ExploderComponent = engine.createComponent(ExploderComponent::class.java)
			val timerComponent: TimerComponent = engine.createComponent(TimerComponent::class.java)

			transformComponent.boundingBox.setSize(.5f)
			textureComponent.src = TextureRegion(assetManager.get<Texture>(GameAssets.BOMB.desc.fileName))

			bomb.add(collisionComponent)
			bomb.add(transformComponent)
			bomb.add(textureComponent)
			bomb.add(exploderComponent)
			bomb.add(timerComponent)

			engine.addEntity(bomb)
			return bomb
		}

		private fun makePickup(assetManager: AssetManager, engine: PooledEngine) : Entity{
			val pickup: Entity = engine.createEntity()

			val collisionComponent: CollisionComponent = engine.createComponent(CollisionComponent::class.java)
			val transformComponent: TransformComponent = engine.createComponent(TransformComponent::class.java)
			val textureComponent: TextureComponent = engine.createComponent(TextureComponent::class.java)

			pickup.add(collisionComponent)
			pickup.add(transformComponent)
			pickup.add(textureComponent)

			engine.addEntity(pickup)
			return pickup
		}

		private fun makeWall(assetManager: AssetManager, engine: PooledEngine) : Entity{
			val wall: Entity = engine.createEntity()

			val transformComponent: TransformComponent = engine.createComponent(TransformComponent::class.java)
			val textureComponent: TextureComponent = engine.createComponent(TextureComponent::class.java)

			textureComponent.src = TextureRegion(assetManager.get<Texture>(GameAssets.WALL.desc.fileName))

			wall.add(transformComponent)
			wall.add(textureComponent)

			engine.addEntity(wall)
			return wall
		}
	}


	enum class Type{
		PLAYER,
		ROCK,
		BOMB,
		PICKUP,
		WALL
	}
}
