package kimeraSolar.vacinas;

import java.io.File;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import kimeraSolar.ruleEngineManagement.domain.WorkingMemory;
import kimeraSolar.vacinas.backend.configuration.CamarasConfiguration;
import kimeraSolar.vacinas.backend.configuration.GerentesConfiguration;
import kimeraSolar.vacinas.domain.Camara;
import kimeraSolar.vacinas.domain.Gerente;
import kimeraSolar.vacinas.domain.GpsSensorWrapper;
import kimeraSolar.vacinas.domain.TempSensorWrapper;
import kimeraSolar.vacinas.domain.Vacina;
import kimeraSolar.vacinas.services.RuleEngine;

import javax.xml.parsers.DocumentBuilderFactory;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DroolsTest implements CommandLineRunner {

	@Autowired
	private GerentesConfiguration gerentesConfiguration;

	@Autowired
	private CamarasConfiguration camarasConfiguration;
	
	public final String INPUT_FILE = "config/teste_01.xml";

    public void run(String... args) {

		RuleEngine.startEngine(args);

		System.out.println("Inicializando Working Memory do MonitoraVax...");
		System.out.println("Regras inicializadas:");

        for(Rule rule : RuleEngine.ruleEngineManagement.listRules()){
            System.out.println(rule.toString());
        }

		WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
    	
        try {
        	// load up the knowledge base        	
	        
        	Map<String, Vacina.TipoVacina> tipos = new HashMap<String,Vacina.TipoVacina>();
        	Map<String, String> gpsMode = new HashMap<String, String>();
        	Map<String, String> tempMode = new HashMap<String, String>();
        
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
        		gerentesConfiguration.addGerente(g);
        		gpsMode.put(id, mode);
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("camara").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("camara").item(i).getAttributes();
        		
        		String id = n.getNamedItem("id").getTextContent();
        		String gps = n.getNamedItem("gps-sensor-mode").getTextContent();
        		String temp = n.getNamedItem("temp-sensor-mode").getTextContent();
        		
        		Camara c = new Camara(id);
        		camarasConfiguration.addCamara(c);
        		gpsMode.put(id, gps);
        		tempMode.put(id, temp);
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("vacina").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("vacina").item(i).getAttributes();
        		
        		String tipo = n.getNamedItem("tipo").getTextContent();
        		String c = n.getNamedItem("camara").getTextContent();
        		
        		Vacina v = new Vacina(tipos.get(tipo), new Date(), null, false);
        		RuleEngine.ruleEngineManagement.insertFact(v);
        		camarasConfiguration.getCamaras().get(c).addVacina(v);
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("responsabilidade").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("responsabilidade").item(i).getAttributes();
        		
        		String gerente = n.getNamedItem("gerente").getTextContent();
        		String camara = n.getNamedItem("camara").getTextContent();
        		
        		Camara c = camarasConfiguration.getCamaras().get(camara);
        		Gerente g = gerentesConfiguration.getGerentes().get(gerente);
        		c.addGerente(g); g.addCamara(c);
        	}

        	// Inicialização dos sensores
        	List<Thread> threads = new LinkedList<Thread>();

        	int sensorid = 0;
            for (Map.Entry<String, Camara> entry : camarasConfiguration.getCamaras().entrySet()) {
            	Camara c = entry.getValue();
            	FactHandle f = workingMemory.getKieSession().insert(c);
            	threads.add(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), workingMemory.getKieSession(), f, gpsMode.get(entry.getKey()))));
            	sensorid += 1;
            	threads.add(new Thread(new TempSensorWrapper("s" + String.format("%01d", sensorid), workingMemory.getKieSession(), f, tempMode.get(entry.getKey()))));
            	sensorid += 1;
            }
            
            for (Map.Entry<String, Gerente> entry : gerentesConfiguration.getGerentes().entrySet()) {
            	Gerente g = entry.getValue();
            	FactHandle f = workingMemory.getKieSession().insert(g);
            	threads.add(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), workingMemory.getKieSession(), f, gpsMode.get(entry.getKey()))));
            	sensorid += 1;
            }
            
            for (Thread t : threads) {
            	t.start();
            }
            
			for (Thread t : threads){
				t.join();
			}
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
