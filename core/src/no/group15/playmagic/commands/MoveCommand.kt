package no.group15.playmagic.commands

import com.badlogic.gdx.utils.Pool


class MoveCommand() : Command {

	@Transient private lateinit var pool: Pool<MoveCommand>
	override val type: Command.Type = Command.Type.MOVE

	var x = 0f
	var y = 0f


	constructor(pool: Pool<MoveCommand>) : this() {
		this.pool = pool
	}

	fun free() {
		pool.free(this)
	}

	override fun reset() {
		x = 0f
		y = 0f
	}
}
