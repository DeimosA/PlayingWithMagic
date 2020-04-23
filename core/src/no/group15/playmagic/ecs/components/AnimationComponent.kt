package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import kotlin.collections.HashSet


class AnimationComponent : Component, Pool.Poolable {

	// Current frame in the animation of the current state
	var currentFrame : Int = 0

	// Value at index of the any animation state is the number of frames to be rendered in the animation
	lateinit var stateFrameCount : Array<Int>

	// The animation state to frame map
	lateinit var src: Array<Array<TextureRegion>>

	// Time between each frame in the animation
	val frameSwitchDelta : Float = 0.2f

	// Time since last frame change
	var lastSwitch : Float = 0.0f

	override fun reset() {
		currentFrame = 0
	}
}
