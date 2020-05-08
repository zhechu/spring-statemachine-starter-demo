package com.wise;

import com.wise.bean.AuditContent;
import com.wise.config.ContentAuditStateMachineBuilder;
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
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Autowired
//    private StateMachine<States, Events> stateMachine;

//    @Autowired
//    StateMachineFactory<States, Events> factory;
//
//    @Override
//    public void run(String... args) {
//        StateMachine<States,Events> stateMachine = factory.getStateMachine();
//        stateMachine.start();
//
//        // 参数
//        AuditContent auditContent = new AuditContent();
//        auditContent.setContentId(1L);
////        auditContent.setTextContent("肯定涉政啊");
////        auditContent.setTextContent("肯定涉黄啊");
//        auditContent.setTextContent("正常文本");
//
//        Message<Events> message = MessageBuilder.withPayload(Events.MACHINE_AUDIT)
//                .setHeader("auditContent", auditContent).build();
//        stateMachine.sendEvent(message);
//
//        // 异常处理
//        if (stateMachine.getExtendedState().getVariables().containsKey("hasError")) {
//            throw (RuntimeException)stateMachine.getExtendedState().getVariables().get("error");
//        }
//
//        log.info("机审后状态:{}", stateMachine.getState());
//
//
//        // 另一个状态机
//        StateMachine<States,Events> stateMachine2 = factory.getStateMachine();
//        stateMachine2.start();
//
//        message = MessageBuilder.withPayload(Events.MACHINE_AUDIT)
//                .setHeader("auditContent", auditContent).build();
//        stateMachine2.sendEvent(message);
//
//        // 异常处理
//        if (stateMachine2.getExtendedState().getVariables().containsKey("hasError")) {
//            throw (RuntimeException)stateMachine2.getExtendedState().getVariables().get("error");
//        }
//
//        log.info("动态创建状态机的机审后状态:{}", stateMachine2.getState());
//
//
//        message = MessageBuilder.withPayload(Events.MANUAL_AUDIT)
//                .setHeader("auditContent", auditContent).build();
//        stateMachine.sendEvent(message);
//
//        log.info("人工审核后状态:{}", stateMachine.getState());
//
//        stateMachine.sendEvent(Events.UP);
//
//        log.info("上架后状态:{}", stateMachine.getState());
//
//        stateMachine.sendEvent(Events.DOWN);
//
//        log.info("下架后状态:{}", stateMachine.getState());
//
//        stateMachine.sendEvent(Events.DELETE_CONTENT);
//
//        log.info("删除内容后状态:{}", stateMachine.getState());
//    }

    @Autowired
    ContentAuditStateMachineBuilder contentAuditStateMachineBuilder;

    @Override
    public void run(String... args) throws Exception {
        StateMachine<States,Events> stateMachine = contentAuditStateMachineBuilder.build("1");

        stateMachine.getStateMachineAccessor()
                .doWithRegion(function -> function.addStateMachineInterceptor(new StateMachineInterceptorAdapter<States, Events>() {
                    @Override
                    public Exception stateMachineError(StateMachine<States, Events> sm, Exception exception) {
                        log.error("-------------------->异常", exception);
                        return exception;
                    }
                }));

        stateMachine.start();

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
            log.error("第一次异常", (RuntimeException)stateMachine.getExtendedState().getVariables().get("error"));
//            throw (RuntimeException)stateMachine.getExtendedState().getVariables().get("error");

            // 重置变量
            stateMachine.getExtendedState().getVariables().remove("hasError");
            stateMachine.getExtendedState().getVariables().remove("error");
        }

        log.info("机审后状态:{}", stateMachine.getState());


        // 另一个状态机
        StateMachine<States,Events> stateMachine2 = contentAuditStateMachineBuilder.build("2");
        stateMachine2.start();

        message = MessageBuilder.withPayload(Events.MACHINE_AUDIT)
                .setHeader("auditContent", auditContent).build();
        stateMachine2.sendEvent(message);

        // 异常处理
        if (stateMachine2.getExtendedState().getVariables().containsKey("hasError")) {
            throw (RuntimeException)stateMachine2.getExtendedState().getVariables().get("error");
        }

        log.info("动态创建状态机的机审后状态:{}", stateMachine2.getState());


        message = MessageBuilder.withPayload(Events.MANUAL_AUDIT)
                .setHeader("auditContent", auditContent).build();
        stateMachine.sendEvent(message);

        // 异常处理
        if (stateMachine.getExtendedState().getVariables().containsKey("hasError")) {
            log.error("第二次异常", (RuntimeException)stateMachine.getExtendedState().getVariables().get("error"));
//            throw (RuntimeException)stateMachine.getExtendedState().getVariables().get("error");
        }

        log.info("人工审核后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.UP);

        log.info("上架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DOWN);

        log.info("下架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DELETE_CONTENT);

        log.info("删除内容后状态:{}", stateMachine.getState());
    }

}