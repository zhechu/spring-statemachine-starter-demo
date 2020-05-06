package com.wise.config;


import com.wise.action.*;
import com.wise.enums.Events;
import com.wise.enums.States;
import com.wise.guard.MachineAuditGuard;
import com.wise.guard.ManualAuditGuard;
import com.wise.listener.LogListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@Slf4j
public class ContentAuditStateMachineBuilder {

	@Autowired
	private ErrorAction errorAction;

	@Autowired
	private LogListener logListener;

	public StateMachine<States, Events> build(String machineId) throws Exception {
		StateMachineBuilder.Builder<States, Events> builder = StateMachineBuilder.builder();

		builder.configureConfiguration()
				.withConfiguration()
				.machineId(machineId)
				// 是否自动启动初始状态
				.autoStartup(true)
				.listener(logListener);

		builder.configureStates()
				.withStates()
				.initial(States.PENDING)
				.choice(States.MACHINE_AUDIT)
				.choice(States.MANUAL_AUDIT)
				.end(States.DESTROY)
				.states(EnumSet.allOf(States.class));

		builder.configureTransitions()
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

		return builder.build();
	}

//	@Bean
	public Guard<States, Events> guard() {
		return (context) -> true;
	}

}