package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Pool


class CommandDispatcher {

	private val moveCommandPool = object : Pool<MoveCommand>() {
		override fun newObject() = MoveCommand(this)
	}


	fun createCommand(type: Command.Type): Command? {
		val command = when (type) {
			Command.Type.NONE -> null
			Command.Type.MESSAGE -> null
			Command.Type.SPAWN_PLAYER -> null
			Command.Type.REMOVE_PLAYER -> null
			Command.Type.RESET_MAP -> null
			Command.Type.MOVE -> moveCommandPool.obtain()
			Command.Type.POSITION -> null
			Command.Type.DROP_BOMB -> null
		}
//		command?.type = type
		return command
	}

	fun send(command: Command) {
		command.type.receiver?.receive(command)
	}
}
