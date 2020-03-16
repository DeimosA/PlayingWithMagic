package no.group15.playmagic.ui

interface AppState {

	/**
	 * Initialize the state. Use this instead of constructors
	 */
	fun create()

	/**
	 * Update the application with time step [deltaTime]
	 */
	fun update(deltaTime: Float)

	/**
	 * Application is resized. [width] and [height] defines the new resolution
	 */
	fun resize(width: Int, height: Int)

	/**
	 * Go back/escape
	 */
	fun back()

	/**
	 * Called when the application is paused
	 */
	fun pause()

	/**
	 * Resumes after a pause
	 */
	fun resume()

	/**
	 * Dispose of all resources from this state
	 */
	fun dispose()

}
