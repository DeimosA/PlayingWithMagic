package no.group15.playmagic.commands


class ResetGameCommand : Command {

	override val type: Command.Type = Command.Type.RESET_GAME


	override fun free() {
	}

	override fun reset() {
	}
}
