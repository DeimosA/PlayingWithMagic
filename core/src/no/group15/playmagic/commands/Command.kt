package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Pool


interface Command : Pool.Poolable {

	val type: Type


	fun free()

	enum class Type {
		NONE{ override var receiver: CommandReceiver? = null },
		MESSAGE{ override var receiver: CommandReceiver? = null },
		SPAWN_PLAYER{ override var receiver: CommandReceiver? = null },
		REMOVE_PLAYER{ override var receiver: CommandReceiver? = null },
		RESET_MAP{ override var receiver: CommandReceiver? = null },
		MOVE { override var receiver: CommandReceiver? = null }, // Relative move command from user input
		POSITION { override var receiver: CommandReceiver? = null }, // Positions from other players
		SEND_POSITION { override var receiver: CommandReceiver? = null }, // Local player position sent to server
		DROP_BOMB{ override var receiver: CommandReceiver? = null }
		;
		abstract var receiver: CommandReceiver?
	}
}
