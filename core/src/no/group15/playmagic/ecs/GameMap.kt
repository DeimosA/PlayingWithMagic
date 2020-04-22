package no.group15.playmagic.ecs

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.ecs.entities.EntityFactory
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


class GameMap {


	// --- PUBLIC INTERFACE ---

	fun width() = mapMatrix[0].size
	fun height() = mapMatrix.size


	fun overlappingWithWall(rectangle: Rectangle): Boolean {
		val rectangleTile = toMatrixIndexes(WorldCoordinate(rectangle.getCenter(Vector2())))
		for (tile in nearTiles(rectangleTile)) {
			if (isRigidTile(tile) and overlapping(rectangle, tile)) {
				return true
			}
		}
		return false
	}


	fun willOverlapWithWall(rectangle: Rectangle, deltaX: Float, deltaY: Float): Boolean {
		rectangle.x += deltaX
		rectangle.y += deltaY

		val isOverlapping = overlappingWithWall(rectangle)

		//revert changes to object
		rectangle.x -= deltaX
		rectangle.y -= deltaY

		return isOverlapping
	}


	//TODO remove entity creation
	fun makeEntities(engine: PooledEngine, assetManager: AssetManager) {
		val base = toWorldCoordinate(MatrixIndexes(0, 0))
		val center = WorldCoordinate(0f, 0f)

		for ((y, row) in mapMatrix.withIndex()) {
			for ((x, cellType) in row.withIndex()) {

				center.x = base.x + x
				center.y = base.y - y

				val entity = when (cellType) {
					TileType.EMPTY, TileType.SPAWN -> null
					TileType.WALL -> EntityFactory.makeEntity(assetManager, engine, EntityFactory.Type.WALL)
					TileType.DESTRUCTIBLE -> EntityFactory.makeEntity(assetManager, engine, EntityFactory.Type.ROCK)
				}

				if (entity != null) {
					val transform = entity.getComponent(TransformComponent::class.java)
					transform.boundingBox.setCenter(center.x, center.y).setSize(1f, 1f)
					transform.position = transform.boundingBox.getCenter(transform.position)
				}
			}

		}

	}


	// --- IMPLEMENTATION ---


	private fun overlapping(rectangle: Rectangle, tile: MatrixIndexes): Boolean {
		val tile = toWorldCoordinate(tile)
		val tileBoundingBox = Rectangle()
		tileBoundingBox.height = 1f
		tileBoundingBox.width = 1f
		tileBoundingBox.setCenter(Vector2(tile.x, tile.y))

		return rectangle.overlaps(tileBoundingBox)
	}


	private fun isRigidTile(tile: MatrixIndexes): Boolean {
		val tile = mapMatrix[tile.y][tile.x]
		return tile == TileType.WALL || tile == TileType.DESTRUCTIBLE
	}

	/**
	 * Returns the map coordinate of the tiles around the
	 * entity. If the entity is the one marked with 'e' in the diagram
	 *     t t t
	 *     t e t
	 *     t t t
	 * the function return all the eight tiles marked with 't'
	 * plus the one marked with 'e'.
	 */
	private fun nearTiles(tile: MatrixIndexes): Iterable<MatrixIndexes> {
		val list = LinkedList<MatrixIndexes>()

		for (i in max(0, tile.x - 1)..min(tile.x + 1, width() - 1)) {
			for (j in max(0, tile.y - 1)..min(tile.y + 1, height() - 1)) {
				list.add(MatrixIndexes(i, j))
			}
		}

		return list
	}



	/**
	 * Translate the world coordinate to the matrix indexes.
	 */
	private fun toMatrixIndexes(c: WorldCoordinate): MatrixIndexes {
		val xFloor = floor(c.x).toInt()
		val yFloor = floor(c.y).toInt()

		return MatrixIndexes(xFloor + width() / 2, (height() - 1) / 2 - yFloor)
	}


	/**
	 * Translate the matrix indexes to the world coordinate.
	 */
	private fun toWorldCoordinate(m: MatrixIndexes) = WorldCoordinate(m.x - width() / 2 + .5f, (height() - 1) / 2 - m.y + .5f)



	private class WorldCoordinate(
		var x: Float,
		var y: Float
	) {
		constructor(vector: Vector2) : this(vector.x, vector.y)

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
		EMPTY, WALL, DESTRUCTIBLE, SPAWN
	}

	private val o: TileType = TileType.EMPTY
	private val x: TileType = TileType.WALL
	private val d: TileType = TileType.DESTRUCTIBLE
	private val s: TileType = TileType.SPAWN

	val mapMatrix: Array<Array<TileType>> = arrayOf(
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

}
