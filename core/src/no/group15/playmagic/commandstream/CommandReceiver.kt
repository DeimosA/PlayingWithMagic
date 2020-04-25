package no.group15.playmagic.commandstream


interface CommandReceiver {

	/**
	 * Receive [command] from CommandDispatcher
	 */
	fun receive(command: Command)

}
