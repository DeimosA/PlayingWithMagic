package no.group15.playmagic.levelmap

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import no.group15.playmagic.ecs.entities.EntityFactory

abstract class LevelMap (private val size: Pair<Int,Int>){
	lateinit var levelMap : Array<Array<Entity?>>

	/**
	 * Loads a map from a two dimensional array of Int, where 0 is no wall, 1 is breakable wall, 2 is hard wall and higher is spawn position of player nr n-2.
	 * hardWall is an array of textures associated with hard walls and will be randomly mapped with the hard walls in the map.
	 * wall is equivalent to hardWall, but associated with breakable walls.
	 *
	 */
	fun loadFromMap(wallMap: Array<Array<Int>>, hardWall:Array<TextureRegion>, wall:Array<TextureRegion>, engine: PooledEngine){
		wallMap.forEachIndexed{ i, row -> row.forEachIndexed{ j, tile ->
			when (tile){
				0 -> levelMap[i][j] = null
				1 -> levelMap[i][j] = EntityFactory.makeEntity(engine, EntityFactory.Type.ROCK)
				2 -> levelMap[i][j] = EntityFactory.makeEntity(engine, EntityFactory.Type.ROCK) //TODO add hardrock instead
				else -> {
					levelMap[i][j] =  EntityFactory.makeEntity(engine, EntityFactory.Type.PICKUP) // TODO add players instead
				}
 			}
		} }

	}
}
