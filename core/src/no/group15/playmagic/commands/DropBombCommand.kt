package no.group15.playmagic.commands

class DropBombCommand() : Command {
	override val type = Command.Type.DROP_BOMB

	override fun free() {
		// not pooled?
	}

	override fun reset() {
		// not pooled?
	}
}
