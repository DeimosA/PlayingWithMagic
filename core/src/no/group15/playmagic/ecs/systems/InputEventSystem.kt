package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.toImmutable
import ktx.math.toMutable
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.move

class InputEventSystem(priority: Int) : EntitySystem(priority), InputProcessor {
	private lateinit var entities: ImmutableArray<Entity>
	private val transformMapper = mapperFor<TransformComponent>()

	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(allOf(TransformComponent::class).get())
	}

	override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

	override fun mouseMoved(screenX: Int, screenY: Int) = false

	override fun keyTyped(character: Char) = false

	override fun scrolled(amount: Int) = false

	override fun keyUp(keycode: Int) = false

	override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

	private fun updatePos(dir: Int, entity: Entity) {
		val transform = transformMapper.get(entity)
		transform.position = move(dir, transform.position)
	}

	override fun keyDown(keycode: Int): Boolean = when (keycode) {

		in setOf(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT) -> {
			entities.forEach {updatePos(keycode, it)}
			true
		}
		else -> false
	}

	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

}
