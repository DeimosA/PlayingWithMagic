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
import java.lang.RuntimeException


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
	private val destructible = mapperFor<DestructibleComponent>()

	override fun addedToEngine (engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(ExploderComponent::class, TimerComponent::class, TransformComponent::class, TextureComponent::class).get()
		)
		Command.Type.DROP_BOMB.receiver = this
	}



	override fun receive(signal: Signal<BombTimeoutEvent>, event: BombTimeoutEvent) {
		// explosion in ended
		if (event.bomb[exploder]!!.isExploded) {
			engine.removeEntity(event.bomb)
		}
		// bomb must explode
		else {
			event.bomb[texture]!!.src = TextureRegion(assetManager.get<Texture>(GameAssets.EXPLOSION.desc.fileName))

			val bombCenter = event.bomb[transform]!!.boundingBox.getCenter(Vector2())
			event.bomb[transform]!!.boundingBox.setSize(1.2f, 1.2f)
			event.bomb[transform]!!.boundingBox.setCenter(bombCenter)
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
				val playerComponent = getLocalPlayer()[player]!!
				if (System.currentTimeMillis() > playerComponent.millisPreviousBombDrop + playerComponent.bombCooldown) {
					playerComponent.millisPreviousBombDrop = System.currentTimeMillis()
					val bomb = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.BOMB)
					bomb[timer]!!.timeLeft = 3f

					// get player position position
					val playerPos = getLocalPlayerPosition()

					bomb[transform]!!.position.set(playerPos.x, playerPos.y)
					bomb[transform]!!.boundingBox.setCenter(bomb[transform]!!.position)
				}
			}
		}
	}



	// --- IMPLEMENTATION ---

	private fun getLocalPlayer(): Entity {
		for (entity in engine.getEntitiesFor(allOf(PlayerComponent::class).get())) {
			if (entity[player]!!.isLocalPlayer) {
				return entity
			}
		}
		throw RuntimeException("Local player don't exists.")
	}



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

