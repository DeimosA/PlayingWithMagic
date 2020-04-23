package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.*
import ktx.log.info
import no.group15.playmagic.ecs.components.AnimationComponent
import no.group15.playmagic.ecs.components.StateComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TransformComponent


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
		super.update(deltaTime)
		for (entity in entities) {
			val animator = animationMapper.get(entity)
			val texture = textureMapper.get(entity)
			val state = stateMapper.get(entity)

			animator.lastSwitch += deltaTime
			// Switch as many times as one should have since last update.
			while (animator.lastSwitch > animator.frameSwitchDelta) {
				// If there is more frames in the animation: animate them
				if (animator.currentFrame < animator.stateFrameCount[state.stateMap[state.currentState]!!]-1) {
					texture.src = animator.src[state.stateMap[state.currentState]!!][++animator.currentFrame]
					// Set last switch to when the switch should have happen (in case of delay)
					animator.lastSwitch -= animator.frameSwitchDelta

				} else {    // If there are no more frames: reset state.
					animator.reset()
					state.reset()
				}
			}
		}
	}
}
