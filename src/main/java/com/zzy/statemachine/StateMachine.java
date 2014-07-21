package com.zzy.statemachine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zzy.statemachine.context.StateMachineContext;
import com.zzy.statemachine.event.FlowEvent;
import com.zzy.statemachine.persist.StatePersist;
import com.zzy.statemachine.state.MutableStateBean;
import com.zzy.statemachine.state.SpecificState;
import com.zzy.statemachine.state.StateOwner;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class StateMachine {
	private static final Log LOG = LogFactory.getLog(StateMachine.class);

    private StatePersist statePersist;

    private String xmlPath;

    public StateMachine(String xmlPath, StatePersist statePersist){
        this.xmlPath = xmlPath;
        this.statePersist = statePersist;
    }

	public void init(){
		SAXReader saxReader = new SAXReader();
		InputStream is = null;
		Document document = null;
		try {
	        is = new FileInputStream(xmlPath);
        } catch (Exception e) {
        	LOG.error("error to open state-config.xml file.", e);
        	return;
        }
		try {
	        document = saxReader.read(is);
        } catch (DocumentException e) {
        	LOG.error("error to parse xml file.", e);
        	return;
        }finally{
        	if(is != null){
        		try {
	                is.close();
                } catch (IOException e) {
                	LOG.error("cannot close iostream.", e);
                	return;
                }
        	}
        }
		Map<String, SpecificState> stateMap = new HashMap<String, SpecificState>();
		Map<String, Integer> stateValueMap = new HashMap<String, Integer>();
		Element root = document.getRootElement();
		List<Element> states = root.selectNodes("/state-machine/state");
		for(Element state : states){
			String className = state.attributeValue("class");
			String id = state.attributeValue("id");
			String statusValue = state.attributeValue("value");
			Integer stateValueInt = null;
			if(StringUtils.isNotBlank(statusValue)){
				stateValueInt = Integer.valueOf(statusValue);
			}else{
            	LOG.error("value in state-config.xml file must be not blank.");
            	return;
			}
			Class<?> stateClass = null;
			Constructor<?> constructor = null;
			Object instance = null;
			try {
	            stateClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
            	LOG.error("class not found in state-config.xml file.", e);
            	return;
            }
			
			try {
	            constructor = stateClass.getConstructor(Integer.class);
            } catch (Exception e) {
            	LOG.error("cannot get the constructor method.", e);
            	return;
            }
			try {
	            instance = constructor.newInstance(Integer.valueOf(statusValue));
            } catch (Exception e) {
            	LOG.error("cannot instinate the class.", e);
            	return;
            }

			stateMap.put(id, (SpecificState) instance);
			stateValueMap.put(id, stateValueInt);
		}
		
		for(String stateId : stateMap.keySet()){
			List<Element> refBeans = root.selectNodes("/state-machine/state[@id='" + stateId + "']/next-state");
			if(refBeans != null && !refBeans.isEmpty()){
				for(Element refBean : refBeans){
					Attribute event = refBean.attribute("event");
					Attribute ref = refBean.attribute("ref");
					String refName = ref.getValue();
					SpecificState stateInstance = stateMap.get(stateId);
					stateInstance.addEventState(event.getValue(), stateMap.get(refName));
				}
			}
			StateMachineContext.addState(stateId, stateMap.get(stateId), stateValueMap.get(stateId));
		}
		List<Element> beans = root.selectNodes("/state-machine/bean");
//		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for(Element bean : beans){
//			String path = pack.attributeValue("package").replace('.', '/');  
//			Enumeration<URL> resources = null;
//	        try {
//	            resources = classLoader.getResources(path);
//            } catch (IOException e) {
//            	LOG.error("annotation scan occured error.", e);
//            	return;
//            }
			String beanRef = bean.attributeValue("name");
			String beanClass = bean.attributeValue("class");
			String start = bean.attributeValue("start");
			
			Class<?> classType = null;
			try {
	           classType = Class.forName(beanClass);
            } catch (ClassNotFoundException e) {
            	LOG.error("class:" + beanClass + " cannot found.", e);
            	continue;
            }
			StateMachineContext.addMutableBeanType(beanRef, classType, start);
		}

	}
	
	public final void transition(StateOwner stateOwner, FlowEvent e) {
		SpecificState currentState = stateOwner.getSpecificState();
		currentState = currentState.next(e);
		if (currentState != null) {
			currentState.run(stateOwner);
			saveCurrentState(stateOwner);
		}
//		SpecificStatus currentState = readCurrentState(taskid); // 从数据库获得当前状态
//		StateOwner stateOwner = new StateOwner(taskid, currentState);
//		// 转换状态
//		currentState = currentState.next(e);
//		if (currentState != null) {
//			currentState.run(stateOwner);
//			saveCurrentState(stateOwner); // 保存当前状态
//		}
	}
	
	public final void prepareStart(StateOwner stateOwner) {
		SpecificState currentState = stateOwner.getSpecificState();
		if (currentState != null) {
			currentState.run(stateOwner);
			if(stateOwner != null && stateOwner.getMutableStateBeans() != null){
				List<MutableStateBean> beans = stateOwner.getMutableStateBeans();
				for(MutableStateBean bean : beans){
                    statePersist.persistState(bean);
				}
				
			}else{
				throw new RuntimeException("stateOwner or mutableStateBean is null, cannot persist.");
			}
		}
//		SpecificStatus currentState = readCurrentState(taskid); // 从数据库获得当前状态
//		StateOwner stateOwner = new StateOwner(taskid, currentState);
//		// 转换状态
//		currentState = currentState.next(e);
//		if (currentState != null) {
//			currentState.run(stateOwner);
//			saveCurrentState(stateOwner); // 保存当前状态
//		}
	}
	
	private void saveCurrentState(StateOwner stateOwner){
		if(stateOwner != null && stateOwner.getMutableStateBeans() != null){
			List<MutableStateBean> beans = stateOwner.getMutableStateBeans();
			for(MutableStateBean bean : beans){
                //TODO this is a merge operation.
                statePersist.persistState(bean);
			}
			
		}else{
			throw new RuntimeException("stateOwner or mutableStateBean is null, cannot persist.");
		}
	}

}
