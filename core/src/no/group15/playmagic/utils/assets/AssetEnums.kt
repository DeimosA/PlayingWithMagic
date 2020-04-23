package no.group15.playmagic.utils.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.freetype.*


enum class GameAssets(override val desc: AssetDescriptor<out Any>) : AssetDesc {
	BADLOGIC(AssetDescriptor("badlogic.jpg", Texture::class.java)),
	// Should be a TextureAtlas, but the texture packer did not work for me
	PLAYER(AssetDescriptor("characterSheet.png", Texture::class.java)),
	WALL(AssetDescriptor("wall.png", Texture::class.java)),
	DESTRUCTIBLE_WALL(AssetDescriptor("destructible_wall.png", Texture::class.java))
}

enum class VirtualStickAssets(override val desc: AssetDescriptor<out Any>, override val region: Region) : AssetDescRegion {
	PAD_REGION(
		AssetDescriptor("virtual_joystick.png", Texture::class.java),
		Region(2, 2, 300, 300)
	),
	HANDLE_REGION(
		AssetDescriptor("virtual_joystick.png", Texture::class.java),
		Region(306, 2, 140, 140)
	)
}

enum class FontAssets(override val desc: AssetDescriptor<out Any>) : AssetDesc {
	DRAGONFLY_25(AssetDescriptor(
		"Dragonfly-25.ttf", BitmapFont::class.java,
		freeTypeFontParameters("fonts/Dragonfly-z9jl.ttf") {
			size = 25
		}
	)),
}

enum class MenuAssets(override val desc: AssetDescriptor<out Any>) : AssetDesc {
	HOVER_BACKGROUND(AssetDescriptor("menuhoverbackground.png", Texture::class.java)),
	SOUND_CLICK(AssetDescriptor("sounds/click.wav", Sound::class.java)),
	DRAGONFLY_59(AssetDescriptor(
		"Dragonfly-59.ttf", BitmapFont::class.java,
		freeTypeFontParameters("fonts/Dragonfly-z9jl.ttf") {
			size = 59
		}
	)),
	DRAGONFLY_70(AssetDescriptor(
		"Dragonfly-70.ttf", BitmapFont::class.java,
		freeTypeFontParameters("fonts/Dragonfly-z9jl.ttf") {
			size = 65
		}
	)),
	DRAGONFLY_120(AssetDescriptor(
		"Dragonfly-120.ttf", BitmapFont::class.java,
		freeTypeFontParameters("fonts/Dragonfly-z9jl.ttf") {
			size = 120
		}
	))
}
