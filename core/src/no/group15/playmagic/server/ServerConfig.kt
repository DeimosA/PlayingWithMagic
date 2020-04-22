package no.group15.playmagic.server


class ServerConfig(
	val host: String? = null,
	val port: Int = 30715,
	val maxPlayers: Int = 5,
	val tickRate: Float = 30f
)
