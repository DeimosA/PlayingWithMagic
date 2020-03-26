package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.collections.gdxListOf


class MessagesWidget(
	private val margin: Float,
	private val font: BitmapFont
) : Widget {

	private class Message : Pool.Poolable {
		var text = ""
		var timestamp = 0f
		fun set(text: String, timestamp: Float) {
			this.text = text
			this.timestamp = timestamp
		}
		override fun reset() {
			text = ""
			timestamp = 0f
		}
	}

	private val msgPool = object : Pool<Message>(3) {
		override fun newObject() = Message()
	}
	private val messages = gdxListOf<Message>()
	private var currentTime = 0f
	private val msgShowTime = 5f
	private val topLeftPos = Vector2()
	private val lineHeight = font.lineHeight * 1.2f


	init {
	    // add test messages
		val message = msgPool.obtain()
		message.set("Hello world", currentTime)
		messages.add(message)
		val msg2 = msgPool.obtain()
		msg2.set("Player joined", currentTime + 2f)
		messages.add(msg2)
	}

	override fun update(deltaTime: Float) {
		for (msg in messages) {
			if (msg.timestamp + msgShowTime <= currentTime) {
				messages.remove()
				msgPool.free(msg)
			}
		}
		currentTime += deltaTime
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
		messages.forEach { msgPool.free(it) }
		messages.clear()
	}
}
