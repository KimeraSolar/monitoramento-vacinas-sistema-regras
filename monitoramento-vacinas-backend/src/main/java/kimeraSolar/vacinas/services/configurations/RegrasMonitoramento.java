package kimeraSolar.vacinas.services.configurations;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONObject;

import kimeraSolar.ruleEngineManagement.domain.RuleForm;
import kimeraSolar.ruleEngineManagement.domain.RulePackage;
import kimeraSolar.ruleEngineManagement.services.packageServices.RuleFileReader;
import kimeraSolar.ruleEngineManagement.services.packageServices.implementation.RuleFileReaderImpl;

public class RegrasMonitoramento {

    static private String pkgName = "kimeraSolar.vacinas.rules";

    static public RulePackage getRulesFromFile(){

        RuleFileReader ruleFileReader = new RuleFileReaderImpl();

        RulePackage rulePackage = new RulePackage();
        try {
            rulePackage = ruleFileReader.readRuleFile("monitoramento-vacinas-backend/src/main/resources/rules/Sample.drl");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rulePackage;
    }

    static public RulePackage getFactDeclarations(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName);
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

    static public RulePackage getPerigoRules(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName);
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

    static public RulePackage getAlertaRules(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName);
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
    
    static public RulePackage getDescarteRules(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName);
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

    static public RulePackage getManutencaoCamaraRules(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName);
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

    static public RulePackage getVariacaoBruscaTempRules(){
        RulePackage rulePackage = new RulePackage();
        
        rulePackage.setPkgName(pkgName);
        rulePackage.setFileName("variacaoBruscaTemp");
        
        JSONObject riseVariacaoJsonObject = new JSONObject();
        riseVariacaoJsonObject.put("ruleName", "Rise Variacao Brusca Temperatura Rule");

        StringBuilder riseVariacaoStringBuilder = new StringBuilder();
        riseVariacaoStringBuilder
            .append("rule \"").append(riseVariacaoJsonObject.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $tempFinal : LeituraTemperatura( $tempF : temp, $camara : camara ) over window:length(10)\n")
            .append("    $tempInicial : LeituraTemperatura( $tempI : temp, $camara == camara, this before[0s, 10s] $tempFinal )\n")
            .append("    eval(Math.abs($tempF - $tempI) > 5)\n")
            .append("    not( exists ( VariacaoBruscaTemp( $tempFinal == tempFinal, $tempInicial == tempInicial ) ) )\n")
            .append("then\n")
            .append("    insert( new VariacaoBruscaTemp( $camara, $tempInicial, $tempFinal ) );\n")
            .append("    $camara.sendMessage(\"Variação brusca de temperatura na câmara \" + $camara.getObjectId() + \" com variação de \" + ($tempF - $tempI) + \" em menos de 10s.\"); \n")
            .append("end\n");
        riseVariacaoJsonObject.put("source", riseVariacaoStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(riseVariacaoJsonObject.toString()));

        return rulePackage;
    }

    static public RulePackage getManutencaoSensoresRules(){
        RulePackage rulePackage = new RulePackage();

        rulePackage.setPkgName(pkgName);
        rulePackage.setFileName("manutencaoSensores");

        JSONObject riseManutencaoJsonObject = new JSONObject();
        riseManutencaoJsonObject.put("ruleName", "Rise Manutencao Sensores Rule");

        StringBuilder riseManutencaoStringBuilder = new StringBuilder();
        riseManutencaoStringBuilder
            .append("rule \"").append(riseManutencaoJsonObject.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $camara : Camara( ativa == true )\n")
            .append("    $casosVariacaoBrusca : Number() from accumulate(\n")
            .append("        $v : VariacaoBruscaTemp( $camara == camara ) over window:time( 1m ),\n")
            .append("        count( $v )\n")
            .append("    )\n")
            .append("    eval( $casosVariacaoBrusca.intValue() >= 10 )\n")
            .append("    not( exists( ManutencaoNecessariaSensores( $camara == camara, ativo == true ) ) )\n")
            .append("then\n")
            .append("    insert(new ManutencaoNecessariaSensores( $camara, true ) );\n")
            .append("    $camara.sendMessage(\"Manutenção sugerida nos sensores da unidade \" + $camara.getObjectId() + \" após \" + $casosVariacaoBrusca.intValue() + \" casos de variação brusca de temperatura em 1m.\");\n")
            .append("end\n");
        riseManutencaoJsonObject.put("source", riseManutencaoStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(riseManutencaoJsonObject.toString()));

        JSONObject retractManutencaoJsonObject = new JSONObject();
        retractManutencaoJsonObject.put("ruleName", "Retract Manutencao Sensores Rule");

        StringBuilder retractManutencaoStringBuilder = new StringBuilder();
        retractManutencaoStringBuilder
            .append("rule \"").append(retractManutencaoJsonObject.get("ruleName")).append("\"\n")
            .append("when\n")
            .append("    $camara : Camara( ativa == true )\n")
            .append("    $casosVariacaoBrusca : Number() from accumulate(\n")
            .append("        $v : VariacaoBruscaTemp( $camara == camara ) over window:time( 1m ),\n")
            .append("        count( $v )\n")
            .append("    )\n")
            .append("    eval($casosVariacaoBrusca.intValue() < 10)\n")
            .append("    $manutencao : ManutencaoNecessariaSensores( $camara == camara, ativo == true )\n")
            .append("then\n")
            .append("    $manutencao.setAtivo( false );\n")
            .append("    update($manutencao);\n")
            .append("    $camara.sendMessage(\"Sem necessidade de manutenção dos sensores na unidade \" + $camara.getObjectId() + \" após \" + $casosVariacaoBrusca.intValue() + \" casos de variação brusca de temperatura nos últimos 1m.\");\n")
            .append("end\n");
        retractManutencaoJsonObject.put("source", retractManutencaoStringBuilder.toString());
        rulePackage.addRule(RuleForm.parseJson(retractManutencaoJsonObject.toString()));

        return rulePackage;
    }
}
