package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.components.MovementComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.move

class InputEventSystem(priority: Int) : EntitySystem(priority), InputProcessor {
	private lateinit var entities: ImmutableArray<Entity>
	private val movementMapper = mapperFor<MovementComponent>()

	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(allOf(TransformComponent::class).get())
	}

	override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

	override fun mouseMoved(screenX: Int, screenY: Int) = false

	override fun keyTyped(character: Char) = false

	override fun scrolled(amount: Int) = false

	override fun keyUp(keycode: Int) = swap(keycode)

	override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

	override fun keyDown(keycode: Int): Boolean = swap(keycode)

	private fun swap(keycode: Int): Boolean {
		val movement = { e: Entity -> movementMapper.get(e) }

		return when (keycode) {
			Input.Keys.UP -> {
				entities.forEach { movement(it).moveUp = !movement(it).moveUp }
				true
			}
			Input.Keys.DOWN -> {
				entities.forEach { movement(it).moveDown = !movement(it).moveDown }
				true
			}
			Input.Keys.LEFT -> {
				entities.forEach { movement(it).moveLeft = !movement(it).moveLeft }
				true
			}
			Input.Keys.RIGHT -> {
				entities.forEach { movement(it).moveRight = !movement(it).moveRight }
				true
			}
			else -> false
		}
	}

	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

}
