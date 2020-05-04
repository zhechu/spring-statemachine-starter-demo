package com.wise.action;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * 机审处理
 *
 * @author lingyuwang
 * @date 2020-05-04 0:01
 * @since 1.0.9
 */
@Slf4j
public class MachineAuditAction implements Action<States, Events> {

	@Override
	public void execute(StateContext<States, Events> context) {
		AuditContent auditContent = context.getMessage().getHeaders().get("audtiContent", AuditContent.class);

		log.info("机审参数:{}", auditContent);
	}

}