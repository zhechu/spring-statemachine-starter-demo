package com.wise.listener;

import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

/**
 * 监听器
 *
 * @author lingyuwang
 * @date 2020-05-06 23:00
 * @since 1.0.9
 */
@Component
@Slf4j
public class LogListener extends StateMachineListenerAdapter<States, Events> {

    @Override
    public void stateChanged(State<States, Events> from, State<States, Events> to) {
        log.info("State change to {}, this:{}", to.getId(), this);
    }

}
