package kimeraSolar.vacinas.backend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kimeraSolar.vacinas.backend.configuration.GerentesConfiguration;
import kimeraSolar.vacinas.domain.Camara;
import kimeraSolar.vacinas.domain.Gerente;
import kimeraSolar.vacinas.domain.MovingObject.Location;

@RestController
@RequestMapping("/vacinas")
public class GerenteController {

    Logger logger = LoggerFactory.getLogger(GerenteController.class);

    @Autowired
    private GerentesConfiguration gerentesConfiguration;

    protected Gerente getGerente(String gerenteId){
        return gerentesConfiguration.getGerentes().get(gerenteId);
    }

    public static class CamaraBrief{
        public CamaraBrief(boolean ativo, float temperatura, String id) {
            super();
            this.ativo = ativo;
            this.temperatura = temperatura;
            this.id = id;
        }
        public boolean ativo;
        public float temperatura;
        public String id;
        
    }

    @GetMapping("/{username}/camaras")
    public List<CamaraBrief> getCamarasBrief(@PathVariable("username") String username){
        logger.info("Requesting Camaras Info from Gerente {}", username);
        List<CamaraBrief> camarasBrief = new ArrayList<>();
        try{
            Gerente g = getGerente(username);

            for(Camara c : g.getCamaras()){
                camarasBrief.add(
                    new CamaraBrief(c.isAtiva(), c.getTemp(), c.getObjectId())
                );
            }
            logger.info("Request will return {} Camaras Info", camarasBrief.size());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Gerente {}", username);
        }

        return camarasBrief;
    }

    public static class LocationObject{
			
        public LocationObject(String key, Location value) {
            super();
            this.key = key;
            this.value = value;
        }
        
        public String key;
        public Location value;
    }

    @GetMapping("/{username}/locations")
    public List<LocationObject> getLocations(@PathVariable("username") String username){
        logger.info("Requesting Locations from Gerente {}", username);
        
        List<LocationObject> locations = null;
        try{
            locations = new ArrayList<>();
            Gerente g = getGerente(username);
            for( Camara c : g.getCamaras() ){
                locations.add(
                    new LocationObject(c.getObjectId(), c.getLocal())
                );
            }
            locations.add(
                new LocationObject("Gerente", g.getLocal())
            );
            logger.info("Request will return {} Locations Info", locations.size());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Gerente {}", username);
        }

        return locations;
    }

    public static class MensagemObject{

        public MensagemObject(Date date, String mensagem){
            this.key = date;
            this.value = mensagem;
        }

        public Date key;
        public String value;
    }

    @GetMapping("/{username}/mensagens")
    public List<MensagemObject> getMensagens(@PathVariable("username") String username){
        logger.info("Requesting Mensagens Info from Gerente {}", username);
        
       List<MensagemObject> mensagens = null;
        try{
            mensagens = new ArrayList<>();
            Gerente g = getGerente(username);
            for( Map.Entry<Date, String> mensagem : g.getMensagens().entrySet()){
                mensagens.add(
                    new MensagemObject(mensagem.getKey(), mensagem.getValue())
                );
            }
            logger.info("Request will return {} Mensagens Info", mensagens.size());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Gerente {}", username);
        }

        return mensagens;
    }

    public static class GerenteObject{
        public GerenteObject(String key, String value) {
            super();
            this.key = key;
            this.value = value;
        }
        
        public String key;
        public String value;
    }

    @GetMapping("/gerentes")
    public List<GerenteObject> getGerentes(){
        logger.info("Requesting Gerentes Info");
        List<GerenteObject> response = new ArrayList<>();
        for(Map.Entry<String, Gerente> entry : gerentesConfiguration.getGerentes().entrySet()) {
            response.add(new GerenteObject(entry.getKey(), entry.getValue().getNome()));
        }
        logger.info("Request will return {} Gerentes Info", response.size());
        return response;
    }
}
