package no.group15.playmagic.commandstream.commands

import no.group15.playmagic.commandstream.Command


class PositionCommand : Command {

	override val type = Command.Type.POSITION

	var x = 0f
	var y = 0f
	var playerId = 0


	override fun free() {
	}

	override fun reset() {
	}
}


class SendPositionCommand() : Command {

	override val type = Command.Type.SEND_POSITION

	var x = 0f
	var y = 0f
	var playerId = 0


	constructor(x: Float, y: Float, id: Int): this() {
		this.x = x
		this.y = y
		playerId = id
	}

	override fun free() {
	}

	override fun reset() {
	}
}
