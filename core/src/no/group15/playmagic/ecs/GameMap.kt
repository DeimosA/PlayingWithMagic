package no.group15.playmagic.ecs

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import no.group15.playmagic.ecs.components.CollisionComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.utils.assets.GameAssets
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


class GameMap(
	private val assetManager: AssetManager
) {


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
	fun makeEntities(engine: PooledEngine) {
		var base = toWorldCoordinate(MatrixIndexes(0, 0))
		var center = WorldCoordinate(0f, 0f)

		for ((y, row) in mapMatrix.withIndex()) {
			for ((x, cellType) in row.withIndex()) {

				center.x = base.x + x
				center.y = base.y - y

				when (cellType) {

					//CellType.EMPTY -> println("empty")

					CellType.WALL -> {
						var entity = engine.createEntity()

						var transform = engine.createComponent(TransformComponent::class.java)
						transform.boundingBox.setCenter(center.x, center.y).setSize(1f, 1f)
						transform.position = transform.boundingBox.getCenter(transform.position)
						entity.add(transform)

						var texture = engine.createComponent(TextureComponent::class.java)
						texture.src = TextureRegion(assetManager.get<Texture>(GameAssets.WALL.desc.fileName))
						entity.add(texture)

						entity.add(engine.createComponent(CollisionComponent::class.java))

						engine.addEntity(entity)
					}

					CellType.DESTRUCTIBLE -> {
						var entity = engine.createEntity()

						var transform = engine.createComponent(TransformComponent::class.java)
						transform.boundingBox.setCenter(center.x, center.y).setSize(1f, 1f)
						transform.position = transform.boundingBox.getCenter(transform.position)
						entity.add(transform)

						var texture = engine.createComponent(TextureComponent::class.java)
						texture.src = TextureRegion(assetManager.get<Texture>(GameAssets.DESTRUCTIBLE_WALL.desc.fileName))
						entity.add(texture)

						entity.add(engine.createComponent(CollisionComponent::class.java))

						engine.addEntity(entity)
					}

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
		return mapMatrix[tile.y][tile.x] != CellType.EMPTY
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
		var xFloor = floor(c.x).toInt()
		var yFloor = floor(c.y).toInt()

		return MatrixIndexes(xFloor + width() / 2, (height() - 1) / 2 - yFloor)
	}


	/**
	 * Translate the matrix indexes to the world coordinate.
	 */
	private fun toWorldCoordinate(m: MatrixIndexes): WorldCoordinate {
		return WorldCoordinate(m.x - width() / 2 + .5f, (height() - 1) / 2 - m.y + .5f)
	}

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

	private enum class CellType {
		EMPTY, WALL, DESTRUCTIBLE
	}

	private val o: CellType = CellType.EMPTY
	private val x: CellType = CellType.WALL
	private val d: CellType = CellType.DESTRUCTIBLE

	private val mapMatrix: Array<Array<CellType>> = arrayOf(
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
