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
			val animationComponent : AnimationComponent = engine.createComponent(AnimationComponent::class.java)
			val stateComponent : StateComponent = engine.createComponent(StateComponent::class.java)
			val sheet = assetManager.get<Texture>(GameAssets.PLAYER.desc.fileName)
			val playerComponent : PlayerComponent = engine.createComponent(PlayerComponent::class.java)

			animationComponent.src = TextureRegion.split(sheet,
				sheet.width / 13, sheet.height / 16
			)
			for (row in animationComponent.src) {
				for (region in row) {
					region.setRegion(region, 3, 10, 22, 22)
				}
			}
			stateComponent.stateMap = mapOf( "IDLE" to 0, "DROPPING" to 2,
				"WALKING_LEFT" to 9, "WALKING_RIGHT" to 1,
				"DEAD_LEFT" to 15,"DEAD_RIGHT" to 7
			)
			stateComponent.setNewState("IDLE")
			stateComponent.defaultState = "IDLE"

			animationComponent.stateFrameCount = arrayOf(
				13, 8, 2, 0, 0, 6, 4, 7, 13, 8, 2, 0, 0, 6, 4, 7
			)

			textureComponent.src = animationComponent.src[0][0]


			transformComponent.boundingBox.setSize(0.8f)
			transformComponent.boundingBox.setCenter(transformComponent.position)

			player.add(collisionComponent)
			player.add(playerComponent)
			player.add(transformComponent)
			player.add(textureComponent)
			player.add(animationComponent)
			player.add(stateComponent)

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
			timerComponent.timeLeft = 3f

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
