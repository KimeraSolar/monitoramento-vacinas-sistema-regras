package kimeraSolar.vacinas.services.listeners;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kimeraSolar.vacinas.domain.Eventos.Perigo;

public class PerigoListener extends DefaultRuleRuntimeEventListener {
    Logger logger = LoggerFactory.getLogger(PerigoListener.class);

    List<Pair<Timestamp, Timestamp>> perigos = new LinkedList<>();
    
    @Override
    public void objectInserted(ObjectInsertedEvent event){
        if(event.getObject() instanceof Perigo){
            Perigo insertedPerigo = (Perigo) event.getObject();

            perigos.add(Pair.of(insertedPerigo.getLeituraTemperatura().getInicio(), insertedPerigo.getInicio()));

            logger.info("Novo Perigo: ");
            logger.info(insertedPerigo.getLeituraTemperatura().getInicio().toString());
            logger.info(insertedPerigo.getInicio().toString());
        }
    } 

    public void writeReport(String file_name){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file_name));
            writer.write("Timestamp Leitura,Timestamp Alerta\n");
            for (Pair<Timestamp, Timestamp> p : this.perigos){
                writer.write(p.getLeft() + "," + p.getRight() + "\n");
            }
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
