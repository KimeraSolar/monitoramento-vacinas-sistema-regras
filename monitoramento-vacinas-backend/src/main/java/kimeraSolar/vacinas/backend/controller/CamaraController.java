package kimeraSolar.vacinas.backend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import kimeraSolar.vacinas.backend.configuration.GerentesConfiguration;
import kimeraSolar.vacinas.backend.configuration.SensorsConfiguration;
import kimeraSolar.vacinas.backend.configuration.VacinaTiposConfiguration;
import kimeraSolar.vacinas.backend.configuration.VacinasConfiguration;
import kimeraSolar.vacinas.backend.schemas.CamaraSchema;
import kimeraSolar.vacinas.backend.schemas.VacinaSchema;
import kimeraSolar.vacinas.domain.Camara;
import kimeraSolar.vacinas.domain.Vacina;
import kimeraSolar.vacinas.services.RuleEngine;
import kimeraSolar.vacinas.domain.Eventos.LeituraTemperatura;
import kimeraSolar.vacinas.domain.Gerente;
import kimeraSolar.vacinas.domain.GpsSensorWrapper;
import kimeraSolar.vacinas.domain.TempSensorWrapper;

@RestController
@RequestMapping("/vacinas")
public class CamaraController {

    Logger logger = LoggerFactory.getLogger(CamaraController.class);

    @Autowired
    private CamarasConfiguration camarasConfiguration;

    @Autowired
    private GerentesConfiguration gerentesConfiguration;

	@Autowired
	private SensorsConfiguration sensorsConfiguration;
	
    @Autowired
	private VacinasConfiguration vacinasConfiguration;

    @Autowired
    private VacinaTiposConfiguration vacinaTiposConfiguration;
 
    public static class TemperaturaObject{

        public TemperaturaObject(Date key, Float value){
            this.key = key;
            this.value = value;
        }

        public Date key;
        public Float value;
    }

    @PostMapping("/camara/inserir")
    public CamaraSchema.CamaraInsertSchema insertCamara(@RequestBody CamaraSchema.CamaraInsertSchema camaraInsertSchema){
        logger.info("Inserting new Camara {}", camaraInsertSchema.camaraName);
        try{
            Pair<Gerente, FactHandle> gerentePair = gerentesConfiguration.getGerente(camaraInsertSchema.gerenteName);
            WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
        
            if(camarasConfiguration.getCamaras().containsKey(camaraInsertSchema.camaraName)){
                Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(camaraInsertSchema.camaraName);
                camaraPair.getLeft().addGerente(gerentePair.getLeft());
                gerentePair.getLeft().addCamara(camaraPair.getLeft());
                workingMemory.getKieSession().update(gerentePair.getRight(), gerentePair.getLeft());
                workingMemory.getKieSession().update(camaraPair.getRight(), camaraPair.getLeft());
            }else{
                Camara c = new Camara(camaraInsertSchema.camaraName);
                c.addGerente(gerentePair.getLeft());
                gerentePair.getLeft().addCamara(c);
                workingMemory.getKieSession().update(gerentePair.getRight(), gerentePair.getLeft());
                
                camarasConfiguration.addCamara(c);
                Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(camaraInsertSchema.camaraName);
                
                Thread temperatureSensorWrapper = new Thread(new TempSensorWrapper(c.getObjectId() + "TempSensor", RuleEngine.ruleEngineManagement.getWorkingMemory().getKieSession(), camaraPair.getRight(), "smooth"));
                sensorsConfiguration.addSensorWrapper(temperatureSensorWrapper);
                temperatureSensorWrapper.start();

                Thread gpsSensorWrapper = new Thread(new GpsSensorWrapper(c.getObjectId() + "GPS", RuleEngine.ruleEngineManagement.getWorkingMemory().getKieSession(), camaraPair.getRight(), "static"));
                sensorsConfiguration.addSensorWrapper(gpsSensorWrapper);
                gpsSensorWrapper.start();
            }
		}catch(Exception exception){
            logger.warn("Request Failed for Inserting Camara {}", camaraInsertSchema.camaraName);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        
        }		
        return camaraInsertSchema;
    }

    @PostMapping("/camara/desinscrever")
    public CamaraSchema.CamaraInsertSchema unsubscribeCamara(@RequestBody CamaraSchema.CamaraInsertSchema camaraInsertSchema){
        logger.info("Unsubscribing Gerente {} from Camara {}", camaraInsertSchema.gerenteName, camaraInsertSchema.camaraName);
        try{
            Pair<Gerente, FactHandle> gerentePair = gerentesConfiguration.getGerente(camaraInsertSchema.gerenteName);
            Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(camaraInsertSchema.camaraName);
                
            if(gerentePair.getLeft().getCamaras().contains(camaraPair.getLeft())){
                camaraPair.getLeft().removeGerente(gerentePair.getLeft());
                gerentePair.getLeft().removeCamara(camaraPair.getLeft());
                
                WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
                workingMemory.getKieSession().update(gerentePair.getRight(), gerentePair.getLeft());
                workingMemory.getKieSession().update(camaraPair.getRight(), camaraPair.getLeft());
            }
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Unsubscribing Gerente {} from Camara {}", camaraInsertSchema.gerenteName, camaraInsertSchema.camaraName);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        
        }
    
        return camaraInsertSchema;
    }

