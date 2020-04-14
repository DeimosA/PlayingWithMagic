package no.group15.playmagic.ecs

import ktx.math.ImmutableVector2
import com.badlogic.gdx.Input


/**
 * Commands which the input events are mapped to for the different input events
 */

fun move(dir: Int, pos: ImmutableVector2, step: Int = 10) = when (dir) {
	Input.Keys.UP -> pos.copy(y = pos.y + step)
	Input.Keys.DOWN -> pos.copy(y = pos.y - step)
	Input.Keys.LEFT -> pos.copy(pos.x - step)
	Input.Keys.RIGHT -> pos.copy(pos.x + step)
	else -> ImmutableVector2(0f, 0f)
}
