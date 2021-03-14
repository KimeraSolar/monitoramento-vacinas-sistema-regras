package com.sample;


import java.util.LinkedList;
import java.util.List;
import java.util.Date;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class DroolsTest {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-rules");
        	
        	Vacina.TipoVacina[] tipos = {	new Vacina.TipoVacina("CoronaVac", 2, 8, 30*24*60*60*1000),
        									new Vacina.TipoVacina("Covidshield", 2, 8, 20*24*60*60*1000) };
        	
        	Gerente[] gerentes = {	new Gerente("Gerente 01", "g01"), 
        							new Gerente("Gerente 02", "g02") /*, 
        							new Gerente("Gerente 03", "g03"), 
        							new Gerente("Gerente 04", "g04"), 
        							new Gerente("Gerente 05", "g05")*/	};
        	
        	Camara[] camaras = {	new Camara("c01", new Vacina(tipos[1], new Date(), null, false)),
        							new Camara("c02", new Vacina(tipos[1], new Date(), null, false)),
        							new Camara("c03", new Vacina(tipos[1], new Date(), null, false)),
        							new Camara("c04", new Vacina(tipos[1], new Date(), null, false)),
        							new Camara("c05", new Vacina(tipos[1], new Date(), null, false)),
        							new Camara("c06", new Vacina(tipos[0], new Date(), null, false)),
        							new Camara("c07", new Vacina(tipos[0], new Date(), null, false)),
        							new Camara("c08", new Vacina(tipos[0], new Date(), null, false)),
        							new Camara("c09", new Vacina(tipos[0], new Date(), null, false)),
        							new Camara("c10", new Vacina(tipos[0], new Date(), null, false))	};
        	
        	List<Thread> threads = new LinkedList<Thread>();

            // go !
        	int sensorid = 0;
            for (Camara c : camaras) {
            	FactHandle f = kSession.insert(c);
            	threads.add(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), kSession, f)));
            	sensorid += 1;
            	threads.add(new Thread(new TempSensorWrapper("s" + String.format("%01d", sensorid), kSession, f)));
            	sensorid += 1;
            }
            
            for (Gerente g : gerentes) {
            	FactHandle f = kSession.insert(g);
            	threads.add(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), kSession, f)));
            	sensorid += 1;
            }
            
            for (Thread t : threads) {
            	t.start();
            }
            
            for (Thread t : threads) {
            	t.join();
            }
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
