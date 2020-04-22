package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool


class TextureComponent : Component, Pool.Poolable {

	lateinit var src: TextureRegion

	// Origin for scaling and rotation relative to size
	val origin = Vector2(0.5f, 0.5f)


	override fun reset() {
		origin.set(0.5f, 0.5f)
	}
}
