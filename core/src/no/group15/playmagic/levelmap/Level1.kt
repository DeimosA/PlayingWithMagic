package no.group15.playmagic.levelmap

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import no.group15.playmagic.ecs.TextureName

class Level1(assetManager: AssetManager, engine) : LevelMap(Pair<Int,Int>(20,20)){
	private val wallMap : Array<Array<Int>> = arrayOf(
		arrayOf(2,2,2,2,2, 2,2,2,2,2, 2,2,2,2,2, 2,2,2,2,2),
		arrayOf(2,3,0,0,0, 1,1,0,0,1, 1,0,0,1,1, 0,0,0,0,2),
		arrayOf(2,0,0,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,0,0,2),
		arrayOf(2,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,2),
		arrayOf(2,0,0,0,2, 2,0,0,0,0, 0,0,0,0,2, 2,0,0,0,2),

		arrayOf(2,0,0,0,2, 2,0,0,0,0, 0,0,0,0,2, 2,0,0,0,2),
		arrayOf(2,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,2),
		arrayOf(2,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,2),
		arrayOf(2,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,2),
		arrayOf(2,0,0,0,0, 0,0,0,0,2, 2,0,0,0,0, 0,0,0,0,2),

		arrayOf(2,0,0,0,0, 0,0,0,0,2, 2,0,0,0,0, 0,0,0,0,2),
		arrayOf(2,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,2),
		arrayOf(2,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,2),
		arrayOf(2,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,2),
		arrayOf(2,0,0,0,2, 2,0,0,0,0, 0,0,0,0,2, 2,0,0,0,2),

		arrayOf(2,0,0,0,2, 2,0,0,0,0, 0,0,0,0,2, 2,0,0,0,2),
		arrayOf(2,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,2),
		arrayOf(2,0,0,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,0,0,2),
		arrayOf(2,0,0,0,0, 1,1,0,0,1, 1,0,0,1,1, 0,0,0,4,2),
		arrayOf(2,2,2,2,2, 2,2,2,2,2, 2,2,2,2,2, 2,2,2,2,2)
	)
	init {
		val tileSet = assetManager.get<Texture>(TextureName.TILE_SET.fileName)
		val hardWall = arrayOf(
			TextureRegion(tileSet, 480, 380, 40,40),
			TextureRegion(tileSet, 520, 380, 40,40)
		)

		val softWall = arrayOf(TextureRegion(tileSet, 480, 192, 40,40))

		super.loadFromMap(this.wallMap, hardWall, softWall, engine)
	}
}
