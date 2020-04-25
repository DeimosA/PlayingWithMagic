package no.group15.playmagic.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.*
import ktx.inject.Context
import no.group15.playmagic.ecs.components.DestructibleComponent
import no.group15.playmagic.ecs.components.ExploderComponent
import no.group15.playmagic.ecs.components.PickupComponent
import no.group15.playmagic.ecs.components.PlayerComponent
import no.group15.playmagic.ecs.systems.*


fun engineFactory(injectContext: Context, viewport: Viewport): Engine {
	val assetManager: AssetManager = injectContext.inject()
	val batch: SpriteBatch = injectContext.inject()
	val engine = PooledEngine()
	val gameMap = GameMap()

	// Add systems
	engine.addSystem(EntityManagementSystem(0, injectContext, gameMap))
	engine.addSystem(MovementSystem(1, injectContext, gameMap))
	engine.addSystem(CollisionSystem(2))
	engine.addSystem(TimerSystem(4))
	engine.addSystem(BombExploderSystem(5, injectContext))
	engine.addSystem(HealthSystem(6, injectContext))
	engine.addSystem(RockDropSystem(6, assetManager))
	engine.addSystem(PickUpSystem(7))
	engine.addSystem(AnimationSystem(9))
	engine.addSystem(RenderingSystem(10, viewport, batch))

	// Register signals
	engine.getSystem(TimerSystem::class.java).registerListener(engine.getSystem(BombExploderSystem::class.java))

	engine.getSystem(CollisionSystem::class.java).registerListener(
		allOf(PlayerComponent::class).get(),
		allOf(ExploderComponent::class).get(),
		engine.getSystem(HealthSystem::class.java)
	)

	engine.getSystem(CollisionSystem::class.java).registerListener(
		allOf(DestructibleComponent::class).get(),
		allOf(ExploderComponent::class).get(),
		engine.getSystem(EntityManagementSystem::class.java)
	)

	engine.getSystem(CollisionSystem::class.java).registerListener(
		allOf(PlayerComponent::class).get(),
		allOf(PickupComponent::class).get(),
		engine.getSystem(PickUpSystem::class.java)
	)

	engine.getSystem(CollisionSystem::class.java).registerListener(
		allOf(ExploderComponent::class).get(),
		allOf(DestructibleComponent::class).get(),
		engine.getSystem(RockDropSystem::class.java)
	)


	return engine
}
