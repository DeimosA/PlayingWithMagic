package no.group15.playmagic.ecs

class NewTestGameMap: GameMap() {

	override val mapMatrix: Array<Array<TileType>> = arrayOf(
		arrayOf(o, x, x, x, x, x, x, x, x, x, x, x, x, x),
		arrayOf(x, s, o, d, o, o, o, o, o, o, o, o, d, x),
		arrayOf(x, o, x, x, d, x, o, o, x, d, x, x, o, x),
		arrayOf(x, o, o, o, o, o, s, d, o, o, o, o, o, x),
		arrayOf(x, o, x, d, x, o, o, o, o, x, d, x, o, x),
		arrayOf(x, o, x, s, x, x, o, o, x, x, s, x, d, x),
		arrayOf(x, d, o, o, o, o, o, o, o, o, d, o, o, x),
		arrayOf(x, o, x, d, x, x, o, o, x, s, d, x, o, x),
		arrayOf(x, d, o, d, o, o, o, o, o, o, o, o, s, x),
		arrayOf(x, x, x, x, x, x, x, x, x, x, x, x, x, x)
	)
}
