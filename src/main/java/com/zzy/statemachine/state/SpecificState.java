package com.zzy.statemachine.state;

import com.zzy.statemachine.event.FlowEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;


public abstract class SpecificState {
	
	private static final Log LOG = LogFactory.getLog(SpecificState.class);
	
	protected Integer status;
	
	protected Map<String, SpecificState> transitions = new HashMap<String, SpecificState>();
	
	public abstract void run(StateOwner stateOwner);
	
	public SpecificState(Integer status){
//		throw new RuntimeException("SpecificState object cannot be insinated, because of the invalid constrator.");
	}
	
	public SpecificState next(FlowEvent e){
		LOG.debug(" transitions size = " + transitions.size());
	    if (transitions.containsKey(e.getName())){
	    	return (SpecificState) transitions.get(e.getName());
	    }else{
	    	throw new RuntimeException("Input not supported for current state "
	                                 + " state is " + this.getClass().getName()
	                                 + " Event is " + e.getName());
	    }
	}
	
	public void addEventState(String eventName, SpecificState specificState){
		transitions.put(eventName, specificState);
	}
	
}
