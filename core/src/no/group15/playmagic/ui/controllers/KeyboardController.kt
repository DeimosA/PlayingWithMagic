package no.group15.playmagic.ui.controllers

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.CommandDispatcher
import no.group15.playmagic.commandstream.commands.DropBombCommand
import no.group15.playmagic.commandstream.commands.MoveCommand
import kotlin.math.abs
import kotlin.math.sqrt


class KeyboardController(private val commandDispatcher: CommandDispatcher) : InputAdapter() {

	private var moveRight = false
	private var moveUp = false
	private var moveLeft = false
	private var moveDown = false

	private val diagonalScale = 1 / sqrt(2f)

	fun update(deltaTime: Float) {
		// If both keys on an axis are held down, movement is zero
		val x = (if (moveLeft) -1f else 0f) + (if (moveRight) 1f else 0f)
		val y = (if (moveDown) -1f else 0f) + (if (moveUp) 1f else 0f)
		if (x != 0f || y != 0f) {
			// Normalise if moving diagonally
			val scalar = if (abs(x) + abs(y) > 1f) diagonalScale else 1f
			val command = commandDispatcher.createCommand(Command.Type.MOVE) as MoveCommand
			command.x = x * scalar
			command.y = y * scalar
			commandDispatcher.send(command)
		}
	}

	private fun setKey(keycode: Int, active: Boolean): Boolean {
		return when (keycode) {
			Input.Keys.RIGHT, Input.Keys.D -> {
				moveRight = active
				true
			}
			Input.Keys.UP, Input.Keys.W -> {
				moveUp = active
				true
			}
			Input.Keys.LEFT, Input.Keys.A ->  {
				moveLeft = active
				true
			}
			Input.Keys.DOWN, Input.Keys.S -> {
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
//				commandDispatcher.send(commandDispatcher.createCommand(Command.Type.DROP_BOMB) as DropBombCommand)
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
