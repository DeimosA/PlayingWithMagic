package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Pool


open class PositionCommand() : Command {

	override val type = Command.Type.POSITION

	open var x = 0f
	open var y = 0f
	open var playerId = 0


	override fun free() {
	}

	override fun reset() {
		x = 0f
		y = 0f
		playerId = 0
	}
}

class SendPositionCommand : PositionCommand() {

	override val type = Command.Type.SEND_POSITION

}
