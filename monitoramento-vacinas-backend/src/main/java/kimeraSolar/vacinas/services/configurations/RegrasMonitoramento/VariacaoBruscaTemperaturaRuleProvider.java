package kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.RuleForm;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;

@Configuration
public class VariacaoBruscaTemperaturaRuleProvider {

    @Autowired
    private PkgNameProvider pkgNameProvider;

    private double temperatura = 5;

    private String tempo = "10s";

    private String riseRuleName = "Rise Variacao Brusca Temperatura Rule";

    public double getTemperatura(){
        return temperatura;
    }

    public void setTemperatura(double temperatura){
        this.temperatura = temperatura;
    }

    public String getTempo(){
        return tempo;
    }

    public void setTempo(String tempo){
        this.tempo = tempo;
    }

    public String getRiseRuleName(){
        return riseRuleName;
    }

    public RulePackage getRulePackage(){
        RulePackage rulePackage = new RulePackage();
        
        rulePackage.setPkgName(pkgNameProvider.getPkgName());
        rulePackage.setFileName("variacaoBruscaTemp");
        
        JSONObject riseVariacaoJsonObject = new JSONObject();
        riseVariacaoJsonObject.put("ruleName", riseRuleName);

        StringBuilder riseVariacaoStringBuilder = new StringBuilder();
        riseVariacaoStringBuilder
            .append("rule \"").append(riseVariacaoJsonObject.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $tempFinal : LeituraTemperatura( $tempF : temp, $camara : camara ) over window:length(10)\n")
            .append("    $tempInicial : LeituraTemperatura( $tempI : temp, $camara == camara, this before[0s, " + tempo + "] $tempFinal )\n")
            .append("    eval(Math.abs($tempF - $tempI) > " + temperatura + ")\n")
            .append("    not( exists ( VariacaoBruscaTemp( $tempFinal == tempFinal, $tempInicial == tempInicial ) ) )\n")
            .append("then\n")
            .append("    insert( new VariacaoBruscaTemp( $camara, $tempInicial, $tempFinal ) );\n")
            .append("    $camara.sendMessage(\"Variação brusca de temperatura na câmara \" + $camara.getObjectId() + \" com variação de \" + ($tempF - $tempI) + \" em menos de " + tempo + ".\"); \n")
            .append("end\n");
        riseVariacaoJsonObject.put("source", riseVariacaoStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(riseVariacaoJsonObject.toString()));

        return rulePackage;
    }
}
