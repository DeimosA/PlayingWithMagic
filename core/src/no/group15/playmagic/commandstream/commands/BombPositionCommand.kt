package no.group15.playmagic.commandstream.commands

import no.group15.playmagic.commandstream.Command


/**
 * Someone dropped a bomb at this position
 */
class BombPositionCommand : Command {

	override val type: Command.Type = Command.Type.BOMB_POSITION

	var x = 0f
	var y = 0f


	override fun free() {
	}

	override fun reset() {
	}
}

/**
 * Send dropped bomb position to server
 */
class SendBombPositionCommand : Command {

	override val type: Command.Type = Command.Type.SEND_BOMB_POSITION

	var x = 0f
	var y = 0f


	override fun free() {
	}

	override fun reset() {
	}
}
