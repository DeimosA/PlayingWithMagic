package no.group15.playmagic.commandstream

import com.badlogic.gdx.utils.Pool


interface Command : Pool.Poolable {

	val type: Type

	/**
	 * Free the command to the pool if it has any
	 */
	fun free()

	enum class Type {
//		NONE { override var receiver: CommandReceiver? = null },
		CONFIG { override var receiver: CommandReceiver? = null }, // Server tells the client to configure itself
		MESSAGE { override var receiver: CommandReceiver? = null }, // Show the user a message
		SERVER_MESSAGE { override var receiver: CommandReceiver? = null }, // Message from the server to the client
		SPAWN_PLAYER { override var receiver: CommandReceiver? = null }, // Spawn a player
		REMOVE_PLAYER { override var receiver: CommandReceiver? = null }, // Remove a disconnected player
		RESET_GAME { override var receiver: CommandReceiver? = null }, // Reset game to its initial state
		MOVE { override var receiver: CommandReceiver? = null }, // Relative move command from user input
		POSITION { override var receiver: CommandReceiver? = null }, // Positions from other players
		SEND_POSITION { override var receiver: CommandReceiver? = null }, // Local player position sent to server
		DROP_BOMB { override var receiver: CommandReceiver? = null }, // Drop bomb command from user input
		BOMB_POSITION { override var receiver: CommandReceiver? = null }, // A different player dropped a bomb
		SEND_BOMB_POSITION { override var receiver: CommandReceiver? = null }, // Send position of dropped bomb to server
		KILL_PLAYER { override var receiver: CommandReceiver? = null }, // A player was killed
		SEND_KILL_PLAYER { override var receiver: CommandReceiver? = null }, // Send kill command to server
		DESTROY { override var receiver: CommandReceiver? = null }, // Destroy a destructible
		SEND_DESTROY { override var receiver: CommandReceiver? = null } // Send destroy command to server
		;
		abstract var receiver: CommandReceiver?
	}
}
