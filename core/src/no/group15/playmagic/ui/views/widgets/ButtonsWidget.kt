package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.inject.Context
import no.group15.playmagic.commands.Command
import no.group15.playmagic.commands.CommandDispatcher
import no.group15.playmagic.commands.DropBombCommand
import kotlin.math.pow


class ButtonsWidget(
	private val viewport: Viewport,
	buttonTexture: TextureRegion,
	size: Float,
	injectContext: Context
) : Widget {
	private val inputMultiplexer: InputMultiplexer = injectContext.inject()
	private val commandDispatcher: CommandDispatcher = injectContext.inject()
	private val buttonSprite = Sprite(buttonTexture)
	private val margin = 67f
	private val handleCenter = Vector2()
	private val buttonRadius = size / 2
	private val buttonRadius2 = buttonRadius.pow(2)
	private val cooldown = 3000 //ms
	private var millisPreviousBombDrop: Long = 0

	private val buttonInput = object : InputAdapter() {
		override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			val cursor = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
			if (handleCenter.dst2(cursor) < buttonRadius2) {
				if (System.currentTimeMillis() > millisPreviousBombDrop + cooldown) {
					commandDispatcher.send(
						commandDispatcher.createCommand(Command.Type.DROP_BOMB) as DropBombCommand
					)
					millisPreviousBombDrop = System.currentTimeMillis()
				}
				return true
			}
			return false
		}
	}


	init {
	    buttonSprite.setPosition(viewport.rightGutterWidth - size - margin, margin)
		buttonSprite.setSize(size, size)
		inputMultiplexer.addProcessor(buttonInput)
	}


	override fun update(deltaTime: Float) {
		//nothing?
	}

	override fun render(batch: SpriteBatch) {
		buttonSprite.draw(batch)
	}

	override fun resize(width: Float, height: Float) {
		handleCenter.set(
			buttonSprite.x + buttonSprite.width / 2,
			buttonSprite.y + buttonSprite.height / 2
		)
		//handleSprite.setCenter(handleCenter.x, handleCenter.y)
	}

	override fun dispose() {
		inputMultiplexer.removeProcessor(buttonInput)
	}
}
