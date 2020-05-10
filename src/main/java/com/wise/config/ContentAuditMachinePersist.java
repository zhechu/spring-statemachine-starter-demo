package com.wise.config;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

@Component
public class ContentAuditMachinePersist implements StateMachinePersist<States, Events, AuditContent> {

	@Override
	public void write(StateMachineContext<States, Events> context, AuditContent contextObj) {
		// do nothing
	}

	@Override
	public StateMachineContext<States, Events> read(AuditContent contextObj) {
		StateMachineContext<States, Events> result = new DefaultStateMachineContext<>(
				States.getByCode(contextObj.getStateCode()),
				null, null, null, null, ContentAuditStateMachineBuilder.MACHINE_ID);
		return result;
	}
}