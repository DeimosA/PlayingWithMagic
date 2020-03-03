package no.group15.playmagic.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import no.group15.playmagic.PlayMagic

object DesktopLauncher {
	@JvmStatic
	fun main(arg: Array<String>) {
		val config = Lwjgl3ApplicationConfiguration()
		config.setTitle("Playing with Magic")
		config.setWindowedMode(1280, 720)
		Lwjgl3Application(PlayMagic(), config)
	}
}
