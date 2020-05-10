package com.wise.action;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * 机审处理
 *
 * @author lingyuwang
 * @date 2020-05-04 0:01
 * @since 1.0.9
 */
@Component
@Slf4j
public class MachineAuditAction implements Action<States, Events> {

	@Override
	public void execute(StateContext<States, Events> context) {
		AuditContent auditContent = context.getMessage().getHeaders().get("auditContent", AuditContent.class);

		log.info("机审参数:{}", auditContent);

		// 若包含敏感词则返回 false，表示机审不通过
		if (StringUtils.contains(auditContent.getTextContent(), "涉政")) {
			auditContent.setMachineAuditResult(false);
		} else {
			auditContent.setMachineAuditResult(true);
		}

		// 机审状态持久化
		auditContent.setStateCode(context.getTarget().getId().getCode());

		log.info("机审持久化状态:{}", auditContent);

		// TODO 落库
	}

}
