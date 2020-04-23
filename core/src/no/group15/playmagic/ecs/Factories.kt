package no.group15.playmagic.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.signals.Listener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.inject.Context
import no.group15.playmagic.ecs.entities.EntityFactory
import no.group15.playmagic.ecs.systems.*
import no.group15.playmagic.events.BombTimeoutEvent


fun engineFactory(injectContext: Context, viewport: Viewport): Engine {
	val assetManager: AssetManager = injectContext.inject()
	val batch: SpriteBatch = injectContext.inject()
	val engine = PooledEngine()

	val gameMap = GameMap()
	gameMap.makeEntities(engine, assetManager)

	// Add systems
	engine.addSystem(EntityManagementSystem(0, injectContext))
  engine.addSystem(MovementSystem(1, injectContext, gameMap))
	engine.addSystem(CollisionSystem(2))
  engine.addSystem(TimerSystem(4))
	engine.addSystem(BombExploderSystem(5, assetManager))
	engine.addSystem(RenderingSystem(10, viewport, batch))

  // Register signals
  engine.getSystem(TimerSystem::class.java).registerListener(engine.getSystem(BombExploderSystem::class.java))

	return engine
}
