package com.wise.action;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * 人工审核处理
 *
 * @author lingyuwang
 * @date 2020-05-04 0:01
 * @since 1.0.9
 */
@Slf4j
public class ManualAuditAction implements Action<States, Events> {

	@Override
	public void execute(StateContext<States, Events> context) {
		AuditContent auditContent = context.getMessage().getHeaders().get("auditContent", AuditContent.class);

		log.info("人工审核参数:{}", auditContent);

		// 若包含敏感词则返回 false，表示人工审核不通过
		if (StringUtils.contains(auditContent.getTextContent(), "涉黄")) {
			auditContent.setManualAuditResult(false);
		} else {
			auditContent.setManualAuditResult(true);
		}
	}

}
