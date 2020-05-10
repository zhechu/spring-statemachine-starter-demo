package com.wise.config;

import com.wise.bean.AuditContent;
import com.wise.enums.Events;
import com.wise.enums.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

@Configuration
public class PersistConfig {

	@Autowired
    private ContentAuditMachinePersist contentAuditMachinePersist;

	@Bean(name = "contentAuditPersister")
    public StateMachinePersister<States, Events, AuditContent> contentAuditPersister() {
		return new DefaultStateMachinePersister<>(contentAuditMachinePersist);
	}

}