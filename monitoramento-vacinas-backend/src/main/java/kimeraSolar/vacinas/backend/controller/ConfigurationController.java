package kimeraSolar.vacinas.backend.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kimeraSolar.vacinas.backend.schemas.ConfigurationSchema;
import kimeraSolar.vacinas.services.RuleEngine;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.ManutencaoCamaraRuleProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.ManutencaoSensoresRuleProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.PerigoRuleProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.PkgNameProvider;
import kimeraSolar.vacinas.services.configurations.RegrasMonitoramento.VariacaoBruscaTemperaturaRuleProvider;

@RestController
@RequestMapping("/vacinas/configurations")
public class ConfigurationController {
    Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    private PerigoRuleProvider perigoRuleProvider;

    @Autowired
    private VariacaoBruscaTemperaturaRuleProvider variacaoBruscaTemperaturaRuleProvider;

    @Autowired
    private ManutencaoCamaraRuleProvider manutencaoCamaraRuleProvider;

    @Autowired
    private ManutencaoSensoresRuleProvider manutencaoSensoresRuleProvider;

    @Autowired
    private RuleEngine ruleEngine;

    @Autowired
    private PkgNameProvider pkgNameProvider;
    
    @GetMapping("/perigolimite")
    public Pair<String, Double> getPerigoLimit(){
        return Pair.of("perigoLimite", perigoRuleProvider.getLimiteTemp());
    }

    @PostMapping("/perigolimite")
    public ConfigurationSchema.PerigoConfigurationSchema setPerigoLimit(@RequestBody ConfigurationSchema.PerigoConfigurationSchema perigoPair){

        ruleEngine.ruleEngineManagement.deleteRule(pkgNameProvider.getPkgName(), perigoRuleProvider.getRiseRuleName());
        ruleEngine.ruleEngineManagement.deleteRule(pkgNameProvider.getPkgName(), perigoRuleProvider.getRetractRuleName());

        perigoRuleProvider.setLimiteTemp(perigoPair.temperatura);
        ruleEngine.ruleEngineManagement.insertPackage(perigoRuleProvider.getRulePackage());

        return perigoPair;
    }
    
    @GetMapping("/variacaobrusca")
    public ConfigurationSchema.VariacaoBruscaConfigurationSchema getVariacaoBruscaConfiguration(){
        ConfigurationSchema.VariacaoBruscaConfigurationSchema response = new ConfigurationSchema.VariacaoBruscaConfigurationSchema();
        response.temperatura = variacaoBruscaTemperaturaRuleProvider.getTemperatura();
        response.tempo = variacaoBruscaTemperaturaRuleProvider.getTempo();
        return response;
    }

    @PostMapping("/variacaobrusca")
    public ConfigurationSchema.VariacaoBruscaConfigurationSchema setVariacaoBruscaConfiguration(@RequestBody ConfigurationSchema.VariacaoBruscaConfigurationSchema variacaoBruscaConfigurationSchema){

        ruleEngine.ruleEngineManagement.deleteRule(pkgNameProvider.getPkgName(), variacaoBruscaTemperaturaRuleProvider.getRiseRuleName());

        variacaoBruscaTemperaturaRuleProvider.setTemperatura(variacaoBruscaConfigurationSchema.temperatura);
        variacaoBruscaTemperaturaRuleProvider.setTempo(variacaoBruscaConfigurationSchema.tempo);

        ruleEngine.ruleEngineManagement.insertPackage(variacaoBruscaTemperaturaRuleProvider.getRulePackage());

        return variacaoBruscaConfigurationSchema;
    }

    @GetMapping("/manutencaocamara")
    public ConfigurationSchema.ManutencaoConfigurationSchema getManutencaoCamara(){
        ConfigurationSchema.ManutencaoConfigurationSchema response = new ConfigurationSchema.ManutencaoConfigurationSchema();
        response.tempo = manutencaoCamaraRuleProvider.getTempo();
        response.eventos = manutencaoCamaraRuleProvider.getEventos();
        return response;
    }

    @PostMapping("/manutencaocamara")
    public ConfigurationSchema.ManutencaoConfigurationSchema setManutencaoCamara(@RequestBody ConfigurationSchema.ManutencaoConfigurationSchema manutencaoConfigurationSchema){

        ruleEngine.ruleEngineManagement.deleteRule(pkgNameProvider.getPkgName(), manutencaoCamaraRuleProvider.getRiseRuleName());
        ruleEngine.ruleEngineManagement.deleteRule(pkgNameProvider.getPkgName(), manutencaoCamaraRuleProvider.getRetractRuleName());

        manutencaoCamaraRuleProvider.setEventos(manutencaoConfigurationSchema.eventos);
        manutencaoCamaraRuleProvider.setTempo(manutencaoConfigurationSchema.tempo);

        ruleEngine.ruleEngineManagement.insertPackage(manutencaoCamaraRuleProvider.getRulePackage());

        return manutencaoConfigurationSchema;
    }

    @GetMapping("/manutencaosensores")
    public ConfigurationSchema.ManutencaoConfigurationSchema getManutencaoSensores(){
        ConfigurationSchema.ManutencaoConfigurationSchema response = new ConfigurationSchema.ManutencaoConfigurationSchema();
        response.tempo = manutencaoSensoresRuleProvider.getTempo();
        response.eventos = manutencaoSensoresRuleProvider.getEventos();
        return response;
    }

    @PostMapping("/manutencaosensores")
    public ConfigurationSchema.ManutencaoConfigurationSchema setManutencaoSensores(@RequestBody ConfigurationSchema.ManutencaoConfigurationSchema manutencaoConfigurationSchema){

        ruleEngine.ruleEngineManagement.deleteRule(pkgNameProvider.getPkgName(), manutencaoSensoresRuleProvider.getRiseRuleName());
        ruleEngine.ruleEngineManagement.deleteRule(pkgNameProvider.getPkgName(), manutencaoSensoresRuleProvider.getRetractRuleName());

        manutencaoSensoresRuleProvider.setEventos(manutencaoConfigurationSchema.eventos);
        manutencaoSensoresRuleProvider.setTempo(manutencaoConfigurationSchema.tempo);

        ruleEngine.ruleEngineManagement.insertPackage(manutencaoSensoresRuleProvider.getRulePackage());

        return manutencaoConfigurationSchema;
    }

}
