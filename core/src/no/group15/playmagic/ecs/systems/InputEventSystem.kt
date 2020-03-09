package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.InputProcessor
import ktx.ashley.allOf
import no.group15.playmagic.ecs.components.MovementComponent

class InputEventSystem(priority: Int) : EntitySystem(priority), InputProcessor {
	private lateinit var entities: ImmutableArray<Entity>

	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(allOf(MovementComponent::class).get())
	}

	override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

	override fun mouseMoved(screenX: Int, screenY: Int) = false

	override fun keyTyped(character: Char) = false

	override fun scrolled(amount: Int) = false

	override fun keyUp(keycode: Int) = false

	override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

	override fun keyDown(keycode: Int): Boolean {
		TODO("Not yet implemented")
	}

	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
		TODO("Not yet implemented")
	}

}
