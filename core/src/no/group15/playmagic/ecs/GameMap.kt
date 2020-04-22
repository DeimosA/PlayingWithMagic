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

//	fun willCollide(worldPosX: Float, worldPosY: Float): Boolean {
//		val matrixX = toMatrixCoordX(worldPosX)
//		val matrixY = toMatrixCoordY(worldPosY)
//		return isRigidTile(matrixX, matrixY)
//	}

	private fun toMatrixCoordX(worldPosX: Float): Int {
		val offset = -width() / 2f + 0.5f
		val relPos = worldPosX - offset

		return round(relPos).toInt()
	}
	private fun toMatrixCoordY(worldPosY: Float): Int {
		val offset = -height() / 2f + 0.5f
		val relPos = worldPosY - offset

		return height() - 1 - round(relPos).toInt()
	}

//	fun overlappingWithWall(rectangle: Rectangle): Boolean {
//		val rectangleTile = toMatrixIndexes(WorldCoordinate(rectangle.getCenter(Vector2())))
//		for (tile in nearTiles(rectangleTile)) {
//			if (isRigidTile(tile) and overlapping(rectangle, tile)) {
//				return true
//			}
//		}
//		return false
//	}


//	fun willOverlapWithWall(rectangle: Rectangle, deltaX: Float, deltaY: Float): Boolean {
//		rectangle.x += deltaX
//		rectangle.y += deltaY
//
//		val isOverlapping = overlappingWithWall(rectangle)
//
//		//revert changes to object
//		rectangle.x -= deltaX
//		rectangle.y -= deltaY
//
//		return isOverlapping
//	}


	//TODO remove entity creation
	fun makeEntities(engine: PooledEngine, assetManager: AssetManager) {
		val base = toWorldCoordinate(MatrixIndexes(0, 0))
//		val center = WorldCoordinate(0f, 0f)
		val mapHeight = height()

		for ((y, row) in mapMatrix.withIndex()) {
			for ((x, cellType) in row.withIndex()) {

				val centerX = base.x + x
				val centerY = base.y - y

				val entity = when (cellType) {
					CellType.EMPTY -> null
					CellType.WALL -> EntityFactory.makeEntity(assetManager, engine, EntityFactory.Type.WALL)
					CellType.DESTRUCTIBLE -> EntityFactory.makeEntity(assetManager, engine, EntityFactory.Type.ROCK)
				}

				if (entity != null) {
					val transform = entity.getComponent(TransformComponent::class.java)
					transform.boundingBox.setSize(1f)
					transform.setPosition(centerX, centerY)
//					transform.boundingBox.setCenter(center.x, center.y).setSize(1f, 1f)
//					transform.position = transform.boundingBox.getCenter(transform.position)
				}
			}

		}

	}


	// --- IMPLEMENTATION ---


//	private fun overlapping(rectangle: Rectangle, tile: MatrixIndexes): Boolean {
//		val tile = toWorldCoordinate(tile)
//		val tileBoundingBox = Rectangle()
//		tileBoundingBox.height = 1f
//		tileBoundingBox.width = 1f
//		tileBoundingBox.setCenter(Vector2(tile.x, tile.y))
//
//		return rectangle.overlaps(tileBoundingBox)
//	}


//	private fun isRigidTile(tile: MatrixIndexes): Boolean {
//		return mapMatrix[tile.y][tile.x] != CellType.EMPTY
//	}

	private fun isRigidTile(x: Int, y: Int): Boolean {
		return mapMatrix[y][x] != CellType.EMPTY
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
//	private fun nearTiles(tile: MatrixIndexes): Iterable<MatrixIndexes> {
//		val list = LinkedList<MatrixIndexes>()
//
//		for (i in max(0, tile.x - 1)..min(tile.x + 1, width() - 1)) {
//			for (j in max(0, tile.y - 1)..min(tile.y + 1, height() - 1)) {
//				list.add(MatrixIndexes(i, j))
//			}
//		}
//
//		return list
//	}



	/**
	 * Translate the world coordinate to the matrix indexes.
	 */
//	private fun toMatrixIndexes(c: WorldCoordinate): MatrixIndexes {
//		val xFloor = floor(c.x).toInt()
//		val yFloor = floor(c.y).toInt()
//
//		return MatrixIndexes(xFloor + width() / 2, (height() - 1) / 2 - yFloor)
//	}


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

	enum class CellType {
		EMPTY, WALL, DESTRUCTIBLE
	}

	private val o: CellType = CellType.EMPTY
	private val x: CellType = CellType.WALL
	private val d: CellType = CellType.DESTRUCTIBLE

	val mapMatrix: Array<Array<CellType>> = arrayOf(
		arrayOf(o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x),
		arrayOf(x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x),
		arrayOf(x, o, x, d, d, x, x, x, o, o, x, x, x, d, x, x, o, x),
		arrayOf(x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x),
		arrayOf(x, o, x, o, x, o, x, o, o, o, o, x, o, x, o, x, o, x),
		arrayOf(x, o, x, o, x, o, o, x, o, o, x, o, o, x, o, x, o, x),
		arrayOf(x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x),
		arrayOf(x, o, x, d, d, x, x, x, o, o, x, x, d, x, x, x, o, x),
		arrayOf(x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x),
		arrayOf(x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x)
	)

}
