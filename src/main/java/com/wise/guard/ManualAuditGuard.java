package com.wise.guard;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

/**
 * 人工审核是否通过
 *
 * @author lingyuwang
 * @date 2020-05-03 23:56
 * @since 1.0.9
 */
public class ManualAuditGuard implements Guard<States, Events> {

	@Override
	public boolean evaluate(StateContext<States, Events> context) {
		AuditContent auditContent = context.getMessage().getHeaders().get("auditContent", AuditContent.class);

		if (auditContent != null) {
			return auditContent.getManualAuditResult();
		}

		return false;
	}

}
