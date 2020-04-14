package no.group15.playmagic.desktop

import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import no.group15.playmagic.PlayMagic
import no.group15.playmagic.PlayMagicServer

object DesktopLauncher {
	@JvmStatic
	fun main(args: Array<String>) {
		// Use -headless command line argument to start as server only
		var headless = false
		for (arg in args) {
			if (arg == "-headless") {
				headless = true
			}
		}
		if (headless) {
			val config = HeadlessApplicationConfiguration()
			config.renderInterval -1
			HeadlessApplication(PlayMagicServer(), config)

		} else {
			val config = Lwjgl3ApplicationConfiguration()
			config.setTitle("Playing with Magic")
			config.setWindowedMode(1280, 720)
			Lwjgl3Application(PlayMagic(), config)
		}
	}
}
