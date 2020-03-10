package no.group15.playmagic.ecs

/**
 * Commands which the input events are mapped to for the different input events
 */

enum class Direction {
	UP, DOWN, LEFT, RIGHT
}

fun move(dir: Direction, pos: Pair<Int, Int>, step: Int = 10) = when (dir) {
	Direction.UP -> pos.copy(second = pos.second + step)
	Direction.DOWN -> pos.copy(second = pos.second - step)
	Direction.LEFT -> pos.copy(pos.first - 1)
	Direction.RIGHT -> pos.copy(pos.first + 1)
}
