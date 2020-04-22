package no.group15.playmagic.utils.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion


inline fun <reified E : Enum<E>> loadAssets(assetManager: AssetManager) {
	enumValues<E>().filter { it is AssetDesc }.forEach { it as AssetDesc
		assetManager.load(it.desc)
	}
}

fun textureRegionFactory(assetManager: AssetManager, asset: AssetDescRegion) : TextureRegion {
	return TextureRegion(
		assetManager.get<Texture>(asset.desc.fileName),
		asset.region.x, asset.region.y,
		asset.region.width, asset.region.height
	)
}
