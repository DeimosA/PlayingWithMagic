package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.inject.Context
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.CommandDispatcher
import no.group15.playmagic.commandstream.commands.DropBombCommand
import kotlin.math.pow


class ButtonsWidget(
	private val viewport: Viewport,
	buttonTexture: TextureRegion,
	private val size: Float,
	injectContext: Context
) : Widget {
	private val inputMultiplexer: InputMultiplexer = injectContext.inject()
	private val commandDispatcher: CommandDispatcher = injectContext.inject()
	private val buttonSprite = Sprite(buttonTexture)
	private val margin = 67f
	private val buttonCenter = Vector2()
	private val buttonRadius = size / 2
	private val buttonRadius2 = buttonRadius.pow(2)
	private val bombCoolDown = 3000 //ms
	private var previousBombDrop: Long = 0

	private val buttonInput = object : InputAdapter() {
		override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			val cursor = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
			if (buttonCenter.dst2(cursor) < buttonRadius2) {
				if (TimeUtils.millis() > previousBombDrop + bombCoolDown) {
					previousBombDrop = TimeUtils.millis()
					commandDispatcher.send(
						commandDispatcher.createCommand(Command.Type.DROP_BOMB) as DropBombCommand
					)
					buttonSprite.color = Color.DARK_GRAY
				}
				return true
			}
			return false
		}
	}


	init {
		buttonSprite.setSize(size, size)
		inputMultiplexer.addProcessor(buttonInput)
	}

	override fun update(deltaTime: Float) {
		if (TimeUtils.millis() > previousBombDrop + bombCoolDown) {
			buttonSprite.color = Color.WHITE
		}
	}

	override fun render(batch: SpriteBatch) {
		buttonSprite.draw(batch)
	}

	override fun resize(width: Float, height: Float) {
		buttonSprite.setPosition(viewport.worldWidth - size - margin, margin)
		buttonCenter.set(
			buttonSprite.x + buttonSprite.width / 2,
			buttonSprite.y + buttonSprite.height / 2
		)
	}

	override fun dispose() {
		inputMultiplexer.removeProcessor(buttonInput)
	}
}
