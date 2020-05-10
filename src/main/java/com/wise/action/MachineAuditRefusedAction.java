package com.wise.action;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

/**
 * 机审拒绝后处理
 *
 * @author lingyuwang
 * @date 2020-05-04 0:01
 * @since 1.0.9
 */
@Component
@Slf4j
public class MachineAuditRefusedAction implements Action<States, Events> {

	@Override
	public void execute(StateContext<States, Events> context) {
		AuditContent auditContent = context.getMessage().getHeaders().get("auditContent", AuditContent.class);

		log.info("机审拒绝参数:{}", auditContent);

		// 机审状态持久化
		State<States, Events> target = context.getTarget();
		auditContent.setStateCode(target.getId().getCode());

		log.info("机审拒绝持久化状态:{}", auditContent);

		// TODO 落库
	}

}
