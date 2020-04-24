package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class StateComponent: Component, Pool.Poolable {
	lateinit var stateMap: Map<String, Int>

	lateinit var currentState: String

	var stateChanged = false

	//State to revert to when state is complete
	lateinit var defaultState: String
	var revertState = true

	fun setNewState(newState: String, revert: Boolean = true){
		currentState = if(stateMap.containsKey(newState)){
			stateChanged = true
			revertState = revert
			newState
		}else{
			ktx.log.error {"Incorrect state given to setNewState of StateComponent"}
			defaultState
		}
	}

	override fun reset() {
		currentState = defaultState
		revertState = true
	}
}
