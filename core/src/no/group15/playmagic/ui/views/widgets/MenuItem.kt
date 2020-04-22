package no.group15.playmagic.ui.views.widgets

interface MenuItem : Widget {

	fun contains(x: Float, y: Float): Boolean

	fun hover(x: Float, y: Float)

	fun click(x: Float, y: Float)

	fun reposition(x: Float, y: Float)

	enum class Alignment {
		LEFT,
		CENTER,
		RIGHT
	}
}
