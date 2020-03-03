package no.group15.playmagic.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import no.group15.playmagic.PlayMagic

object DesktopLauncher {
	@JvmStatic
	fun main(arg: Array<String>) {
		val config = LwjglApplicationConfiguration()
		config.title = "Playing with Magic"
		LwjglApplication(PlayMagic(), config)
	}
}
