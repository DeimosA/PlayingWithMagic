package no.group15.playmagic.ecs

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.math.ImmutableVector2
import no.group15.playmagic.ecs.components.CollisionComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TransformComponent
import no.group15.playmagic.utils.assets.GameAssets


class GameMap (
	private val assetManager: AssetManager
) {

	private class Coordinate (
		var x: Float,
		var y: Float
	)

	private enum class CellType {
		EMPTY, WALL, DESTRUCTIBLE
	}

	private val o: CellType =
		CellType.EMPTY
	private val x: CellType =
		CellType.WALL
	private val d: CellType =
		CellType.DESTRUCTIBLE

	private val mapMatrix: Array<CellType> = arrayOf(
		o, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x,
		x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x,
		x, o, x, d, d, x, x, x, o, o, x, x, x, d, x, x, o, x,
		x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x,
		x, o, x, o, x, o, x, o, o, o, o, x, o, x, o, x, o, x,
		x, o, x, o, x, o, o, x, o, o, x, o, o, x, o, x, o, x,
		x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x,
		x, o, x, d, d, x, x, x, o, o, x, x, d, x, x, x, o, x,
		x, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, x,
		x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x
	)

	private val discreteWidth: Int = 18

	// TODO: write a public method that set this value
	private var topLeftCorner: Coordinate =
		Coordinate(-9f, 4f)

	// TODO: replace PooledEngine with an entity factory?
	fun makeEntities (engine: PooledEngine) {
		var offset = Coordinate(topLeftCorner.x, topLeftCorner.y)

		for ( (i, cellType) in mapMatrix.withIndex()) {
			offset.x = topLeftCorner.x + (i % discreteWidth)
			offset.y = topLeftCorner.y - (i / discreteWidth)

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
