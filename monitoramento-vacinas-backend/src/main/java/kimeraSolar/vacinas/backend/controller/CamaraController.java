package kimeraSolar.vacinas.backend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kimeraSolar.vacinas.backend.configuration.CamarasConfiguration;
import kimeraSolar.vacinas.backend.configuration.GerentesConfiguration;
import kimeraSolar.vacinas.backend.configuration.SensorsConfiguration;
import kimeraSolar.vacinas.backend.configuration.VacinaTiposConfiguration;
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
            Gerente g = gerentesConfiguration.getGerentes().get(camaraInsertSchema.gerenteName);
                
            if(camarasConfiguration.getCamaras().containsKey(camaraInsertSchema.camaraName)){
                Camara c = camarasConfiguration.getCamaras().get(camaraInsertSchema.camaraName);
                c.addGerente(g);
                g.addCamara(c);
            }else{
                Camara c = new Camara(camaraInsertSchema.camaraName);
                c.addGerente(g);
                g.addCamara(c);
                
                camarasConfiguration.addCamara(c);
                FactHandle f = RuleEngine.ruleEngineManagement.getWorkingMemory().getKieSession().insert(c);
                
                Thread temperatureSensorWrapper = new Thread(new TempSensorWrapper(c.getObjectId() + "TempSensor", RuleEngine.ruleEngineManagement.getWorkingMemory().getKieSession(), f, "smooth"));
                sensorsConfiguration.addSensorWrapper(temperatureSensorWrapper);
                temperatureSensorWrapper.start();

                Thread gpsSensorWrapper = new Thread(new GpsSensorWrapper(c.getObjectId() + "GPS", RuleEngine.ruleEngineManagement.getWorkingMemory().getKieSession(), f, "static"));
                sensorsConfiguration.addSensorWrapper(gpsSensorWrapper);
                gpsSensorWrapper.start();
            }
		}catch(NullPointerException exception){
            logger.warn("Request Failed for Inserting Camara {}", camaraInsertSchema.camaraName);
        }		
        return camaraInsertSchema;
    }

    @PostMapping("/camara/desinscrever")
    public CamaraSchema.CamaraInsertSchema unsubscribeCamara(@RequestBody CamaraSchema.CamaraInsertSchema camaraInsertSchema){
        logger.info("Unsubscribing Gerente {} from Camara {}", camaraInsertSchema.gerenteName, camaraInsertSchema.camaraName);
        try{
            Gerente g = gerentesConfiguration.getGerentes().get(camaraInsertSchema.gerenteName);
            Camara c = camarasConfiguration.getCamaras().get(camaraInsertSchema.camaraName);
            if(g.getCamaras().contains(c)){
                c.removeGerente(g);
                g.removeCamara(c);
            }
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Unsubscribing Gerente {} from Camara {}", camaraInsertSchema.gerenteName, camaraInsertSchema.camaraName);
        }
    
        return camaraInsertSchema;
    }

    @PostMapping("/camara/delete")
    public CamaraSchema.CamaraInsertSchema deleteCamara(@RequestBody CamaraSchema.CamaraInsertSchema camaraInsertSchema){
        logger.info("Deleting Camara {}", camaraInsertSchema.camaraName);
        try{
            Camara c = camarasConfiguration.getCamaras().get(camaraInsertSchema.camaraName);
            if(!c.isAtiva()){
                for(Gerente g : c.getGerentes()){
                    g.removeCamara(c);
                }
            }
            camarasConfiguration.removeCamara(c.getObjectId());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Deleting Camara {}", camaraInsertSchema.camaraName);
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
                Vacina.TipoVacina t = v.getTipo();
                vacinas.add( 
                    v.getVencimento() == null ? new VacinaSchema(t.getNome(), (float) t.getTempMax(), (float) t.getTempMin(), t.getTempoDescarte(), v.getAbastecimento(), v.getStatus()) :
                    new VacinaSchema(t.getNome(), (float) t.getTempMax(), (float) t.getTempMin(), t.getTempoDescarte(), v.getAbastecimento(), v.getVencimento(), v.getStatus())
                );
            }
            logger.info("Request will return {} Vacinas Info", vacinas.size());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Camara {}", camaraId);
        }

        return vacinas;
    }
    
    @PostMapping("/{camaraid}/abastecer")
    public VacinaSchema inserirVacina(@PathVariable("camaraid") String camaraId, @RequestBody VacinaSchema vacinaSchema){
        logger.info("Creating new Vacina {} in Camara {}", vacinaSchema.name, camaraId);
        try{
            Vacina.TipoVacina tipo = vacinaTiposConfiguration.getVacinaTipos().get(vacinaSchema.name);
            Camara c = camarasConfiguration.getCamaras().get(camaraId);
            c.addVacina(new Vacina(tipo, vacinaSchema.abastecimentoDate, vacinaSchema.vencimentoDate, null, false));
        }catch(Exception exception){
            logger.warn("Failed to Create Vacina {} in Camara {}", vacinaSchema.name, camaraId);
        }
        return vacinaSchema;
    }
}
