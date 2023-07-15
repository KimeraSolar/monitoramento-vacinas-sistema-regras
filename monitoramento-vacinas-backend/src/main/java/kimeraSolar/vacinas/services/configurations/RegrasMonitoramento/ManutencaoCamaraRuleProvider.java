package kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.RuleForm;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;

@Configuration
public class ManutencaoCamaraRuleProvider {

    @Autowired
    private PkgNameProvider pkgNameProvider;

    public RulePackage getRulePackage(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgNameProvider.getPkgName());
        rulePackage.setFileName("ManutencaoCamara");

        JSONObject riseManutencaoCamaraRuleJsonObject = new JSONObject();
        riseManutencaoCamaraRuleJsonObject.put("ruleName", "Rise Manutencao Camara Rule");

        StringBuilder riseManutencaoCamaraStringBuilder = new StringBuilder();
        riseManutencaoCamaraStringBuilder
            .append("rule \"").append(riseManutencaoCamaraRuleJsonObject.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $camara : Camara( ativa == true )\n")
            .append("    $casosPerigo : Number() from accumulate(\n")
            .append("        $p : Perigo ( $camara == camara ) over window:time( 1m ),\n")
            .append("        count( $p )\n")
            .append("    )\n")
            .append("    $casosAlerta : Number() from accumulate(\n")
            .append("        $a : Alerta ( $camara == camara ) over window:time( 1m ),\n")
            .append("        count( $a )\n")
            .append("    )\n")
            .append("    eval($casosPerigo.intValue() + $casosAlerta.intValue() >= 3)\n")
            .append("    not( exists( ManutencaoNecessariaCamara( $camara == camara, ativo == true ) ) )\n")
            .append("then\n")
            .append("    insert(new ManutencaoNecessariaCamara( $camara, true ) );\n")
            .append("    $camara.sendMessage(\"Manutenção sugerida na unidade \" + $camara.getObjectId() + \" após \" + ($casosPerigo.intValue() + $casosAlerta.intValue()) + \" casos de perigo e/ou alerta nos últimos 1m.\");\n")
            .append("end\n");
        riseManutencaoCamaraRuleJsonObject.put("source", riseManutencaoCamaraStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(riseManutencaoCamaraRuleJsonObject.toString()));

        JSONObject retractManutencaoCamaraJsonObject = new JSONObject();
        retractManutencaoCamaraJsonObject.put("ruleName", "Retract Manutencao Camara Rule");

        StringBuilder retractManutencaoCamaraStringBuilder = new StringBuilder();
        retractManutencaoCamaraStringBuilder
            .append("rule \"").append(retractManutencaoCamaraJsonObject.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $camara : Camara( ativa == true )\n")
            .append("    $casosPerigo : Number() from accumulate(\n")
            .append("        $p : Perigo ( $camara == camara ) over window:time( 1m ),\n")
            .append("        count( $p )\n")
            .append("    )\n")
            .append("    $casosAlerta : Number() from accumulate(\n")
            .append("        $a : Alerta ( $camara == camara ) over window:time( 1m ),\n")
            .append("        count( $a )\n")
            .append("    )\n")
            .append("    eval($casosPerigo.intValue() + $casosAlerta.intValue() < 3)\n")
            .append("    $manutencao : ManutencaoNecessariaCamara( $camara == camara, ativo == true )\n")
            .append("then\n")
            .append("    $manutencao.setAtivo( false );\n")
            .append("    update($manutencao);\n")
            .append("    $camara.sendMessage(\"Sem necessidade de manutenção na unidade \" + $camara.getObjectId() + \" após \" + ($casosPerigo.intValue() + $casosAlerta.intValue()) + \" casos de perigo e/ou alerta nos últimos 1m.\");\n")
            .append("end\n");
        retractManutencaoCamaraJsonObject.put("source", retractManutencaoCamaraStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(retractManutencaoCamaraJsonObject.toString()));


        return rulePackage;
    }
}
