package no.group15.playmagic.commands

import no.group15.playmagic.ecs.GameMap


class ConfigCommand : Command {

	override val type: Command.Type = Command.Type.CONFIG

	var playerId = 0
	var tickRate = 1f
	var spawnPosX = 0f
	var spawnPosY = 0f
//	var gameMap: Array<Array<GameMap.TileType>>? = null


	override fun free() {
		// Not pooled
	}

	override fun reset() {
		// Not pooled
	}
}
