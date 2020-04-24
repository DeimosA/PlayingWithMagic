package no.group15.playmagic.commandstream.commands

import no.group15.playmagic.commandstream.Command


class DestroyCommand : Command {

	override val type: Command.Type = Command.Type.DESTROY

	var x = 0f
	var y = 0f


	override fun free() {
	}

	override fun reset() {
	}
}


class SendDestroyCommand : Command {

	override val type: Command.Type = Command.Type.SEND_DESTROY

	var x = 0f
	var y = 0f


	override fun free() {
	}

	override fun reset() {
	}
}
