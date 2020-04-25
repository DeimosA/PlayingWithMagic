package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.TimeUtils
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
import no.group15.playmagic.ecs.events.BombTimeoutEvent
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
	private val textureMapper = mapperFor<TextureComponent>()
	private val transformMapper = mapperFor<TransformComponent>()
	private val exploderMapper = mapperFor<ExploderComponent>()
	private val playerMapper = mapperFor<PlayerComponent>()
	private val collisionMapper = mapperFor<CollisionComponent>()


	override fun addedToEngine (engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(ExploderComponent::class, TimerComponent::class, TransformComponent::class, TextureComponent::class).get()
		)
		Command.Type.DROP_BOMB.receiver = this
		Command.Type.BOMB_POSITION.receiver = this
	}

	override fun receive(signal: Signal<BombTimeoutEvent>, event: BombTimeoutEvent) {
		val exploder = exploderMapper[event.bomb]
		// explosion is over
		if (exploder.isExploded) {
			engine.removeEntity(event.bomb)
		} else {
			// bomb must explode
			textureMapper[event.bomb].src = TextureRegion(assetManager.get<Texture>(GameAssets.EXPLOSION.desc.fileName))

			val transform = transformMapper[event.bomb]
			transform.boundingBox.setSize(1.2f, 1.2f)
			transform.boundingBox.setCenter(transform.position)
			exploder.isExploded = true

			// explosion must be detected as new collision
			val collision = collisionMapper[event.bomb]
			if (collision != null) {
				for (entity in collision.collidingWith) {
					collisionMapper[entity].reset()
				}
				collision.reset()
			}

			// create new timer
			val newTimer = (engine as PooledEngine).createComponent(TimerComponent::class.java)
			newTimer.timeLeft = 1.5f

			event.bomb.add(newTimer)
		}
	}

	override fun receive(command: Command) {
		when (command) {
			is DropBombCommand -> {
				val localPlayer = getLocalPlayer()
				if (localPlayer != null) {
					val playerComponent = playerMapper[localPlayer]

					if (TimeUtils.millis() > playerComponent.previousBombDrop + playerComponent.bombCoolDown) {
						playerComponent.previousBombDrop = TimeUtils.millis()

						// get player position
						val trans = transformMapper[localPlayer]
						val bomb = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.BOMB)
						transformMapper[bomb].setPosition(trans.position.x, trans.position.y)

						// Send dropped bomb position to server
						val bombPos =
							commandDispatcher.createCommand(Command.Type.SEND_BOMB_POSITION) as SendBombPositionCommand
						bombPos.x = trans.position.x
						bombPos.y = trans.position.y
						commandDispatcher.send(bombPos)
					}
				}
			}
			is BombPositionCommand -> {
				// Receive dropped bombs from other players
				val dudBomb = EntityFactory.makeEntity(assetManager, engine as PooledEngine, EntityFactory.Type.BOMB)
				dudBomb.remove<CollisionComponent>()
				transformMapper[dudBomb].setPosition(command.x, command.y)
			}
		}
	}


	// --- IMPLEMENTATION ---

	private fun getLocalPlayer (): Entity? {
		for (entity in engine.getEntitiesFor(allOf(PlayerComponent::class).get())) {
			if (playerMapper[entity].isLocalPlayer) {
				return entity
			}
		}
		return null
	}
}
