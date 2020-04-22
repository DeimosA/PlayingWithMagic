package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Pool


class MoveCommand() : Command {

	@Transient private var pool: Pool<MoveCommand>? = null

	override val type = Command.Type.MOVE

	var x = 0f
	var y = 0f


	constructor(pool: Pool<MoveCommand>) : this() {
		this.pool = pool
	}

	override fun free() {
		pool?.free(this)
	}

	override fun reset() {
		x = 0f
		y = 0f
	}
}
