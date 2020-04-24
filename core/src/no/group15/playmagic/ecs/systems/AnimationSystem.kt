package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.*
import no.group15.playmagic.ecs.components.AnimationComponent
import no.group15.playmagic.ecs.components.StateComponent
import no.group15.playmagic.ecs.components.TextureComponent


class AnimationSystem(
	priority: Int
) : EntitySystem (
	priority
) {
	private lateinit var entities: ImmutableArray<Entity>
	private val animationMapper = mapperFor<AnimationComponent>()
	private val textureMapper = mapperFor<TextureComponent>()
	private val stateMapper = mapperFor<StateComponent>()


	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(AnimationComponent::class, TextureComponent::class, StateComponent::class).get()
		)
	}

	override fun update(deltaTime: Float) {
		for (entity in entities) {
			val animator = animationMapper.get(entity)
			val texture = textureMapper.get(entity)
			val state = stateMapper.get(entity)

			if (state.stateChanged) {
				state.stateChanged = false
				animator.currentFrame = 0
				animator.lastSwitch = animator.frameSwitchDelta
			} else {
				animator.lastSwitch += deltaTime
			}

			if (animator.lastSwitch >= animator.frameSwitchDelta) {

				val stateIndex = state.stateMap[state.currentState]!!
				val framesLeft = animator.stateFrameCount[stateIndex] - (animator.currentFrame)
				// If there is more frames in the animation: animate them
				if (framesLeft > 0) {
					texture.src = animator.src[stateIndex][animator.currentFrame++]
					// Set last switch to when the switch should have happen (in case of delay)
					animator.lastSwitch -= animator.frameSwitchDelta

					if (state.revertState && framesLeft == 1) {
						animator.reset()
						state.reset()
					}
				}
			}
		}
	}
}
