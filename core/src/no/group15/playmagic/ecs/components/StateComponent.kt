package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class StateComponent: Component, Pool.Poolable {
	lateinit var stateMap: Map<String, Int>

	lateinit var currentState: String

	//State to revert to when state is complete
	lateinit var defaultState: String

	fun setNewState(newState: String){
		currentState = if(stateMap.containsKey(newState)){
			newState
		}else{
			ktx.log.error {"Incorrect state given to setNewState of StateComponent"}
			defaultState
		}
	}

	override fun reset() {
		currentState = defaultState
	}
}
