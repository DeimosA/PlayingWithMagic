package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ktx.collections.gdxListOf
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.CommandReceiver
import no.group15.playmagic.commandstream.commands.MessageCommand


class MessagesWidget(
	private val margin: Float,
	private val font: BitmapFont
) : Widget, CommandReceiver {

	private val messages = gdxListOf<MessageCommand>()
	private val msgShowTime = 5f
	private val topLeftPos = Vector2()
	private val lineHeight = font.lineHeight * 1.2f


	init {
		Command.Type.MESSAGE.receiver = this
	}

	override fun update(deltaTime: Float) {
		for (msg in messages) {
			if (msg.timestamp > msgShowTime) {
				messages.remove()
				msg.free()
			}
			msg.timestamp += deltaTime
		}
	}

	override fun render(batch: SpriteBatch) {
		var lineNumber = 0
		messages.forEach { msg ->
			font.draw(batch, msg.text, topLeftPos.x, topLeftPos.y - lineHeight * lineNumber)
			lineNumber++
		}
	}

	override fun resize(width: Float, height: Float) {
		topLeftPos.set(margin, height - margin)
	}

	override fun dispose() {
		messages.forEach { it.free() }
		messages.clear()
	}

	override fun receive(command: Command) {
		if (command is MessageCommand) messages.add(command)
	}
}
