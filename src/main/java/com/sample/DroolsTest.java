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
        // create a resource config that scans for JAX-RS resources and providers
        // in sbr.tbservice package
    	
    	//Criamos 5 recursos para este serviço: Febre, ContactUderVigilance, Location, Temperatura, SistuationSuspiciousTuberculosis
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
        	
        	Resources.gerentes = new HashMap<String, Gerente>();
        	Resources.camaras = new HashMap<String, Camara>();
        	
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
        		Gerente g = new Gerente(gerente[0], gerente[1]);
        		gerentes.add(g);
        		Resources.gerentes.put(g.getNome(), g);
        	}
        	
        	br.close();
        	
        	// Leitura do arquivo de configuração de camaras
        	br = new BufferedReader(new FileReader("config\\Camaras.csv"));
        	line = br.readLine();
        	
        	while ((line = br.readLine()) != null)   
        	{  
        		String[] camara = line.split(",");
        		Camara c = new Camara(camara[0], new Vacina(tipos.get(Integer.parseInt(camara[1])), new Date(), null, false), gerentes );
        		camaras.add(c);
        		Resources.camaras.put(c.getObjectId(), c);
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
