package kimeraSolar.vacinas.backend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import kimeraSolar.ruleEngineManagement.domain.WorkingMemory;
import kimeraSolar.vacinas.backend.configuration.CamarasConfiguration;
import kimeraSolar.vacinas.backend.configuration.VacinaTiposConfiguration;
import kimeraSolar.vacinas.backend.configuration.VacinasConfiguration;
import kimeraSolar.vacinas.backend.schemas.VacinaSchema;
import kimeraSolar.vacinas.domain.Camara;
import kimeraSolar.vacinas.domain.Vacina;
import kimeraSolar.vacinas.services.RuleEngine;

@RestController
@RequestMapping("/vacinas")
public class VacinaController {
    Logger logger = LoggerFactory.getLogger(VacinaController.class);

    @Autowired
    private VacinaTiposConfiguration vacinaTiposConfiguration;
    
    @Autowired
	private VacinasConfiguration vacinasConfiguration;

    @Autowired
    private CamarasConfiguration camarasConfiguration;

    @GetMapping("/tipos")
    public List<VacinaSchema.TipoSchema> getTipos(){
        logger.info("Requesting VacinaTipos Info");
        
        List<VacinaSchema.TipoSchema> response = new ArrayList<>();

        for(Map.Entry<String, Vacina.TipoVacina> entry : vacinaTiposConfiguration.getVacinaTipos().entrySet()){
            Vacina.TipoVacina tipo = entry.getValue();
            response.add(new VacinaSchema.TipoSchema(tipo.getNome(), tipo.getTempMin(), tipo.getTempMax(), tipo.getTempoDescarte()));
        }
        logger.info("Request will return {} VacinaTipos Info", response.size());
        
        return response;
    }

    @PostMapping("/tipos/inserir")
    public VacinaSchema.TipoSchema insereTipo(@RequestBody VacinaSchema.TipoSchema vTipoSchema){
        logger.info("Creating VacinaTipo {}", vTipoSchema.name);
        try{
            if(vacinaTiposConfiguration.getVacinaTipos().containsKey(vTipoSchema.name)){
                throw new Exception();
            }else{
                vacinaTiposConfiguration.addVacinaTipo(new Vacina.TipoVacina(vTipoSchema.name, vTipoSchema.tempMin, vTipoSchema.tempMax, vTipoSchema.tempoDescarte));
            }
        }catch(Exception exception){
            logger.warn("Failed to create VacinaTipo {}", vTipoSchema.name);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return vTipoSchema;
    }

    @PostMapping("/mover")
    public VacinaSchema.VacinaMoveSchema moverVacina(@RequestBody VacinaSchema.VacinaMoveSchema vacinaMoveSchema){
        logger.info("Moving Vacina {} from Camara {} to Camara {}", vacinaMoveSchema.vacinaId, vacinaMoveSchema.camaraAtualId, vacinaMoveSchema.camaraNovaId);
        try{
            Pair<Vacina, FactHandle> vacinaPair = vacinasConfiguration.getVacina(vacinaMoveSchema.vacinaId);
            Pair<Camara, FactHandle> camaraAtualPair = camarasConfiguration.getCamara(vacinaMoveSchema.camaraAtualId);
            Pair<Camara, FactHandle> camaraNovaPair = camarasConfiguration.getCamara(vacinaMoveSchema.camaraNovaId);

            camaraAtualPair.getLeft().removeVacina(vacinaPair.getLeft());
            camaraNovaPair.getLeft().addVacina(vacinaPair.getLeft());
            
            WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
            workingMemory.getKieSession().update(camaraAtualPair.getRight(), camaraAtualPair.getLeft());
            workingMemory.getKieSession().update(camaraNovaPair.getRight(), camaraNovaPair.getLeft());

        }catch(Exception exception){
            logger.warn("Request Failed for Vacina", vacinaMoveSchema.vacinaId);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);  
        }

        return vacinaMoveSchema;
    }

    @PostMapping("/desabastecer")
    public VacinaSchema desabastecerVacina(@RequestBody VacinaSchema.VacinaMoveSchema vacinaMoveSchema){
        logger.info("Requesting Desabastecimento for Vacina {} from Camara {}", vacinaMoveSchema.vacinaId, vacinaMoveSchema.camaraAtualId);
        VacinaSchema response = null;
        try{
            Pair<Vacina, FactHandle> vacinaPair = vacinasConfiguration.getVacina(vacinaMoveSchema.vacinaId);
            Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(vacinaMoveSchema.camaraAtualId);

            camaraPair.getLeft().removeVacina(vacinaPair.getLeft());
            vacinaPair.getLeft().setRetirada(new Date());
            
            WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
            workingMemory.getKieSession().update(camaraPair.getRight(), camaraPair.getLeft());
            workingMemory.getKieSession().update(vacinaPair.getRight(), vacinaPair.getLeft());

            response = getVacinaSchema(vacinaPair.getLeft());
            }catch(Exception exception){
            logger.warn("Request Failed for Desabastecimento for Vacina {} from Camara {}", vacinaMoveSchema.vacinaId, vacinaMoveSchema.camaraAtualId);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return response;
    }

    @GetMapping("/vacinainfo/{vacinaid}")
    public VacinaSchema getVacina(@PathVariable("vacinaid") String vacinaId){
        Pair<Vacina, FactHandle> vacinaPair = vacinasConfiguration.getVacina(vacinaId);
        return getVacinaSchema(vacinaPair.getLeft());
    }

    protected static VacinaSchema getVacinaSchema(Vacina vacina){
        VacinaSchema response = new VacinaSchema();
        response.id = vacina.getVacinaId();
        response.name = vacina.getTipo().getNome();
        response.tempMin = vacina.getTipo().getTempMin();
        response.tempMax = vacina.getTipo().getTempMax();
        response.tempoDescarte = vacina.getTipo().getTempoDescarte();
        response.status = vacina.getStatus();
        response.vencimentoDate = vacina.getVencimento() != null ? vacina.getVencimento() : DateUtils.addDays(vacina.getAbastecimento(), 30);
        response.abastecimentoDate = vacina.getAbastecimento();
        response.desabastecimentoDate = vacina.getRetirada();
        return response;
    }
}
