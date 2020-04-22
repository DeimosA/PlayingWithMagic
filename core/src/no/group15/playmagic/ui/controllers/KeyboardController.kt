package no.group15.playmagic.ui.controllers

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import ktx.log.debug
import no.group15.playmagic.commands.Command
import no.group15.playmagic.commands.CommandDispatcher
import no.group15.playmagic.commands.MoveCommand


class KeyboardController(private val commandDispatcher: CommandDispatcher) : InputAdapter() {

	private var moveRight = false
	private var moveUp = false
	private var moveLeft = false
	private var moveDown = false


	fun update(deltaTime: Float) {
		val x = (if (moveLeft) -1f else 0f) + (if (moveRight) 1f else 0f)
		val y = (if (moveDown) -1f else 0f) + (if (moveUp) 1f else 0f)
		if (x != 0f || y != 0f) {
			val command = commandDispatcher.createCommand(Command.Type.MOVE) as MoveCommand
			command.x = x
			command.y = y
			commandDispatcher.send(command)
		}
	}

	private fun setKey(keycode: Int, active: Boolean): Boolean {
		return when (keycode) {
			Input.Keys.RIGHT -> {
				moveRight = active
				true
			}
			Input.Keys.UP -> {
				moveUp = active
				true
			}
			Input.Keys.LEFT ->  {
				moveLeft = active
				true
			}
			Input.Keys.DOWN -> {
				moveDown = active
				true
			}
			else -> {
				false
			}
		}
	}

	override fun keyDown(keycode: Int): Boolean {
		return when (keycode) {
			Input.Keys.SPACE -> {
				// TODO Drop bomb!
				debug { "plz drop bomb!" }
				true
			}
			else -> {
				setKey(keycode, true)
			}
		}
	}

	override fun keyUp(keycode: Int): Boolean {
		return setKey(keycode, false)
	}

	override fun keyTyped(character: Char): Boolean {
		return super.keyTyped(character)
	}
}
