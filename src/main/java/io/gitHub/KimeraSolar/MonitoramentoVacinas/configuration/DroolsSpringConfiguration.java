package io.gitHub.KimeraSolar.MonitoramentoVacinas.configuration;

import org.kie.internal.io.ResourceFactory;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsSpringConfiguration {
    
    private final KieServices kieServices = KieServices.Factory.get();
    private final KieSession kieSession;

    public DroolsSpringConfiguration(){
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/Monitoramento.drl"));

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();

        KieBaseConfiguration config = kieServices.newKieBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);

        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        KieBase kieBase = kieContainer.newKieBase( config );
        kieSession = kieBase.newKieSession();
    }

    @Bean
    public KieSession getKieSession(){
        return kieSession;
    }
}
