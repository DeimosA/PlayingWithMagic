package no.group15.playmagic.commandstream.commands

import no.group15.playmagic.commandstream.Command


class KillPlayerCommand : Command {

	override val type: Command.Type = Command.Type.KILL_PLAYER

	var playerId = 0

	override fun free() {
	}

	override fun reset() {
	}
}


class SendKillPlayerCommand : Command {

	override val type: Command.Type = Command.Type.SEND_KILL_PLAYER

	var playerId = 0

	override fun free() {
	}

	override fun reset() {
	}
}
