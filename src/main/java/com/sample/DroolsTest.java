package com.sample;

import java.io.BufferedReader;  
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class DroolsTest {
	
	// Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/vacinas/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     * @throws IOException 
     */
    public static HttpServer startServer() throws IOException {
        
    	final ResourceConfig rc = new ResourceConfig().packages("com.sample");
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, false);
        
        String userDirectory = System.getProperty("user.dir");
        StaticHttpHandler staticHttpHandler = new StaticHttpHandler(userDirectory);
        staticHttpHandler.setFileCacheEnabled(false);
        httpServer.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");
        
        httpServer.start();
        
        return httpServer;
           
    }

    public static final void main(String[] args) {
    	
        try {
        	// load up the knowledge base        	
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-rules");
        	
        	List<Vacina.TipoVacina> tipos = new LinkedList<Vacina.TipoVacina>();
        	List<Camara> camaras = new LinkedList<Camara>();
        	List<Gerente> gerentes = new LinkedList<Gerente>();
        	List<Vacina> vacinas = new LinkedList<Vacina>();
        	
        	Resources.gerentes = new HashMap<String, Gerente>();
        	Resources.camaras = new HashMap<String, Camara>();
        	
        	// TODO: ler a entrada de arquivo xml
        	// Leitura do arquivo de configuração de vacinas
        	Vacina.TipoVacina tipo1 = new Vacina.TipoVacina("CovidShield", -2, 8, 2000), tipo2 = new Vacina.TipoVacina("CoronaVac", -4, 6, 3000); 
        	
        	tipos.add(tipo1);
        	tipos.add(tipo2);
        	
        	Gerente g1 = new Gerente("g01", "Gerente 1"), g2 = new Gerente("g02", "Gerente 2");
        	gerentes.add(g1); gerentes.add(g2);
        	
        	for( Gerente g : gerentes) {
        		Resources.gerentes.put(g.getObjectId(), g); 
        		kSession.insert(g);
        	}
        	
        	Camara c1 = new Camara("c01"), c2 = new Camara("c02"), c3 = new Camara("c03");
        	
        	c1.addGerente(g1); g1.addCamara(c1);
        	c1.addGerente(g2); g2.addCamara(c1);
        	c2.addGerente(g1); g1.addCamara(c2);
        	c3.addGerente(g1); g1.addCamara(c3);
        	c3.addGerente(g2); g2.addCamara(c3);
        	
        	camaras.add(c1); camaras.add(c2); camaras.add(c3);
        	
        	for(Camara c : camaras) {
        		Resources.camaras.put(c.getObjectId(), c);
        		kSession.insert(c);
        	}
        	
        	Vacina v1 = new Vacina(tipo1, new Date(), null, false), 
				   v2 = new Vacina(tipo1, new Date(), null, false), 
        		   v3 = new Vacina(tipo2, new Date(), null, false), 
        		   v4 = new Vacina(tipo2, new Date(), null, false), 
        		   v5 = new Vacina(tipo2, new Date(), null, false);
        	
        	c1.addVacina(v1); c1.addVacina(v2); c1.addVacina(v3);
        	c2.addVacina(v4); c2.addVacina(v5);
        	
        	vacinas.add(v1); vacinas.add(v2); vacinas.add(v3);
        	vacinas.add(v4); vacinas.add(v5);
        	
        	for(Vacina v : vacinas) {
        		kSession.insert(v);
        	}
        	
        	// Inicialização dos sensores
        	List<Thread> threads = new LinkedList<Thread>();

        	int sensorid = 0;
            for (Camara c : camaras) {
            	FactHandle f = kSession.insert(c);
            	threads.add(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), kSession, f)));
            	sensorid += 1;
            	threads.add(new Thread(new TempSensorWrapper("s" + String.format("%01d", sensorid), kSession, f, "random")));
            	sensorid += 1;
            }
            
            for (Gerente g : gerentes) {
            	g.setCamaras(camaras);
            	FactHandle f = kSession.insert(g);
            	threads.add(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), kSession, f)));
            	sensorid += 1;
            }
            

            final HttpServer server = startServer();
            
            System.out.println(String.format("Jersey app started with WADL available at "
                    + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
            
            for (Thread t : threads) {
            	t.start();
            }
            
            for (Thread t : threads) {
            	t.join();
            }
            server.shutdown();
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
