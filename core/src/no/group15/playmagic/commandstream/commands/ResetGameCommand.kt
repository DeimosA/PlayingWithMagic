package no.group15.playmagic.commandstream.commands

import no.group15.playmagic.commandstream.Command


class ResetGameCommand : Command {

	override val type: Command.Type = Command.Type.RESET_GAME


	override fun free() {
	}

	override fun reset() {
	}
}
