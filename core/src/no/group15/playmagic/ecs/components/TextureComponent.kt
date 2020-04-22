package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import ktx.math.ImmutableVector2


class TextureComponent : Component, Pool.Poolable {

	lateinit var src: TextureRegion

	// Origin for scaling and rotation relative to size
	val origin = ImmutableVector2(0.5f, 0.5f)


	override fun reset() {
	}
}
