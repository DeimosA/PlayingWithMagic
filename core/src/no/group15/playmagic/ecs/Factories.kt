package no.group15.playmagic.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.inject.Context
import ktx.math.ImmutableVector2
import no.group15.playmagic.ecs.components.CollisionComponent
import no.group15.playmagic.ecs.components.MovementComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.systems.*
import no.group15.playmagic.utils.assets.GameAssets


fun engineFactory(injectContext: Context, viewport: Viewport): Engine {
	val assetManager: AssetManager = injectContext.inject()
	val batch: SpriteBatch = injectContext.inject()
	val engine = PooledEngine()

	// Add entities
	// test entity
	val entity = engine.createEntity()
	val transform = engine.createComponent(TransformComponent::class.java)
	transform.position = Vector2()
	//transform.scale = ImmutableVector2(.8f, .8f)
	entity.add(transform)
	val texture = engine.createComponent(TextureComponent::class.java)
	texture.src = TextureRegion(assetManager.get<Texture>(GameAssets.BADLOGIC.desc.fileName))
	entity.add(texture)
	entity.add(engine.createComponent(MovementComponent::class.java))
	entity.add(engine.createComponent(CollisionComponent::class.java))
	engine.addEntity(entity)

	val gameMap = GameMap(assetManager)
	gameMap.makeEntities(engine)

	// Add systems
	engine.addSystem(InputEventSystem(0))
	engine.addSystem(MovementSystem(1, injectContext, gameMap))
	engine.addSystem(CollisionSystem(2))
	engine.addSystem(RenderingSystem(10, viewport, batch))

	injectContext.inject<InputMultiplexer>().addProcessor(engine.getSystem(InputEventSystem::class.java))

	return engine
}
