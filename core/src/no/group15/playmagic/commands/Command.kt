package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Pool


interface Command : Pool.Poolable {

	val type: Type


	enum class Type {
		NONE{ override var receiver: CommandReceiver? = null },
		MESSAGE{ override var receiver: CommandReceiver? = null },
		SPAWN_PLAYER{ override var receiver: CommandReceiver? = null },
		REMOVE_PLAYER{ override var receiver: CommandReceiver? = null },
		RESET_MAP{ override var receiver: CommandReceiver? = null },
		MOVE { override var receiver: CommandReceiver? = null },
		POSITION{ override var receiver: CommandReceiver? = null },
		DROP_BOMB{ override var receiver: CommandReceiver? = null }
		;
		abstract var receiver: CommandReceiver?
	}
}
