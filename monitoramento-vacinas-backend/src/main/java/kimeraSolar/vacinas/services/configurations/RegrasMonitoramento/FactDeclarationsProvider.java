package kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.RuleForm;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;

@Configuration
public class FactDeclarationsProvider {

    @Autowired
    private PkgNameProvider pkgName;

    public RulePackage getRulePackage(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName.getPkgName());
        rulePackage.setFileName("declarations");

        rulePackage.addInclude("kimeraSolar.vacinas.domain.Camara");
        rulePackage.addInclude("kimeraSolar.vacinas.domain.Vacina");
        rulePackage.addInclude("kimeraSolar.vacinas.domain.Eventos.LeituraTemperatura");
        rulePackage.addInclude("kimeraSolar.vacinas.domain.Eventos.Perigo");
        rulePackage.addInclude("kimeraSolar.vacinas.domain.Eventos.Alerta");
        rulePackage.addInclude("kimeraSolar.vacinas.domain.Eventos.Descarte");
        rulePackage.addInclude("java.sql.Timestamp");
        
        JSONObject jsonPerigoDeclaration = new JSONObject();
        jsonPerigoDeclaration.put("ruleName", "Perigo Declaration");

        StringBuilder perigoDeclarationStringBuilder = new StringBuilder();
        perigoDeclarationStringBuilder
            .append("declare Perigo\n")
            .append("    @role( event )\n")
            .append("    @timestamp( inicio )\n")
            .append("    @duration( duration )\n")
            .append("end\n");

        jsonPerigoDeclaration.put("source", perigoDeclarationStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(jsonPerigoDeclaration.toString()));

        JSONObject jsonAlertaDeclaration = new JSONObject();
        jsonAlertaDeclaration.put("ruleName", "Alerta Declaration");

        StringBuilder alertaDeclarationStringBuilder = new StringBuilder();
        alertaDeclarationStringBuilder
            .append("declare Alerta\n")
            .append("    @role( event )\n")
            .append("    @timestamp( inicio )\n")
            .append("    @duration( duration )\n")
            .append("end\n");
        
        jsonAlertaDeclaration.put("source", alertaDeclarationStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(jsonAlertaDeclaration.toString()));

        JSONObject leituraDeclarationJsonObject = new JSONObject();
        leituraDeclarationJsonObject.put("ruleName", "Leitura Temeratura Declaration");

        StringBuilder leituraDeclarationStringBuilder = new StringBuilder();
        leituraDeclarationStringBuilder
            .append("declare LeituraTemperatura\n")
            .append("    @role( event )\n")
            .append("    @timestamp( inicio )\n")
            .append("end\n");
        leituraDeclarationJsonObject.put("source", leituraDeclarationStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(leituraDeclarationJsonObject.toString()));

        JSONObject variacaoDeclarationJsonObject = new JSONObject();
        variacaoDeclarationJsonObject.put("ruleName", "Variacao Brusca Temperatura Declaration");

        StringBuilder variacaoDeclarationStringBuilder = new StringBuilder();
        variacaoDeclarationStringBuilder
            .append("declare VariacaoBruscaTemp\n")
            .append("    @role( event )\n")
            .append("    camara : Camara\n")
            .append("    tempInicial : LeituraTemperatura\n")
            .append("    tempFinal : LeituraTemperatura\n")
            .append("\n")
            .append("end\n");
        variacaoDeclarationJsonObject.put("source", variacaoDeclarationStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(variacaoDeclarationJsonObject.toString()));

        JSONObject descarteDeclaratioJsonObject = new JSONObject();
        descarteDeclaratioJsonObject.put("ruleName", "Descarte Declaration");

        StringBuilder descarteDeclarationStringBuilder = new StringBuilder();
        descarteDeclarationStringBuilder
            .append("declare Descarte\n")
            .append("    @role( event )\n")
            .append("    @timestamp ( timestamp )\n")
            .append("end\n");

        descarteDeclaratioJsonObject.put("source", descarteDeclarationStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(descarteDeclaratioJsonObject.toString()));

        JSONObject manutencaoCamaraDeclarationJsonObject = new JSONObject();
        manutencaoCamaraDeclarationJsonObject.put("ruleName", "Manutencao Camara Declaration");

        StringBuilder manutencaoCamaraDeclarationStringBuilder = new StringBuilder();
        manutencaoCamaraDeclarationStringBuilder
            .append("declare ManutencaoNecessariaCamara\n")
            .append("    camara : Camara\n")
            .append("    ativo : boolean\n")
            .append("end\n");
        manutencaoCamaraDeclarationJsonObject.put("source", manutencaoCamaraDeclarationStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(manutencaoCamaraDeclarationJsonObject.toString()));

        JSONObject manutencaoSensoresDeclarationJsonObject = new JSONObject();
        manutencaoSensoresDeclarationJsonObject.put("ruleName", "Manutencao Sensores Declaration");
        
        StringBuilder manutencaoSensoresDeclarationStringBuilder = new StringBuilder();
        manutencaoSensoresDeclarationStringBuilder
            .append("declare ManutencaoNecessariaSensores\n")
            .append("    camara : Camara\n")
            .append("    ativo : boolean\n")
            .append("end\n");
        manutencaoSensoresDeclarationJsonObject.put("source", manutencaoSensoresDeclarationStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(manutencaoSensoresDeclarationJsonObject.toString()));
        return rulePackage;
    }

}
