package com.wise.action;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

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

	private AtomicInteger atomicInteger = new AtomicInteger(0);

	@Override
	public void execute(StateContext<States, Events> context) {
	    // TODO 状态检查

		AuditContent auditContent = context.getMessage().getHeaders().get("auditContent", AuditContent.class);

		log.info("机审参数:{}", auditContent);

		// 若包含敏感词则返回 false，表示机审不通过
		if (StringUtils.contains(auditContent.getTextContent(), "涉政")) {
			auditContent.setMachineAuditResult(false);
		} else {
			auditContent.setMachineAuditResult(true);
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}

		if (atomicInteger.getAndIncrement() % 2 == 0) {
			throw new RuntimeException("测试运行时异常");
		}
	}

}
