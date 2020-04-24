package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ktx.ashley.*
import ktx.inject.Context
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.CommandDispatcher
import no.group15.playmagic.commandstream.CommandReceiver
import no.group15.playmagic.commandstream.commands.BombPositionCommand
import no.group15.playmagic.commandstream.commands.DropBombCommand
import no.group15.playmagic.commandstream.commands.SendBombPositionCommand
import no.group15.playmagic.ecs.components.*
import no.group15.playmagic.ecs.entities.EntityFactory
import no.group15.playmagic.events.BombTimeoutEvent
import no.group15.playmagic.utils.assets.GameAssets


class BombExploderSystem(
	priority: Int,
	injectContext: Context
): EntitySystem(
	priority
), Listener<BombTimeoutEvent>,
	CommandReceiver {

	private val assetManager: AssetManager = injectContext.inject()
	private val commandDispatcher: CommandDispatcher = injectContext.inject()

	private lateinit var entities: ImmutableArray<Entity>
	private val texture = mapperFor<TextureComponent>()
	private val transform = mapperFor<TransformComponent>()
	private val exploder = mapperFor<ExploderComponent>()
	private val player = mapperFor<PlayerComponent>()


	override fun addedToEngine (engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(ExploderComponent::class, TimerComponent::class, TransformComponent::class, TextureComponent::class).get()
		)
		Command.Type.DROP_BOMB.receiver = this
		Command.Type.BOMB_POSITION.receiver = this
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
				// get player position position
				val playerPos = getLocalPlayerPosition()
				if (playerPos != null) {
					val bomb = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.BOMB)
					transform[bomb].setPosition(playerPos.x, playerPos.y)

					// Send dropped bomb position to server
					val bombPos =
						commandDispatcher.createCommand(Command.Type.SEND_BOMB_POSITION) as SendBombPositionCommand
					bombPos.x = playerPos.x
					bombPos.y = playerPos.y
					commandDispatcher.send(bombPos)
				}
			}
			is BombPositionCommand -> {
				// Receive dropped bombs from other players
				val dudBomb = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.BOMB)
				dudBomb.remove<CollisionComponent>()
				transform[dudBomb].setPosition(command.x, command.y)
			}
		}
	}


	// --- IMPLEMENTATION ---

	private fun getLocalPlayerPosition (): Vector2? {
		var playerPos:Vector2? = null
		for (entity in engine.getEntitiesFor(allOf(PlayerComponent::class).get())) {
			if (entity[player]!!.isLocalPlayer) {
				playerPos = entity[transform]!!.position
				break
			}
		}
		return playerPos
	}
}
