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
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@Slf4j
public class ContentAuditStateMachineBuilder {

	@Autowired
	private MachineAuditAction machineAuditAction;

	@Autowired
	private ErrorAction errorAction;

	@Autowired
	private MachineAuditPassedAction machineAuditPassedAction;

	@Autowired
	private MachineAuditRefusedAction machineAuditRefusedAction;

	@Autowired
	private ManualAuditAction manualAuditAction;

	@Autowired
	private ManualAuditPassedAction manualAuditPassedAction;

	@Autowired
	private ManualAuditRefusedAction manualAuditRefusedAction;

	@Autowired
	private LogListener logListener;

	@Autowired
	private MachineAuditGuard machineAuditGuard;

	@Autowired
	private ManualAuditGuard manualAuditGuard;

	public static final String MACHINE_ID = "content_audit";

	public StateMachine<States, Events> build() throws Exception {
		StateMachineBuilder.Builder<States, Events> builder = StateMachineBuilder.builder();

		builder.configureConfiguration()
				.withConfiguration()
				.machineId(MACHINE_ID)
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
					.action(machineAuditAction, errorAction)
					.and()
				.withChoice()
					.source(States.MACHINE_AUDIT)
					.first(States.MACHINE_AUDIT_PASSED, machineAuditGuard, machineAuditPassedAction, errorAction)
					.last(States.MACHINE_AUDIT_REFUSED, machineAuditRefusedAction, errorAction)
					.and()
				.withExternal()
					.source(States.MACHINE_AUDIT_PASSED).target(States.MANUAL_AUDIT).event(Events.MANUAL_AUDIT)
					.action(manualAuditAction, errorAction)
					.and()
				.withChoice()
					.source(States.MANUAL_AUDIT)
					.first(States.MANUAL_AUDIT_PASSED, manualAuditGuard, manualAuditPassedAction, errorAction)
					.last(States.MANUAL_AUDIT_REFUSED, manualAuditRefusedAction, errorAction)
					.and()
				.withExternal()
					.source(States.MANUAL_AUDIT_PASSED).target(States.UP).event(Events.UP)
					.and()
				.withExternal()
					.source(States.UP).target(States.DOWN).event(Events.DOWN)
					.and()
				.withExternal()
					.source(States.DOWN).target(States.UP).event(Events.RE_UP)
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

}