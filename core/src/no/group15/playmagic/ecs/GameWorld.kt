package no.group15.playmagic.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.assets.*
import no.group15.playmagic.ecs.components.*
import no.group15.playmagic.ecs.systems.BombExploderSystem
import no.group15.playmagic.ecs.systems.MovementSystem
import no.group15.playmagic.ecs.systems.RenderingSystem
import no.group15.playmagic.ecs.systems.TimerSystem


fun engineFactory(viewport: Viewport, batch: SpriteBatch, assetManager: AssetManager): Engine {
	val engine = PooledEngine()

	// Add entities
	// test entity
	val entity = engine.createEntity()
	val transform = engine.createComponent(TransformComponent::class.java)
	transform.position.set(0f, 0f)
	transform.scale.set(2f, 2f)
	entity.add(transform)
	val texture = engine.createComponent(TextureComponent::class.java)
	texture.src = TextureRegion(assetManager.get<Texture>(TextureName.BADLOGIC.fileName))
	entity.add(texture)
	entity.add(engine.createComponent(MovementComponent::class.java))
	engine.addEntity(entity)

	testBomb(engine, assetManager)

	// Add systems
	engine.addSystem(MovementSystem(0, viewport))
	engine.addSystem(RenderingSystem(10, viewport, batch))

	return engine
}

fun loadGameAssets(assetManager: AssetManager) {
	enumValues<TextureName>().forEach {
		assetManager.load<Texture>(it.fileName)
	}
}

enum class TextureName(val fileName: String) {
	BADLOGIC("badlogic.jpg"),
	VIRTUAL_JOYSTICK("virtual_joystick.png"),
	BOMB( "bomb.png"),
	EXPLOSION( "explosion.png")
}



// BombExploder System Test code
fun testBomb(engine: PooledEngine, assetManager: AssetManager) {
	val bomb = createBomb(engine, assetManager)
	engine.addEntity(bomb)
	engine.addSystem(BombExploderSystem(0, assetManager))
	engine.addSystem(TimerSystem(0))
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
	transform.scale.set(2f, 2f)

	timer.timeLeft = 3f

	exploder.range = 5f

	texture.src = TextureRegion(assetManager.get<Texture>(TextureName.BOMB.fileName))

	entity.add(transform)
	entity.add(timer)
	entity.add(exploder)
	entity.add(texture)

	return entity
}
