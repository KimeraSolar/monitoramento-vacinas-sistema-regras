package kimeraSolar.vacinas.services.listeners;

import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kimeraSolar.vacinas.domain.Eventos.Alerta;

public class AlertaListener extends DefaultRuleRuntimeEventListener{

    Logger logger = LoggerFactory.getLogger(AlertaListener.class);
    
    @Override
    public void objectInserted(ObjectInsertedEvent event){
        if(event.getObject() instanceof Alerta){
            Alerta insertedAlerta = (Alerta) event.getObject();
            logger.info("Novo Alerta: ");
            logger.info(insertedAlerta.getLeituraTemperatura().getInicio().toString());
            logger.info(insertedAlerta.getInicio().toString());
        }
    } 
}
