package com.zzy.statemachine;

import com.zzy.statemachine.context.StateMachineContext;
import com.zzy.statemachine.persist.StatePersist;
import com.zzy.statemachine.state.SpecificState;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by khaerothe on 2014/7/27.
 */
public class StateMachineFactory {

    private static final Log LOG = LogFactory.getLog(StateMachineFactory.class);

    private StateMachineFactory(){

    }

    public static StateMachine createStateMachine(String[] xmlPaths, StatePersist statePersist){
        if(xmlPaths != null){
            for(String xmlPath : xmlPaths){
                init(xmlPath, statePersist);
            }
        }else{
            throw new RuntimeException("xml path is null");
        }
        return null;
    }

    private static StateMachine init(String xmlPath, StatePersist statePersist){

        StateMachine StateMachine = new StateMachine(null, null, null, statePersist);
//        StateMachine StateMachine = new StateMachine(xmlPath, statePersist);
        SAXReader saxReader = new SAXReader();
        InputStream is;
        Document document = null;
        try {
            is = new FileInputStream(xmlPath);
        } catch (Exception e) {
            throw new RuntimeException("error to open " + xmlPath + " file.", e);
        }
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            throw new RuntimeException("error to parse xml file.", e);
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException("cannot close io-stream.", e);
            }
        }
        Map<String, SpecificState> stateMap = new HashMap<>();
        Map<String, Integer> stateValueMap = new HashMap<>();
        Element root = document.getRootElement();
        List<Element> states = root.selectNodes("/state-machine/state");
        for(Element state : states){
            String className = state.attributeValue("class");
            String id = state.attributeValue("id");
            String statusValue = state.attributeValue("value");
            Integer stateValueInt;
            if(StringUtils.isNotBlank(statusValue)){
                stateValueInt = Integer.valueOf(statusValue);
            }else{
                throw new RuntimeException("value in " + xmlPath + " file must be not blank.");
            }
            Class<?> stateClass;
            Constructor<?> constructor;
            Object instance;
            try {
                stateClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("class not found in " + xmlPath + " file.", e);
            }

            try {
                constructor = stateClass.getConstructor(Integer.class);
            } catch (Exception e) {
                throw new RuntimeException("cannot get the constructor method.", e);
            }
            try {
                instance = constructor.newInstance(Integer.valueOf(statusValue));
            } catch (Exception e) {
                throw new RuntimeException("cannot instantiate the class.", e);
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
        for(Element bean : beans){
            String beanRef = bean.attributeValue("name");
            String beanClass = bean.attributeValue("class");
            String start = bean.attributeValue("start");

            Class<?> classType;
            try {
                classType = Class.forName(beanClass);
            } catch (ClassNotFoundException e) {
                LOG.error("class:" + beanClass + " cannot found.", e);
                continue;
            }
            StateMachineContext.addMutableBeanType(beanRef, classType, start);
        }
        return StateMachine;
    }
}
