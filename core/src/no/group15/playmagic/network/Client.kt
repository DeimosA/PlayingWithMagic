package no.group15.playmagic.network

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.SocketHints

class Client(
	host: String = "playmagic.norwayeast.cloudapp.azure.com",
	port: Int = 30715
) {

	private val socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host, port, SocketHints())


	init {
	    println("Connected to server: ${socket.remoteAddress}")
	}

}
