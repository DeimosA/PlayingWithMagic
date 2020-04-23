package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import no.group15.playmagic.commands.Command
import no.group15.playmagic.commands.CommandReceiver
import no.group15.playmagic.commands.DropBombCommand
import no.group15.playmagic.ecs.components.*
import no.group15.playmagic.ecs.entities.EntityFactory
import no.group15.playmagic.events.BombTimeoutEvent
import no.group15.playmagic.utils.assets.GameAssets


class BombExploderSystem(
	priority: Int,
	private val assetManager: AssetManager
): EntitySystem(
	priority
), Listener<BombTimeoutEvent>,
	CommandReceiver {

	private lateinit var entities: ImmutableArray<Entity>
	private val timer = mapperFor<TimerComponent>()
	private val texture = mapperFor<TextureComponent>()
	private val transform = mapperFor<TransformComponent>()
	private val exploder = mapperFor<ExploderComponent>()
	private val player = mapperFor<PlayerComponent>()

	override fun addedToEngine (engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(ExploderComponent::class, TimerComponent::class, TransformComponent::class, TextureComponent::class).get()
		)
		Command.Type.DROP_BOMB.receiver = this
	}

	/*
	override fun update(deltaTime: Float) {
		var explosionTexture: TextureRegion = TextureRegion(assetManager.get<Texture>(GameAssets.EXPLOSION.desc.fileName))

		for (entity in entities) {
			if (entity[timer]!!.timeLeft <= 0) {

				entity[texture]!!.src = explosionTexture
				entity[exploder]!!.isExploded = true

			}
		}
	}
	 */

	override fun receive(signal: Signal<BombTimeoutEvent>, event: BombTimeoutEvent) {
		// Exploded
		if (event.bomb[exploder]!!.isExploded) {
			engine.removeEntity(event.bomb)
		}
		//Not exploded
		else {
			event.bomb[texture]!!.src = TextureRegion(assetManager.get<Texture>(GameAssets.EXPLOSION.desc.fileName))
			event.bomb[exploder]!!.isExploded = true

			// create new timer
			val newTimer = (engine as PooledEngine).createComponent(TimerComponent::class.java)
			newTimer.timeLeft = 3f

			event.bomb.add(newTimer)
		}
	}



	override fun receive(command: Command) {
		when (command) {
			is DropBombCommand -> {
				val bomb = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.BOMB)
				bomb[timer]!!.timeLeft = 3f

				// get player position position
				val playerPos = getLocalPlayerPosition()

				bomb[transform]!!.position.set(playerPos.x, playerPos.y)
				bomb[transform]!!.boundingBox.setCenter(bomb[transform]!!.position)
			}
		}
	}



	// --- IMPLEMENTATION ---

	private fun getLocalPlayerPosition (): Vector2 {
		var playerPos = Vector2(0f, 0f)
		for (entity in engine.getEntitiesFor(allOf(PlayerComponent::class).get())) {
			if (entity[player]!!.isLocalPlayer) {
				playerPos = entity[transform]!!.position
				break
			}
		}

		return playerPos
	}

}

// BombExploder System Test code
fun testBomb(engine: PooledEngine, assetManager: AssetManager) {
	val bomb = createBomb(engine, assetManager)
	engine.addEntity(bomb)
	//engine.addSystem(BombExploderSystem(0, assetManager))
	//engine.addSystem(TimerSystem(0))
}

// BombExploder System Test code
fun createBomb(engine: PooledEngine, assetManager: AssetManager): Entity {
	val entity = engine.createEntity()
	val transform = engine.createComponent(TransformComponent::class.java)
	val exploder = engine.createComponent(ExploderComponent::class.java)
	val timer = engine.createComponent(TimerComponent::class.java)
	val texture = engine.createComponent(TextureComponent::class.java)
	//val assetManager: AssetManager = AssetManager()

	transform.position.set(0f, 0f)
	//transform.scale. = ImmutableVector2(2f, 2f)

	timer.timeLeft = 3f

	exploder.range = 5f

	texture.src = TextureRegion(assetManager.get<Texture>(GameAssets.BOMB.desc.fileName))

	entity.add(transform)
	entity.add(timer)
	entity.add(exploder)
	entity.add(texture)

	return entity
}
