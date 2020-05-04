package com.wise.config;

import com.wise.action.ErrorAction;
import com.wise.action.MachineAuditAction;
import com.wise.action.MachineAuditPassedAction;
import com.wise.enums.Events;
import com.wise.enums.States;
import com.wise.guard.MachineAuditGuard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class StateMachineConfig
        extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config
            .withConfiguration().stateDoActionPolicy()
                // 是否自动启动初始状态
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states
            .withStates()
                .initial(States.PENDING)
                .choice(States.MACHINE_AUDIT)
                .choice(States.MANUAL_AUDIT)
                .end(States.DESTROY)
                    .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
            .withExternal()
                .source(States.PENDING).target(States.MACHINE_AUDIT).event(Events.MACHINE_AUDIT)
                .action(new MachineAuditAction(), new ErrorAction())
                // 若 guard 的 evaluate 返回 true 才会执行过渡
                .guard(guard())
                // 支持 SpEL 表达式
                // .guardExpression("true")
                .and()
//            .withExternal()
//                .source(States.MACHINE_AUDIT)
//                .first(States.MACHINE_AUDIT_PASSED, new MachineAuditGuard(), new MachineAuditPassedAction())
//                .last(ComplexFormStates.DEAL_FORM,new ComplexFormChoiceAction())
//                .source(States.MACHINE_AUDIT).target(States.S2).event(Events.E2)
//                .and()
            .withExternal()
                .source(States.DOWN).target(States.DESTROY).event(Events.DELETE_CONTENT);
    }

    @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }

    @Bean
    public Guard<States, Events> guard() {
        return (context) -> true;
    }

}