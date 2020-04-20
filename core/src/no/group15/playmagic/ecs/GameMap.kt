package no.group15.playmagic.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.ImmutableVector2
import no.group15.playmagic.ecs.components.CollisionComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.utils.assets.GameAssets
import java.util.*
import kotlin.math.max
import kotlin.math.min


class GameMap (
	private val assetManager: AssetManager
) {

	private class Coordinate (
		var x: Float,
		var y: Float
	) {
		override fun toString() = "($x, $y)"
	}

	private enum class CellType {
		EMPTY, WALL, DESTRUCTIBLE
	}

	private val o: CellType =
		CellType.EMPTY
	private val x: CellType =
		CellType.WALL
	private val d: CellType =
		CellType.DESTRUCTIBLE

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

	//private val discreteWidth: Int = 18

	// TODO: write a public method that set this value
	private var topLeftCorner: Coordinate =
		Coordinate(-9f, 4f)



	fun overlappingWithWall(entity: Entity): Boolean {
		for (tile in nearTiles(entity)) {
			if (isRigidTile(tile) and overlapping(entity, tile)) {
				return true
			}
		}
		return false
	}



	// TODO: replace PooledEngine with an entity factory?
	fun makeEntities (engine: PooledEngine) {
		var offset = Coordinate(topLeftCorner.x, topLeftCorner.y)

		for ( (y, row) in mapMatrix.withIndex()) {
			for ( (x, cellType) in row.withIndex()) {

				offset.x = topLeftCorner.x + x
				offset.y = topLeftCorner.y - y

				//println("" + offset.x + ", " + offset.y)


				// TODO: move entity creation to entity factories
				when (cellType) {

					//CellType.EMPTY -> println("empty")

					CellType.WALL -> {
						var entity = engine.createEntity()

						var transform = engine.createComponent(TransformComponent::class.java)
						transform.position = ImmutableVector2(offset.x, offset.y)
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
						transform.position = ImmutableVector2(offset.x, offset.y)
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



	private fun nearTiles(entity: Entity): Iterable<Coordinate> {
		val list = LinkedList<Coordinate>()
		val center = Vector2()
		val entityPoint = boundingBox(entity).getCenter(center)
		for (i in max(0, entityPoint.x.toInt() - 1) until min(entityPoint.x.toInt() + 1, mapMatrix[0].size - 1) + 1) {
			for (j in max(0, entityPoint.y.toInt() - 1) until min(entityPoint.y.toInt() + 1, mapMatrix.size - 1) + 1) {
				list.add(Coordinate(i.toFloat(), j.toFloat()))
			}
		}
		return list
	}



	private fun boundingBox (entity: Entity): Rectangle {
		val transform = entity.getComponent(TransformComponent::class.java)
		val texture = entity.getComponent(TextureComponent::class.java)
		val coordinate = toMatrixIndexes(Coordinate(transform.position.x, transform.position.y))

		return Rectangle(
			coordinate.x,
			coordinate.y,
			texture.size.x * transform.scale.x,
			texture.size.y * transform.scale.y
		)
	}



	private fun toMatrixIndexes(coordinate: Coordinate): Coordinate {
		return Coordinate(coordinate.x - topLeftCorner.x, topLeftCorner.y - coordinate.y)
	}



	private fun isRigidTile(coordinate: Coordinate): Boolean {
		val x = coordinate.x.toInt()
		val y = coordinate.y.toInt()
		return mapMatrix[y][x] != CellType.EMPTY
	}



	private fun overlapping(entity: Entity, tile: Coordinate): Boolean {
		val entityBoundingBox = boundingBox(entity)
		val tileBoundingBox = Rectangle(tile.x, tile.y, 1f, 1f)

		return entityBoundingBox.overlaps(tileBoundingBox)
	}

}
