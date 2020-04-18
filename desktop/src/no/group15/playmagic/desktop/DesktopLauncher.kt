package no.group15.playmagic.desktop

import com.badlogic.gdx.Application
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import no.group15.playmagic.PlayMagic
import no.group15.playmagic.PlayMagicServer

object DesktopLauncher {
	@JvmStatic
	fun main(args: Array<String>) {
		var headless = false
		var logLevel = Application.LOG_INFO
		for (arg in args) {
			// Use -headless command line argument to start as server only
			if (arg == "-headless") {
				headless = true
			} else if (arg == "-debuglog") {
				logLevel = Application.LOG_DEBUG
			}
		}

		if (headless) {
			val config = HeadlessApplicationConfiguration()
			config.renderInterval = -1f
			HeadlessApplication(PlayMagicServer(logLevel), config)

		} else {
			val config = Lwjgl3ApplicationConfiguration()
			config.setTitle("Playing with Magic")
			config.setWindowedMode(1280, 720)
			Lwjgl3Application(PlayMagic(logLevel), config)
		}
	}
}
