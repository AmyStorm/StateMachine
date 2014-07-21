package com.zzy.statemachine.persist;

import com.zzy.statemachine.state.MutableStateBean;

/**
 * Created by khaerothe on 2014/7/21.
 */
public interface StatePersist {

    void persistState(MutableStateBean bean);
}
