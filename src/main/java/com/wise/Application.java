package com.wise;

import com.wise.bean.AuditContent;
import com.wise.config.ContentAuditStateMachineBuilder;
import com.wise.enums.Events;
import com.wise.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private ContentAuditStateMachineBuilder contentAuditStateMachineBuilder;

    @Autowired
    @Qualifier("contentAuditPersister")
    private StateMachinePersister<States, Events, AuditContent> contentAuditPersister;

    @Override
    public void run(String... args) throws Exception {
//        machineAuditDemo();

//        manualAuditDemo();

        reUpDemo();
    }

    /**
     * 恢复上架示例
     *
     * @author lingyuwang
     * @date 2020-05-10 12:10
     * @since 1.0.9
     */
    private void reUpDemo() throws Exception {
        StateMachine<States,Events> stateMachine = contentAuditStateMachineBuilder.build();

        // 参数
        AuditContent auditContent = new AuditContent();
        auditContent.setContentId(1L);
//        auditContent.setTextContent("肯定涉政啊");
//        auditContent.setTextContent("肯定涉黄啊");
        auditContent.setTextContent("正常文本");

        // TODO 参数校验

        // TODO 从数据库获取当前状态
        auditContent.setStateCode(States.MACHINE_AUDIT_PASSED.getCode());

        // 恢复
        contentAuditPersister.restore(stateMachine, auditContent);

        Message<Events> message = MessageBuilder.withPayload(Events.MANUAL_AUDIT)
                .setHeader("auditContent", auditContent).build();
        stateMachine.sendEvent(message);

        // 异常处理
        if (stateMachine.getExtendedState().getVariables().containsKey("hasError")) {
            throw (RuntimeException)stateMachine.getExtendedState().getVariables().get("error");
        }

        log.info("人工审核后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.UP);

        log.info("上架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DOWN);

        log.info("下架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.RE_UP);

        log.info("恢复上架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DOWN);

        log.info("再次下架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DELETE_CONTENT);

        log.info("删除内容后状态:{}", stateMachine.getState());
    }

    /**
     * 人工审核示例
     *
     * @author lingyuwang
     * @date 2020-05-10 12:10
     * @since 1.0.9
     */
    private void manualAuditDemo() throws Exception {
        StateMachine<States,Events> stateMachine = contentAuditStateMachineBuilder.build();

        // 参数
        AuditContent auditContent = new AuditContent();
        auditContent.setContentId(1L);
//        auditContent.setTextContent("肯定涉政啊");
//        auditContent.setTextContent("肯定涉黄啊");
        auditContent.setTextContent("正常文本");

        // TODO 参数校验

        // TODO 从数据库获取当前状态
        auditContent.setStateCode(States.MACHINE_AUDIT_PASSED.getCode());

        // 恢复
        contentAuditPersister.restore(stateMachine, auditContent);

        Message<Events> message = MessageBuilder.withPayload(Events.MANUAL_AUDIT)
                .setHeader("auditContent", auditContent).build();
        stateMachine.sendEvent(message);

        // 异常处理
        if (stateMachine.getExtendedState().getVariables().containsKey("hasError")) {
            throw (RuntimeException)stateMachine.getExtendedState().getVariables().get("error");
        }

        log.info("人工审核后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.UP);

        log.info("上架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DOWN);

        log.info("下架后状态:{}", stateMachine.getState());

        stateMachine.sendEvent(Events.DELETE_CONTENT);

        log.info("删除内容后状态:{}", stateMachine.getState());
    }

    /**
     * 机审状态示例
     *
     * @author lingyuwang
     * @date 2020-05-10 12:05
     * @since 1.0.9
     */
    private void machineAuditDemo() throws Exception {
        StateMachine<States,Events> stateMachine = contentAuditStateMachineBuilder.build();

        // 参数
        AuditContent auditContent = new AuditContent();
        auditContent.setContentId(1L);
        auditContent.setStateCode(States.PENDING.getCode());
//        auditContent.setTextContent("肯定涉政啊");
//        auditContent.setTextContent("肯定涉黄啊");
        auditContent.setTextContent("正常文本");

        // TODO 参数校验

        // 恢复
        contentAuditPersister.restore(stateMachine, auditContent);

        // 查看恢复后状态机的状态
        log.info("恢复后状态:{}", stateMachine.getState());

        Message<Events> message = MessageBuilder.withPayload(Events.MACHINE_AUDIT)
                .setHeader("auditContent", auditContent).build();
        stateMachine.sendEvent(message);

        // 异常处理
        if (stateMachine.getExtendedState().getVariables().containsKey("hasError")) {
            throw (RuntimeException)stateMachine.getExtendedState().getVariables().get("error");
        }

        log.info("机审后状态:{}", stateMachine.getState());
    }

}