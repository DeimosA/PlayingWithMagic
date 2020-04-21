package no.group15.playmagic.commands


class SpawnPlayerCommand() : Command {

	override val type: Command.Type = Command.Type.SPAWN_PLAYER

	var playerId = 0
	var posX = 0f
	var posY = 0f


	constructor(id: Int, x: Float, y: Float) : this() {
		playerId = id
		posX = x
		posY = y
	}

	override fun free() {
		// Not pooled
	}

	override fun reset() {
		// Not pooled
	}
}
