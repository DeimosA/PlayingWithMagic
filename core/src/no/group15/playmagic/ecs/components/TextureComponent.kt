package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.ImmutableVector2


class TextureComponent : Component, Pool.Poolable {

	lateinit var src: TextureRegion

	// Origin for scaling and rotation relative to size
//	val origin = Vector2(0.4f, 0.4f)
	val origin = ImmutableVector2(0.0f, 0.0f)


	override fun reset() {
//		origin.set(0.4f, 0.4f)
	}
}
