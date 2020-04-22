package no.group15.playmagic.commands


interface CommandReceiver {

	/**
	 * Receive [command] from CommandDispatcher
	 */
	fun receive(command: Command)

}
