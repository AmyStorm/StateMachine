package com.zzy.statemachine.state;

import com.zzy.statemachine.context.StateMachineContext;

import java.util.List;


public class StateOwner {
	private List<MutableStateBean> mutableStateBeans;
	
	private SpecificState specificState;
	
	public StateOwner(List<MutableStateBean> mutableStateBeans, String specificStateName){
		this.mutableStateBeans =  mutableStateBeans;
		this.specificState = StateMachineContext.getState(specificStateName);
	}
	
	public List<MutableStateBean> getMutableStateBeans() {
	    return mutableStateBeans;
    }
	
	public SpecificState getSpecificState() {
		return specificState;
	}
	
}
