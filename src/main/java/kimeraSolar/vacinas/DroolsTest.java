package kimeraSolar.vacinas;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import kimeraSolar.vacinas.backend.Resources;
import kimeraSolar.vacinas.domain.Camara;
import kimeraSolar.vacinas.domain.Gerente;
import kimeraSolar.vacinas.domain.GpsSensorWrapper;
import kimeraSolar.vacinas.domain.TempSensorWrapper;
import kimeraSolar.vacinas.domain.Vacina;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class DroolsTest {
	
	// Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/vacinas/";
    public static final String INPUT_FILE = "config/teste_01.xml";
    
    @Provider
    public static class CORSFilter implements ContainerResponseFilter {

    	   public void filter(ContainerRequestContext request, ContainerResponseContext response) {

    		   response.getHeaders().add("Access-Control-Allow-Origin", "*");
    	       response.getHeaders().add("Access-Control-Allow-Headers", "CSRF-Token, X-Requested-By, Authorization, Content-Type");
    	       response.getHeaders().add("Access-Control-Allow-Credentials", "true");
    	       response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    	    }
   	   }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     * @throws IOException 
     */
    public static HttpServer startServer() throws IOException {
        
    	final ResourceConfig rc = new ResourceConfig().packages("com.sample");
    	
    	rc.register(CORSFilter.class);
    	rc.register(new CORSFilter());
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        
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
        	
        	Map<String, Vacina.TipoVacina> tipos = new HashMap<String,Vacina.TipoVacina>();
        	Map<String, String> gpsMode = new HashMap<String, String>();
        	Map<String, String> tempMode = new HashMap<String, String>();
        	
        	Resources.gerentes = new HashMap<String, Gerente>();
        	Resources.camaras = new HashMap<String, Camara>();
        	
        	// Leitura do arquivo de configuração de vacinas
        	File file = new File(INPUT_FILE);
        	Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        	
        	// Leitura dos tipos de vacina
        	for( int i = 0; i < document.getElementsByTagName("tipo-vacina").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("tipo-vacina").item(i).getAttributes();
        		
        		String nome = n.getNamedItem("nome").getTextContent();
        		float tempMin = Float.parseFloat(n.getNamedItem("tempMin").getTextContent());
        		float tempMax = Float.parseFloat(n.getNamedItem("tempMax").getTextContent());
        		long tempo = Long.parseLong(n.getNamedItem("tempo").getTextContent());
        		
        		tipos.put(nome, new Vacina.TipoVacina(nome, tempMin, tempMax, tempo));
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("gerente").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("gerente").item(i).getAttributes();
        		
        		String nome = n.getNamedItem("nome").getTextContent();
        		String id = n.getNamedItem("id").getTextContent();
        		String mode = n.getNamedItem("gps-sensor-mode").getTextContent();
        		
        		Gerente g = new Gerente(id, nome);
        		Resources.gerentes.put(id, g);
        		gpsMode.put(id, mode);
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("camara").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("camara").item(i).getAttributes();
        		
        		String id = n.getNamedItem("id").getTextContent();
        		String gps = n.getNamedItem("gps-sensor-mode").getTextContent();
        		String temp = n.getNamedItem("temp-sensor-mode").getTextContent();
        		
        		Camara c = new Camara(id);
        		Resources.camaras.put(id, c);
        		gpsMode.put(id, gps);
        		tempMode.put(id, temp);
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("vacina").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("vacina").item(i).getAttributes();
        		
        		String tipo = n.getNamedItem("tipo").getTextContent();
        		String c = n.getNamedItem("camara").getTextContent();
        		
        		Vacina v = new Vacina(tipos.get(tipo), new Date(), null, false);
        		kSession.insert(v);
        		Resources.camaras.get(c).addVacina(v);
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("responsabilidade").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("responsabilidade").item(i).getAttributes();
        		
        		String gerente = n.getNamedItem("gerente").getTextContent();
        		String camara = n.getNamedItem("camara").getTextContent();
        		
        		Camara c = Resources.camaras.get(camara);
        		Gerente g = Resources.gerentes.get(gerente);
        		c.addGerente(g); g.addCamara(c);
        	}

        	// Inicialização dos sensores
        	List<Thread> threads = new LinkedList<Thread>();

        	int sensorid = 0;
            for (Map.Entry<String, Camara> entry : Resources.camaras.entrySet()) {
            	Camara c = entry.getValue();
            	FactHandle f = kSession.insert(c);
            	threads.add(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), kSession, f, gpsMode.get(entry.getKey()))));
            	sensorid += 1;
            	threads.add(new Thread(new TempSensorWrapper("s" + String.format("%01d", sensorid), kSession, f, tempMode.get(entry.getKey()))));
            	sensorid += 1;
            }
            
            for (Map.Entry<String, Gerente> entry : Resources.gerentes.entrySet()) {
            	Gerente g = entry.getValue();
            	FactHandle f = kSession.insert(g);
            	threads.add(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), kSession, f, gpsMode.get(entry.getKey()))));
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
