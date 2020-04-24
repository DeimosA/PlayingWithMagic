package no.group15.playmagic.ecs

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import ktx.collections.*
import ktx.math.ImmutableVector2
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.entities.EntityFactory
import java.lang.RuntimeException
import kotlin.math.round


class GameMap {


	// --- PUBLIC INTERFACE ---

	fun width() = mapMatrix[0].size
	fun height() = mapMatrix.size

	fun willCollideX(worldPosX: Float, yBottom: Float, yTop: Float): Boolean {
		val matrixX = toMatrixCoordX(worldPosX)
		val matrixY1 = toMatrixCoordY(yBottom)
		val matrixY2 = toMatrixCoordY(yTop)
		return isRigidTile(matrixX, matrixY1) || isRigidTile(matrixX, matrixY2)
	}

	fun willCollideY(worldPosY: Float, xLeft: Float, xRight: Float): Boolean {
		val matrixY = toMatrixCoordY(worldPosY)
		val matrixX1 = toMatrixCoordX(xLeft)
		val matrixX2 = toMatrixCoordX(xRight)
		return isRigidTile(matrixX1, matrixY) || isRigidTile(matrixX2, matrixY)
	}

	fun makeEntities(engine: PooledEngine, assetManager: AssetManager) {
		val base = toWorldCoordinate(MatrixIndexes(0, 0))

		for ((y, row) in mapMatrix.withIndex()) {
			for ((x, cellType) in row.withIndex()) {

				val centerX = base.x + x
				val centerY = base.y - y

				val entity = when (cellType) {
					TileType.EMPTY, TileType.SPAWN, TileType.BROKEN_ROCK -> null
					TileType.WALL -> EntityFactory.makeEntity(assetManager, engine, EntityFactory.Type.WALL)
					TileType.DESTRUCTIBLE -> EntityFactory.makeEntity(assetManager, engine, EntityFactory.Type.ROCK)
				}

				if (entity != null) {
					val transform = entity.getComponent(TransformComponent::class.java)
					transform.boundingBox.setSize(1f)
					transform.setPosition(centerX, centerY)
				}
			}
		}
	}



	fun destroyRock(x: Float, y: Float) {
		val x = toMatrixCoordX(x)
		val y = toMatrixCoordY(y)

		if (mapMatrix[y][x] != TileType.DESTRUCTIBLE) {
			throw RuntimeException("The tile is not a rock, you should destroy only rocks.")
		}

		mapMatrix[y][x] = TileType.BROKEN_ROCK
	}


	// --- IMPLEMENTATION ---


	/**
	 * Translate the world coordinate for [worldPosX] to the matrix indexes.
	 */
	private fun toMatrixCoordX(worldPosX: Float): Int {
		val offset = -width() / 2f + 0.5f
		val relPos = worldPosX - offset

		return round(relPos).toInt()
	}

	/**
	 * Translate the world coordinate for [worldPosY] to the matrix indexes.
	 */
	private fun toMatrixCoordY(worldPosY: Float): Int {
		val offset = -height() / 2f + 0.5f
		val relPos = worldPosY - offset

		return height() - 1 - round(relPos).toInt()
	}

	/**
	 * Check if matrix coordinates is a rigid tile
	 */
	private fun isRigidTile(x: Int, y: Int): Boolean {
		val tile = mapMatrix[y][x]
		return tile == TileType.WALL || tile == TileType.DESTRUCTIBLE
	}

	/**
	 * Translate the matrix indexes to the world coordinate.
	 */
	private fun toWorldCoordinate(m: MatrixIndexes) = WorldCoordinate(m.x - width() / 2 + .5f, (height() - 1) / 2 - m.y + .5f)


	private class WorldCoordinate(
		var x: Float,
		var y: Float
	) {
		override fun toString() = "($x, $y)"
	}

	private class MatrixIndexes(
		var x: Int,
		var y: Int
	) {
		override fun toString() = "($x, $y)"
	}


	// --- MAP DATA ---

	enum class TileType {
		EMPTY, WALL, DESTRUCTIBLE, SPAWN, BROKEN_ROCK
	}

	private val o: TileType = TileType.EMPTY
	private val x: TileType = TileType.WALL
	private val d: TileType = TileType.DESTRUCTIBLE
	private val s: TileType = TileType.SPAWN

	private val mapMatrix: Array<Array<TileType>> = arrayOf(
		arrayOf(o, x, x, x, x, x, x, x, x, x, x, x, x, x),
		arrayOf(x, s, o, o, o, o, o, o, o, o, o, o, s, x),
		arrayOf(x, o, x, x, d, x, o, o, x, d, x, x, o, x),
		arrayOf(x, o, o, o, o, o, o, o, o, o, o, o, o, x),
		arrayOf(x, o, x, d, x, o, o, o, o, x, d, x, o, x),
		arrayOf(x, o, x, s, x, x, o, o, x, x, s, x, o, x),
		arrayOf(x, o, o, o, o, o, o, o, o, o, o, o, o, x),
		arrayOf(x, o, x, d, x, x, o, o, x, x, d, x, o, x),
		arrayOf(x, s, o, o, o, o, o, o, o, o, o, o, s, x),
		arrayOf(x, x, x, x, x, x, x, x, x, x, x, x, x, x)
	)

	private val spawnList: GdxArray<ImmutableVector2> by lazy {
		val array = gdxArrayOf<ImmutableVector2>()
		for ((y, row) in mapMatrix.withIndex()) {
			for ((x, tile) in row.withIndex()) {
				if (tile == TileType.SPAWN) {
					val position = toWorldCoordinate(MatrixIndexes(x, y))
					array.add(ImmutableVector2(position.x, position.y))
				}
			}
		}
		array.shrink()
		array.shuffle()
		array
	}

	fun getRandomSpawn(): ImmutableVector2 {
		return spawnList.pop()
	}

	fun returnSpawn(position: ImmutableVector2) {
		spawnList.add(position)
		spawnList.shuffle()
	}



	fun reset() {
		for (i in mapMatrix.indices) {
			for (j in mapMatrix[0].indices) {
				if (mapMatrix[i][j] == TileType.BROKEN_ROCK) {
					mapMatrix[i][j] = TileType.DESTRUCTIBLE
				}
			}
		}
	}
}
