package kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.RuleForm;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;

@Configuration
public class AlertaRuleProvider {

    @Autowired
    private PkgNameProvider pkgName;
    
    public RulePackage getRulePackage(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName.getPkgName());
        rulePackage.setFileName("alerta");

        JSONObject jsonRiseAlertaRule = new JSONObject();
        jsonRiseAlertaRule.put("ruleName", "Rise Alerta Rule");

        StringBuilder riseAlertaRuleStringBuilder = new StringBuilder();
        riseAlertaRuleStringBuilder
            .append("rule \"").append(jsonRiseAlertaRule.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $vacina : Vacina( descartada == false )\n")
            .append("    $camara : Camara( vacinas contains $vacina, $temp : temp, ( $temp.getTemp() < $vacina.getTipo().getTempMin() ) || ( $temp.getTemp() > $vacina.getTipo().getTempMax() ) )\n")
            .append("    not ( exists ( Alerta( $vacina == vacina, ativo == true ) ) )\n")
            .append("then\n")
            .append("    insert ( new Alerta($camara, $vacina, $camara.gerenteMaisProx(), $temp, true, new Timestamp(System.currentTimeMillis())) );\n")
            .append("    $camara.gerenteMaisProx().sendMensagem(	\"Alerta na Câmara \" + $camara.getObjectId() + \" com temperatura \" + $temp.getTemp() + \" fora dos limites para a vacina \" + $vacina.getTipo().getNome() +\n")
            .append("                            \". Favor comparecer no local \" + $camara.getLocal() + \" imediatamente.\");\n")
            .append("    $camara.setAlerta(true);\n")
            .append("    update( $camara );\n")
            .append("    $vacina.setAlerta(true);\n")
            .append("    update( $vacina );\n")
            .append("end\n");
        
        jsonRiseAlertaRule.put("source", riseAlertaRuleStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(jsonRiseAlertaRule.toString()));

        JSONObject jsonRetractAlertaRule = new JSONObject();
        jsonRetractAlertaRule.put("ruleName", "Retract Alerta Rule");

        StringBuilder retractAlertaRuleStringBuilder = new StringBuilder();
        retractAlertaRuleStringBuilder
            .append("rule \"").append(jsonRetractAlertaRule.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $vacina : Vacina( descartada == false )\n")
            .append("    $camara : Camara( vacinas contains $vacina, $temp : temp, ( $temp.getTemp() > $vacina.getTipo().getTempMin() ) && ( $temp.getTemp() < $vacina.getTipo().getTempMax() ) )\n")
            .append("    $alerta : Alerta( $vacina == vacina, $inicio : inicio, $gerente : gerente, ativo == true)\n")
            .append("then\n")
            .append("    $alerta.setAtivo(false);\n")
            .append("    $alerta.setDuration( System.currentTimeMillis() - $inicio.getTime() );\n")
            .append("    update( $alerta );\n")
            .append("    $gerente.sendMensagem(\"A Câmara \" + $camara.getObjectId() + \" está com temperatura \" + $temp.getTemp() + \" fora de alerta para a vacina \" + $vacina.getTipo().getNome() + \".\");\n")
            .append("    $camara.setAlerta(false);\n")
            .append("    update( $camara );\n")
            .append("    $vacina.setAlerta(false);\n")
            .append("    update( $vacina );\n")
            .append("end\n");
        
        jsonRetractAlertaRule.put("source", retractAlertaRuleStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(jsonRetractAlertaRule.toString()));

        return rulePackage;
    }
}
