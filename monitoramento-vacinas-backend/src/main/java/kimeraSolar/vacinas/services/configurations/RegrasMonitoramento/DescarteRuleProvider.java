package kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.RuleForm;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;

@Configuration
public class DescarteRuleProvider {

    @Autowired
    private PkgNameProvider pkgNameProvider;

    public RulePackage getRulePackage(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgNameProvider.getPkgName());
        rulePackage.setFileName("Descarte");

       
        JSONObject riseDescarteRuleJsonObject = new JSONObject();
        riseDescarteRuleJsonObject.put("ruleName", "Rise Descarte Rule");

        StringBuilder riseDescarteRuleStringBuilder = new StringBuilder();
        riseDescarteRuleStringBuilder
            .append("rule \"").append(riseDescarteRuleJsonObject.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $vacina : Vacina( descartada == false )\n")
            .append("    $camara : Camara( vacinas contains $vacina )\n")
            .append("    $alerta : Alerta ( $vacina == vacina, $gerente : gerente, ativo == true, $inicio : inicio)\n")
            .append("    eval ((System.currentTimeMillis() - $inicio.getTime()) > $vacina.getTipo().getTempoDescarte())\n")
            .append("    not (exists (Descarte($alerta == alerta) ) )\n")
            .append("then\n")
            .append("    insert ( new Descarte($alerta, new Timestamp(System.currentTimeMillis())) );\n")
            .append("    $vacina.setDescartada(true);\n")
            .append("    update($vacina);\n")
            .append("    $gerente.sendMensagem(\"Tempo de alerta excedido, descarte sugerido para vacina \" + $vacina.getTipo().getNome() + \" na câmara \" + $camara.getObjectId() + \".\");\n")
            .append("end\n");
        riseDescarteRuleJsonObject.put("source", riseDescarteRuleStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(riseDescarteRuleJsonObject.toString()));

        return rulePackage;
    }
    
}
