package no.group15.playmagic.commands


/**
 * Command from the server to the client
 */
class ServerMessageCommand() : Command {

	override val type: Command.Type = Command.Type.SERVER_MESSAGE

	var action: Action? = null


	constructor(action: Action) : this() {
		this.action = action
	}

	enum class Action {
		REJECTED,
		SHUTDOWN
	}

	override fun free() {
	}

	override fun reset() {
	}
}
