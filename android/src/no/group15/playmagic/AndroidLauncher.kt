package no.group15.playmagic

import android.os.Bundle
import com.badlogic.gdx.Application
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val config = AndroidApplicationConfiguration()
		config.hideStatusBar = true
		config.useImmersiveMode = true
		initialize(PlayMagic(Application.LOG_INFO), config)
	}
}