    @PostMapping("/camara/delete")
    public CamaraSchema.CamaraInsertSchema deleteCamara(@RequestBody CamaraSchema.CamaraInsertSchema camaraInsertSchema){
        logger.info("Deleting Camara {}", camaraInsertSchema.camaraName);
        try{
            Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(camaraInsertSchema.camaraName);
            WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
        
            if(!camaraPair.getLeft().isAtiva()){
                for(Gerente g : camaraPair.getLeft().getGerentes()){
                    Pair<Gerente, FactHandle> gerentePair = gerentesConfiguration.getGerente(g.getObjectId());
            
                    gerentePair.getLeft().removeCamara(camaraPair.getLeft());
                    
                    workingMemory.getKieSession().update(gerentePair.getRight(), gerentePair.getLeft());
                
                }
                camarasConfiguration.removeCamara(camaraInsertSchema.camaraName);
            }else{
                throw new Exception("The Camara still is active");
            }
        }catch(Exception exception){
            logger.warn("Request Failed for Deleting Camara {}", camaraInsertSchema.camaraName);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        
        }
        
        return camaraInsertSchema;
    }

    @GetMapping("/{camaraid}")
    public CamaraSchema getCamaraInfo(@PathVariable("camaraid") String camaraId){
        logger.info("Requesting Info from Camara {}", camaraId);
        CamaraSchema response = null;
        try{
            Camara c = camarasConfiguration.getCamaras().get(camaraId);
            float[] location = {c.getLocal().getLatitude(), c.getLocal().getLongitude()};
                
            response = new CamaraSchema(c.getStatus(), c.getTemp().getTemp(), c.getObjectId(), location );
                
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Camara", camaraId);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        
        }
        return response;
    }

    @GetMapping("/{camaraid}/temperaturas")
    public List<TemperaturaObject> getTemperaturas(@PathVariable("camaraid") String camaraId){
        logger.info("Requesting Temperaturas Info from Camara {}", camaraId);
        
        List<TemperaturaObject> temperaturas = null;
        try{
            temperaturas = new ArrayList<>();
            Camara c = camarasConfiguration.getCamaras().get(camaraId);
            for( Map.Entry<Date, LeituraTemperatura> temperatura : c.getTemperaturas().entrySet()){
                temperaturas.add(
                    new TemperaturaObject(temperatura.getKey(), temperatura.getValue().getTemp())
                );
            }
            logger.info("Request will return {} Temperaturas Info", temperaturas.size());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Camara", camaraId);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        
        }
        return temperaturas;
    }

    @GetMapping("/{camaraid}/vacinas")
    public List<VacinaSchema> getVacinas(@PathVariable("camaraid") String camaraId){
        logger.info("Requesting Vacinas Info from Camara {}", camaraId);
        
        List<VacinaSchema> vacinas = new ArrayList<>();
        try{
            Camara c = camarasConfiguration.getCamaras().get(camaraId);
            for( Vacina v : c.getVacinas()){
                vacinas.add( VacinaController.getVacinaSchema(v) );
            }
            logger.info("Request will return {} Vacinas Info", vacinas.size());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Camara {}", camaraId);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        
        }

        return vacinas;
    }
    
    @PostMapping("/{camaraid}/abastecer")
    public VacinaSchema inserirVacina(@PathVariable("camaraid") String camaraId, @RequestBody VacinaSchema vacinaSchema){
        logger.info("Creating new Vacina {} in Camara {}", vacinaSchema.name, camaraId);
        try{
            Vacina.TipoVacina tipo = vacinaTiposConfiguration.getVacinaTipos().get(vacinaSchema.name);
            Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(camaraId);
            Vacina v = new Vacina(tipo, camaraId + vacinaSchema.name + vacinasConfiguration.getVacinas().size() , vacinaSchema.abastecimentoDate, vacinaSchema.vencimentoDate, null, false);
            camaraPair.getLeft().addVacina(v);
            vacinasConfiguration.addVacina(v);
            
            WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
            workingMemory.getKieSession().update(camaraPair.getRight(), camaraPair.getLeft());
            vacinaSchema = VacinaController.getVacinaSchema(v);
        }catch(Exception exception){
            logger.warn("Failed to Create Vacina {} in Camara {}", vacinaSchema.name, camaraId);
            logger.warn("Exception {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return vacinaSchema;
    }
}
