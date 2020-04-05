package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import no.group15.playmagic.ecs.TextureName
import no.group15.playmagic.ecs.components.ExploderComponent
import no.group15.playmagic.ecs.components.TextureComponent
import no.group15.playmagic.ecs.components.TimerComponent
import no.group15.playmagic.ecs.components.TransformComponent


class BombExploderSystem(
	priority: Int,
	private val assetManager: AssetManager
): EntitySystem(
	priority
) {

	private lateinit var entities: ImmutableArray<Entity>
	private val timer = mapperFor<TimerComponent>()
	private val texture = mapperFor<TextureComponent>()
	private val transform = mapperFor<TransformComponent>()
	private val exploder = mapperFor<ExploderComponent>()

	override fun addedToEngine (engine: Engine) {
		entities = engine.getEntitiesFor(
			allOf(ExploderComponent::class, TimerComponent::class, TransformComponent::class, TextureComponent::class).get()
		)
	}

	override fun update(deltaTime: Float) {
		var explosionTexture: TextureRegion = TextureRegion(assetManager.get<Texture>(TextureName.EXPLOSION.fileName))

		for (entity in entities) {
			if (entity[timer]!!.timeLeft <= 0) {

				entity[texture]!!.src = explosionTexture
				entity[exploder]!!.isExploded = true

			}
		}
	}
}

