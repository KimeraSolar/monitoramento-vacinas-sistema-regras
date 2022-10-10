package kimeraSolar.vacinas.services;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;

import kimeraSolar.ruleEngineManagement.configurations.RuleEngineConfiguration;
import kimeraSolar.ruleEngineManagement.configurations.implementation.DroolsRuleEngineConfiguration;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;
import kimeraSolar.ruleEngineManagement.domain.WorkingMemory;
import kimeraSolar.ruleEngineManagement.services.ruleEngineServices.RuleEngineManagement;
import kimeraSolar.ruleEngineManagement.services.ruleEngineServices.implementation.DroolsRuleEngineManagement;
import kimeraSolar.ruleEngineManagement.services.workingMemoryServices.implementation.DefaultWorkingMemoryBuilder;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

public class RuleEngine {

    static String startFileName;
    static String startFileExtension;

    static public RuleEngineManagement ruleEngineManagement = new MonitoramentoVacinaEngineManagement();
    static final String pkgName = "kimeraSolar.vacinas.rules";
    static final String baseName = "rules";
    static final String sessionName = ".session";

    public static class MonitoramentoVacinasBuilder extends DefaultWorkingMemoryBuilder{

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

            rulePackages.add(RegrasMonitoramento.getFactDeclarations());

            rulePackages.add(RegrasMonitoramento.getPerigoRules());
            rulePackages.add(RegrasMonitoramento.getAlertaRules());
            rulePackages.add(RegrasMonitoramento.getDescarteRules());
            rulePackages.add(RegrasMonitoramento.getManutencaoCamaraRules());
            rulePackages.add(RegrasMonitoramento.getVariacaoBruscaTempRules());
            rulePackages.add(RegrasMonitoramento.getManutencaoSensoresRules());

            return rulePackages;
        }
        
    }

    public static class MonitoramentoVacinaEngineManagement extends DroolsRuleEngineManagement{
        public MonitoramentoVacinaEngineManagement(){
            this.setBuilder(new MonitoramentoVacinasBuilder());
        }
    }

    static public void startEngine(String[] args){

        RuleEngineConfiguration ruleEngineConfiguration = new DroolsRuleEngineConfiguration();
        ruleEngineConfiguration.setPkgName(pkgName);
        ruleEngineConfiguration.setRulesBaseName(baseName);
        ruleEngineConfiguration.setSessionName(sessionName);
        

        ruleEngineManagement.setConfigurations(ruleEngineConfiguration);
        ruleEngineManagement.resetRuleEngine();
    }

}
