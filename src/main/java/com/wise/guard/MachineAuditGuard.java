package com.wise.guard;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

/**
 * 机审是否通过
 *
 * @author lingyuwang
 * @date 2020-05-03 23:56
 * @since 1.0.9
 */
public class MachineAuditGuard implements Guard<States, Events> {

	@Override
	public boolean evaluate(StateContext<States, Events> context) {
		AuditContent auditContent = context.getMessage().getHeaders().get("audtiContent", AuditContent.class);

		// 若包含敏感词则返回 false，表示机审不通过
		if (auditContent != null && StringUtils.contains(auditContent.getTextContent(), "涉政")) {
			return false;
		}

		return true;
	}

}
