package no.group15.playmagic.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.inject.Context
import no.group15.playmagic.ecs.systems.*


fun engineFactory(injectContext: Context, viewport: Viewport): Engine {
	val assetManager: AssetManager = injectContext.inject()
	val batch: SpriteBatch = injectContext.inject()
	val engine = PooledEngine()

	val gameMap = GameMap()
	gameMap.makeEntities(engine, assetManager)

	// Add systems
	engine.addSystem(MovementSystem(1, injectContext, gameMap))
	engine.addSystem(CollisionSystem(2))
	engine.addSystem(RenderingSystem(10, viewport, batch))

	return engine
}
