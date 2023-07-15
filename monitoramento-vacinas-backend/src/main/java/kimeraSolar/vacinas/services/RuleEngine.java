package kimeraSolar.vacinas.services;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kimeraSolar.ruleEngineManagement.configurations.RuleEngineConfiguration;
import kimeraSolar.ruleEngineManagement.configurations.implementation.DroolsRuleEngineConfiguration;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;
import kimeraSolar.ruleEngineManagement.domain.WorkingMemory;
import kimeraSolar.ruleEngineManagement.services.ruleEngineServices.RuleEngineManagement;
import kimeraSolar.ruleEngineManagement.services.ruleEngineServices.implementation.DroolsRuleEngineManagement;
import kimeraSolar.ruleEngineManagement.services.workingMemoryServices.implementation.DefaultWorkingMemoryBuilder;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.AlertaRuleProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.DescarteRuleProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.FactDeclarationsProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.ManutencaoCamaraRuleProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.ManutencaoSensoresRuleProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.PerigoRuleProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.VariacaoBruscaTemperaturaRuleProvider;

@Component
public class RuleEngine {

    String startFileName;
    String startFileExtension;

    public RuleEngineManagement ruleEngineManagement = new MonitoramentoVacinaEngineManagement();
    final String pkgName = "kimeraSolar.vacinas.rules";
    final String baseName = "rules";
    final String sessionName = ".session";

    @Autowired
    private FactDeclarationsProvider factDeclarationsProvider;

    @Autowired
    private PerigoRuleProvider perigoRuleProvider;

    @Autowired
    private AlertaRuleProvider alertaRuleProvider;

    @Autowired
    private DescarteRuleProvider descarteRuleProvider;

    @Autowired
    private ManutencaoCamaraRuleProvider manutencaoCamaraRuleProvider;

    @Autowired
    private ManutencaoSensoresRuleProvider manutencaoSensoresRuleProvider;

    @Autowired
    private VariacaoBruscaTemperaturaRuleProvider variacaoBruscaTemperaturaRuleProvider;

    public class MonitoramentoVacinasBuilder extends DefaultWorkingMemoryBuilder{

        @Override
        public WorkingMemory build(WorkingMemoryConfigurations configurations) {

            configurations.setDefaultBaseName(baseName);
            configurations.setPkgName(pkgName);

            WorkingMemory workingMemory = new WorkingMemory(pkgName, baseName, sessionName);

            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

            List<RulePackage> rulePackages = getRulePackages();
            for(RulePackage rulePackage : rulePackages){
                StringBuilder fileNameBuilder = new StringBuilder();
                fileNameBuilder
                    .append("src/main/resources/")
                    .append(rulePackage.getPkgName().replace(".", "/"))
                    .append("/")
                    .append(rulePackage.getFileName())
                    .append(".drl");
                String fileName = fileNameBuilder.toString();

                kieFileSystem = kieFileSystem.write(fileName, rulePackage.getSourceCode());
                workingMemory.addFile(fileName, rulePackage);
            }

            kieFileSystem = kieFileSystem.writeKModuleXML(configurations.getConfigurations().toString());
            kieFileSystem = kieFileSystem.writePomXML(configurations.getPom().toString());

            workingMemory.setPom(configurations.getPom());
            workingMemory.setKmodule(configurations.getConfigurations());
            
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();

            KieModule kieModule = kieBuilder.getKieModule();
            KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

            workingMemory.setKieSession(kieContainer.newKieSession());
            workingMemory.setKieBase(workingMemory.getKieSession().getKieBase());

            workingMemory.setKieContainer(kieContainer);
            workingMemory.setReleaseId(kieModule.getReleaseId());

            return workingMemory;
        }

        public List<RulePackage> getRulePackages(){
            List<RulePackage> rulePackages = new ArrayList<>();

            rulePackages.add(factDeclarationsProvider.getRulePackage());

            rulePackages.add(perigoRuleProvider.getRulePackage());
            rulePackages.add(alertaRuleProvider.getRulePackage());
            rulePackages.add(descarteRuleProvider.getRulePackage());
            rulePackages.add(manutencaoCamaraRuleProvider.getRulePackage());
            rulePackages.add(variacaoBruscaTemperaturaRuleProvider.getRulePackage());
            rulePackages.add(manutencaoSensoresRuleProvider.getRulePackage());

            return rulePackages;
        }
        
    }

    public class MonitoramentoVacinaEngineManagement extends DroolsRuleEngineManagement{
        public MonitoramentoVacinaEngineManagement(){
            this.setBuilder(new MonitoramentoVacinasBuilder());
        }
    }

    public void startEngine(String[] args){

        RuleEngineConfiguration ruleEngineConfiguration = new DroolsRuleEngineConfiguration();
        ruleEngineConfiguration.setPkgName(pkgName);
        ruleEngineConfiguration.setRulesBaseName(baseName);
        ruleEngineConfiguration.setSessionName(sessionName);
        

        ruleEngineManagement.setConfigurations(ruleEngineConfiguration);
        ruleEngineManagement.resetRuleEngine();
    }

}
