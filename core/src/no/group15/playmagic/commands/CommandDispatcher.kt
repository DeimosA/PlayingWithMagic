package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Pool


class CommandDispatcher {

	private val moveCommandPool = object : Pool<MoveCommand>() {
		override fun newObject() = MoveCommand(this)
	}
	private val positionCommandPool = object : Pool<PositionCommand>() {
		override fun newObject() = PositionCommand(this)
	}


	/**
	 * Command factory of command [type]
	 */
	fun createCommand(type: Command.Type): Command? {
		val command = when (type) {
			Command.Type.NONE -> null
			Command.Type.MESSAGE -> null
			Command.Type.SPAWN_PLAYER -> null
			Command.Type.REMOVE_PLAYER -> null
			Command.Type.RESET_MAP -> null
			Command.Type.MOVE -> moveCommandPool.obtain()
			Command.Type.POSITION -> positionCommandPool.obtain()
			Command.Type.SEND_POSITION -> positionCommandPool.obtain() as PlayerPositionCommand // is this even gonna work?
			Command.Type.DROP_BOMB -> null
		}
		return command
	}

	/**
	 * Send [command] to receiver
	 */
	fun send(command: Command) {
		command.type.receiver?.receive(command) ?: command.free()
	}
}
