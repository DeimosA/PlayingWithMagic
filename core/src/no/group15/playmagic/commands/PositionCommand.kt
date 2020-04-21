package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Pool


open class PositionCommand() : Command {

	@Transient protected lateinit var pool: Pool<PositionCommand>

	override val type = Command.Type.POSITION

	var x = 0f
	var y = 0f
	var playerId = 0


	constructor(pool: Pool<PositionCommand>) : this() {
		this.pool = pool
	}

	override fun free() {
		pool.free(this)
	}

	override fun reset() {
		x = 0f
		y = 0f
		playerId = 0
	}
}

class PlayerPositionCommand() : PositionCommand() {

}
