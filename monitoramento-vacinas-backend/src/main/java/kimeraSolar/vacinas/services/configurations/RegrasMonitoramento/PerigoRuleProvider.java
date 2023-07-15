package kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.RuleForm;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;

@Configuration
public class PerigoRuleProvider {

    @Autowired
    private PkgNameProvider pkgName;
    
    public RulePackage getRulePackage(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName.getPkgName());
        rulePackage.setFileName("perigo");

        JSONObject jsonPerigoRule = new JSONObject();
        jsonPerigoRule.put("ruleName", "Rise Perigo Rule");

        StringBuilder perigoRuleStringBuilder = new StringBuilder();
        perigoRuleStringBuilder
            .append("rule \"").append(jsonPerigoRule.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("\t$vacina : Vacina( descartada == false )\n")
            .append("\t$camara : Camara( vacinas contains $vacina, $temp : temp, ( $temp.getTemp() - $vacina.getTipo().getTempMin() < 1 && > 0 ) || ( $vacina.getTipo().getTempMax() - $temp.getTemp() < 1 && > 0) ) \n")
            .append("\tnot (exists ( Perigo( $vacina == vacina, ativo == true) ) )\n")
            .append("then\n")
            .append("\tinsert(new Perigo( $camara, $vacina, $temp, true, new Timestamp(System.currentTimeMillis())) );\n")
            .append("\t$camara.sendMessage(\"A Câmara \" + $camara.getObjectId() + \" está com temperatura \" + $temp.getTemp() + \"ºC muito perto dos limites para a vacina \" + $vacina.getTipo().getNome() + \".\");\n") 
            .append("\t$camara.setPerigo(true);\n")
            .append("\tupdate( $camara );\n")
            .append("    $vacina.setPerigo(true);\n")
            .append("    update( $vacina );\n")
            .append("end\n");

        jsonPerigoRule.put("source", perigoRuleStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(jsonPerigoRule.toString()));

        JSONObject jsonSemPerigoRule = new JSONObject();
        jsonSemPerigoRule.put("ruleName", "Retract Perigo Rule");

        StringBuilder semPerigoRuleStirngStringBuilder = new StringBuilder();
        semPerigoRuleStirngStringBuilder
        .append("rule \"").append(jsonSemPerigoRule.get("ruleName")).append("\"\n")
        .append("when\n")
        .append("    $vacina : Vacina( descartada == false )\n")
        .append("    $camara : Camara( vacinas contains $vacina, $temp : temp, ( $temp.getTemp() - $vacina.getTipo().getTempMin() > 1 ) && ( $vacina.getTipo().getTempMax() - $temp.getTemp() > 1) )\n")
        .append("    $perigo : Perigo( $vacina == vacina, $inicio : inicio, ativo == true)\n")
        .append("then\n")
        .append("    $perigo.setAtivo( false );\n")
        .append("    $perigo.setDuration( System.currentTimeMillis() - $inicio.getTime() );\n")
        .append("    update( $perigo );\n")
        .append("    $camara.sendMessage(\"A Câmara \" + $camara.getObjectId() + \" está com temperatura \" + $temp.getTemp() + \" fora de perigo para a vacina \" + $vacina.getTipo().getNome() + \".\");\n")
        .append("    $camara.setPerigo(false);\n")
        .append("    update( $camara );\n")
        .append("    $vacina.setPerigo(false);\n")
        .append("    update( $vacina );\n")
        .append("end\n");

        jsonSemPerigoRule.put("source", semPerigoRuleStirngStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(jsonSemPerigoRule.toString()));

        return rulePackage;
    }
}
