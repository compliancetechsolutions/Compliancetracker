package com.compliance.compliance.config;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * Drools configuration.
 *
 * <p>
 * <b>Critical fix over original:</b>
 * <ul>
 * <li>Original declared a {@code @Bean KieSession} with
 * {@code @Scope("prototype")} but then injected it into
 * {@link ComplianceRuleServiceImpl} as a singleton (Spring only respects
 * prototype scope when the injection target also requests a new instance, e.g.
 * via {@code ApplicationContext#getBean()} or a lookup method). The result was
 * a de-facto singleton {@code KieSession} shared across threads.</li>
 * <li><b>Fix</b>: Remove the {@code KieSession} bean entirely. {@link KieBase}
 * is thread-safe and is the correct shared singleton.
 * {@link ComplianceRuleServiceImpl} now creates a fresh {@code KieSession} per
 * call via {@code kieBase.newKieSession()} and disposes it in a finally
 * block.</li>
 * </ul>
 */
@Slf4j
@Configuration
public class DroolsConfig {

	@Bean
	public KieServices kieServices() {
		return KieServices.Factory.get();
	}

	@Bean
	public KieContainer kieContainer(KieServices kieServices) {
		log.info("[DROOLS] Initialising KieContainer from classpath");
		return kieServices.newKieClasspathContainer();
	}

	/**
	 * Thread-safe Drools knowledge base.
	 *
	 * <p>
	 * {@code KieBase} is immutable after construction and safe to share across
	 * threads. It is created once at startup from the {@code complianceRules}
	 * KieBase defined in {@code src/main/resources/META-INF/kmodule.xml}.
	 */
	@Bean
	public KieBase kieBase(KieContainer container) {
		log.info("[DROOLS] Loading KieBase 'complianceRules'");
		return container.getKieBase("complianceRules");
	}

	// NOTE: KieSession is NOT registered as a bean.
	// ComplianceRuleServiceImpl creates a new KieSession per call:
	// KieSession session = kieBase.newKieSession();
	// try { ... } finally { session.dispose(); }
	// This is the only correct pattern for concurrent use.
}
