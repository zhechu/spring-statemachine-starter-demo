package com.wise;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private StateMachine<States, Events> stateMachine;

    @Override
    public void run(String... args) {
        // 参数
        AuditContent auditContent = new AuditContent();
        auditContent.setContentId(1L);
//        auditContent.setTextContent("肯定涉政啊");
//        auditContent.setTextContent("肯定涉黄啊");
        auditContent.setTextContent("正常文本");

        Message<Events> message = MessageBuilder.withPayload(Events.MACHINE_AUDIT)
                .setHeader("auditContent", auditContent).build();
        stateMachine.sendEvent(message);

        // 异常处理
        if (stateMachine.getExtendedState().getVariables().containsKey("hasError")) {
            throw (RuntimeException)stateMachine.getExtendedState().getVariables().get("error");
        }

        log.info("机审后状态:{}", stateMachine.getState());

        message = MessageBuilder.withPayload(Events.MANUAL_AUDIT)
                .setHeader("auditContent", auditContent).build();
        stateMachine.sendEvent(message);

        log.info("人工审核后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.UP);

        log.info("上架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DOWN);

        log.info("下架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DELETE_CONTENT);

        log.info("删除内容后状态:{}", stateMachine.getState());
    }

}