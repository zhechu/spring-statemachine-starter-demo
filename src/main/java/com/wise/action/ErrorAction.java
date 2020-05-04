package com.wise.action;

import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * 错误后处理
 *
 * @author lingyuwang
 * @date 2020-05-04 0:01
 * @since 1.0.9
 */
@Slf4j
public class ErrorAction implements Action<States, Events> {

	@Override
	public void execute(StateContext<States, Events> context) {
		log.info("源状态:{}, 目标状态:{}, 事件:{}, 错误:{}",
				context.getSource(), context.getTarget(), context.getEvent(), context.getException().getMessage());
		context.getExtendedState().getVariables().put("hasError", true);
		context.getExtendedState().getVariables().put("error", context.getException());
	}

}
