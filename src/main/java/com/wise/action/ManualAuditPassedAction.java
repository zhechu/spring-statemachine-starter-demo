package com.wise.action;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * 人工审核通过后处理
 *
 * @author lingyuwang
 * @date 2020-05-04 0:01
 * @since 1.0.9
 */
@Component
@Slf4j
public class ManualAuditPassedAction implements Action<States, Events> {

	@Override
	public void execute(StateContext<States, Events> context) {
		AuditContent auditContent = context.getMessage().getHeaders().get("auditContent", AuditContent.class);

		log.info("人工审核通过参数:{}", auditContent);

		// 机审状态持久化
		auditContent.setStateCode(States.MANUAL_AUDIT_PASSED.getCode());

		log.info("人工审核通过持久化状态:{}", auditContent);

		// TODO 落库
	}

}
