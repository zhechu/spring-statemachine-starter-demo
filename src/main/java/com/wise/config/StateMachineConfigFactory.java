package com.wise.config;

import com.wise.action.*;
import com.wise.enums.Events;
import com.wise.enums.States;
import com.wise.guard.MachineAuditGuard;
import com.wise.guard.ManualAuditGuard;
import com.wise.listener.LogListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.EnumSet;

//@Configuration
//@EnableStateMachineFactory
@Slf4j
public class StateMachineConfigFactory
		extends EnumStateMachineConfigurerAdapter<States, Events> {

	@Autowired
	private ErrorAction errorAction;

	@Autowired
	private LogListener logListener;

	@Override
	public void configure(StateMachineConfigurationConfigurer<States, Events> config)
			throws Exception {
		config
				.withConfiguration()
				// 是否自动启动初始状态
				.autoStartup(true)
				.listener(logListener);
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
				.action(new MachineAuditAction(), errorAction)
				// 若 guard 的 evaluate 返回 true 才会执行过渡
				.guard(guard())
				// 支持 SpEL 表达式
				// .guardExpression("true")
				.and()
				.withChoice()
				.source(States.MACHINE_AUDIT)
				.first(States.MACHINE_AUDIT_PASSED, new MachineAuditGuard(), new MachineAuditPassedAction(), errorAction)
				.last(States.MACHINE_AUDIT_REFUSED, new MachineAuditRefusedAction(), errorAction)
				.and()
				.withExternal()
				.source(States.MACHINE_AUDIT_PASSED).target(States.MANUAL_AUDIT).event(Events.MANUAL_AUDIT)
				.action(new ManualAuditAction(), errorAction)
				.and()
				.withChoice()
				.source(States.MANUAL_AUDIT)
				.first(States.MANUAL_AUDIT_PASSED, new ManualAuditGuard(), new ManualAuditPassedAction(), errorAction)
				.last(States.MANUAL_AUDIT_REFUSED, new ManualAuditRefusedAction(), errorAction)
				.and()
				.withExternal()
				.source(States.MANUAL_AUDIT_PASSED).target(States.UP).event(Events.UP)
				.and()
				.withExternal()
				.source(States.UP).target(States.DOWN).event(Events.DOWN)
				.and()
				.withExternal()
				.source(States.DOWN).target(States.DESTROY).event(Events.DELETE_CONTENT)
				.and()
				.withExternal()
				.source(States.MANUAL_AUDIT_REFUSED).target(States.DESTROY).event(Events.DELETE_CONTENT)
				.and()
				.withExternal()
				.source(States.MACHINE_AUDIT_REFUSED).target(States.DESTROY).event(Events.DELETE_CONTENT)
				.and()
				.withExternal()
				.source(States.DOWN).target(States.DESTROY).event(Events.DELETE_CONTENT);
	}

//	@Bean
	public Guard<States, Events> guard() {
		return (context) -> true;
	}

}