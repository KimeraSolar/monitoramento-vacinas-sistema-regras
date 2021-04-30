package com.sample;

import java.io.BufferedReader;  
import java.io.FileReader; 
import java.util.LinkedList;
import java.util.List;
import java.util.Date;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
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
        	
        	
        	
        	List<Vacina.TipoVacina> tipos = new LinkedList<Vacina.TipoVacina>();
        	List<Camara> camaras = new LinkedList<Camara>();
        	List<Gerente> gerentes = new LinkedList<Gerente>();
        	
        	// Leitura do arquivo de configuração de vacinas
        	BufferedReader br = new BufferedReader(new FileReader("config\\TiposVacina.csv"));
        	String line = br.readLine();
        	
        	while ((line = br.readLine()) != null)  
        	{  
        		String[] tipo = line.split(",");   
        		tipos.add(new Vacina.TipoVacina(tipo[0], Integer.parseInt(tipo[1]), Integer.parseInt(tipo[2]), Integer.parseInt(tipo[3])));
        	}
        	
        	br.close();
        	
        	// Leitura do arquivo de configuração de gerentes
        	br = new BufferedReader(new FileReader("config\\Gerentes.csv"));
        	line = br.readLine();
        	
        	while ((line = br.readLine()) != null)  
        	{  
        		String[] gerente = line.split(",");   
        		gerentes.add(new Gerente(gerente[0], gerente[1]));
        	}
        	
        	br.close();
        	
        	// Leitura do arquivo de configuração de camaras
        	br = new BufferedReader(new FileReader("config\\Camaras.csv"));
        	line = br.readLine();
        	
        	while ((line = br.readLine()) != null)   
        	{  
        		String[] camara = line.split(",");    
        		camaras.add(new Camara(camara[0], new Vacina(tipos.get(Integer.parseInt(camara[1])), new Date(), null, false), gerentes ) );
        	}
        	
        	br.close();
        	
        	// Inicialização dos sensores
        	List<Thread> threads = new LinkedList<Thread>();

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
