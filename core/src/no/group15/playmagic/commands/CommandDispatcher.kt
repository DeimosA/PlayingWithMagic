package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool


class CommandDispatcher : Disposable {

	private val moveCommandPool = object : Pool<MoveCommand>() {
		override fun newObject() = MoveCommand(this)
	}

	private val messageCommandPool = object : Pool<MessageCommand>() {
		override fun newObject() = MessageCommand(this)
	}


	/**
	 * Command factory of command [type]
	 */
	fun createCommand(type: Command.Type): Command? {
		return when (type) {
			Command.Type.NONE -> null
			Command.Type.CONFIG -> ConfigCommand()
			Command.Type.MESSAGE -> messageCommandPool.obtain()
			Command.Type.SPAWN_PLAYER -> SpawnPlayerCommand()
			Command.Type.REMOVE_PLAYER -> RemovePlayerCommand()
			Command.Type.RESET_MAP -> null
			Command.Type.MOVE -> moveCommandPool.obtain()
			Command.Type.POSITION -> PositionCommand()
			Command.Type.SEND_POSITION -> SendPositionCommand()
			Command.Type.DROP_BOMB -> DropBombCommand()
		}
	}

	/**
	 * Send [command] to receiver
	 */
	fun send(command: Command) {
		command.type.receiver?.receive(command) ?: command.free()
	}

	override fun dispose() {
		Command.Type.values().forEach { it.receiver = null }
		moveCommandPool.clear()
		messageCommandPool.clear()
	}
}
