package no.group15.playmagic.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import ktx.ashley.*
import ktx.inject.Context
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.CommandDispatcher
import no.group15.playmagic.commandstream.commands.SendKillPlayerCommand
import no.group15.playmagic.ecs.components.ExploderComponent
import no.group15.playmagic.ecs.components.HealthComponent
import no.group15.playmagic.ecs.components.PlayerComponent
import no.group15.playmagic.ecs.components.StateComponent
import no.group15.playmagic.ecs.events.CollisionEvent


class HealthSystem(
	priority: Int,
	injectContext: Context
) : EntitySystem(
	priority
), Listener<CollisionEvent> {

	private val exploderMapper = mapperFor<ExploderComponent>()
	private val playerMapper = mapperFor<PlayerComponent>()
	private val stateMapper = mapperFor<StateComponent>()
	private val healthMapper = mapperFor<HealthComponent>()

	private val commandDispatcher: CommandDispatcher = injectContext.inject()


	override fun receive(signal: Signal<CollisionEvent>, event: CollisionEvent) {

		val bomb = if (event.entity1.has(exploderMapper)) event.entity1 else event.entity2
		val player = if (event.entity1.has(playerMapper)) event.entity1 else event.entity2

		val exploder = exploderMapper[bomb]
		if (exploder.isExploded) {
			val health = healthMapper[player]
			health.points -= exploder.damage
			if (health.points <= 0) {
				stateMapper[player].setNewState("DEAD_RIGHT", false)
				val command = commandDispatcher.createCommand(Command.Type.SEND_KILL_PLAYER) as SendKillPlayerCommand
				command.playerId = playerMapper[player].playerId
				player.remove<PlayerComponent>()
				commandDispatcher.send(command)
			}
		}
	}
}
