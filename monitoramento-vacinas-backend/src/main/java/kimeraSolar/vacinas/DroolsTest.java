package kimeraSolar.vacinas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import kimeraSolar.ruleEngineManagement.domain.WorkingMemory;
import kimeraSolar.vacinas.backend.configuration.CamarasConfiguration;
import kimeraSolar.vacinas.backend.configuration.GerentesConfiguration;
import kimeraSolar.vacinas.backend.configuration.SensorsConfiguration;
import kimeraSolar.vacinas.backend.configuration.VacinaTiposConfiguration;
import kimeraSolar.vacinas.backend.configuration.VacinasConfiguration;
import kimeraSolar.vacinas.domain.Camara;
import kimeraSolar.vacinas.domain.Gerente;
import kimeraSolar.vacinas.domain.GpsSensorWrapper;
import kimeraSolar.vacinas.domain.TempSensorWrapper;
import kimeraSolar.vacinas.domain.Vacina;
import kimeraSolar.vacinas.domain.MovingObject.Location;
import kimeraSolar.vacinas.services.RuleEngine;
import kimeraSolar.vacinas.services.listeners.AlertaListener;
import kimeraSolar.vacinas.services.listeners.PerigoListener;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DroolsTest implements CommandLineRunner {

	@Autowired
	private GerentesConfiguration gerentesConfiguration;

	@Autowired
	private CamarasConfiguration camarasConfiguration;

	@Autowired
	private SensorsConfiguration sensorsConfiguration;
	
    @Autowired
	private VacinasConfiguration vacinasConfiguration;
	 
	@Autowired
    private VacinaTiposConfiguration vacinaTiposConfiguration;

	@Autowired
    private RuleEngine ruleEngine;

	static Logger logger = LoggerFactory.getLogger(DroolsTest.class);

    public void run(String... args) {
		for( String arg : args){
			if (arg.startsWith("test_mode=") && arg.contains("performance_test")){
				logger.info("Iniciando teste de performance");
				long start_time = System.currentTimeMillis();
				try {
					performance_test(args);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				long final_time = System.currentTimeMillis();
				logger.info("Finalizando teste de performance");
				logger.info("Tempo total do teste: " + (final_time - start_time) + " ms");
				System.exit(0);
			}
		}
		start_engine(args);
		test_01(args);
	}

	public void start_engine( String[] args){
		
		ruleEngine.startEngine(args);
		
		logger.info("Inicializando Working Memory do MonitoraVax...");
		logger.info("Regras inicializadas:");
		
        for(Rule rule : ruleEngine.ruleEngineManagement.listRules()){
            logger.info(rule.toString());
        }

    }

	public void performance_test(String... args) throws FileNotFoundException{
		long sim_time = 300000;
		int cod_test = 1;
		int test_repeat = 3;

		PrintStream stdout = System.out;

		// Primeiro teste - uma única câmara
		{
			int n_vacinas = 10;
			int n_camaras = 1;
			int n_gerentes = 1;
			int n_cidades = 1;
			int n_estados = 1;
			int n_paises = 1;			
			
			for (int i = 1; i <= test_repeat; i++){
				System.setOut(new PrintStream(new FileOutputStream("results/logs/teste_" + String.format("%03d", cod_test) + ".log" )));
				logger.info("Primeiro teste - uma unica camara");
				logger.info("Iniciando teste " + i);

				start_engine(args);
				execute_performance_test(cod_test, sim_time, n_vacinas, n_camaras, n_gerentes, n_cidades, n_estados, n_paises);
				
				cod_test++;
			}

		}

		// Segundo teste - uma unidade de saude (10 camaras)
		{
			int n_vacinas = 10;
			int n_camaras = 10;
			int n_gerentes = 1;
			int n_cidades = 1;
			int n_estados = 1;
			int n_paises = 1;

			for (int i = 1; i <= test_repeat; i++){
				System.setOut(new PrintStream(new FileOutputStream("results/logs/teste_" + String.format("%03d", cod_test) + ".log" )));
				logger.info("Segundo teste - uma unidade de saude (10 camaras)");
				logger.info("Iniciando teste " + i);

				start_engine(args);
				execute_performance_test(cod_test, sim_time, n_vacinas, n_camaras, n_gerentes, n_cidades, n_estados, n_paises);
				
				cod_test++;
			}
		}

		// Terceiro teste - uma cidade (100 camaras)
		{
			int n_vacinas = 10;
			int n_camaras = 10;
			int n_gerentes = 10;
			int n_cidades = 1;
			int n_estados = 1;
			int n_paises = 1;

			for (int i = 1; i <= test_repeat; i++){
				System.setOut(new PrintStream(new FileOutputStream("results/logs/teste_" + String.format("%03d", cod_test) + ".log" )));
				logger.info("Terceiro teste - uma cidade (100 camaras)");
				logger.info("Iniciando teste " + i);

				start_engine(args);
				execute_performance_test(cod_test, sim_time, n_vacinas, n_camaras, n_gerentes, n_cidades, n_estados, n_paises);
				
				cod_test++;
			}
		}

		// Quarto teste - um estado (1000 camaras)
		{
			int n_vacinas = 10;
			int n_camaras = 10;
			int n_gerentes = 10;
			int n_cidades = 10;
			int n_estados = 1;
			int n_paises = 1;

			for (int i = 1; i <= test_repeat; i++){
				System.setOut(new PrintStream(new FileOutputStream("results/logs/teste_" + String.format("%03d", cod_test) + ".log" )));
				logger.info("Quarto teste - um estado (1000 camaras)");
				logger.info("Iniciando teste " + i);

				start_engine(args);
				execute_performance_test(cod_test, sim_time, n_vacinas, n_camaras, n_gerentes, n_cidades, n_estados, n_paises);
				
				cod_test++;
			}
		}

		// Quinto teste - um país (5000 camaras)
		{
			int n_vacinas = 10;
			int n_camaras = 10;
			int n_gerentes = 10;
			int n_cidades = 10;
			int n_estados = 5;
			int n_paises = 1;

			for (int i = 1; i <= test_repeat; i++){
				System.setOut(new PrintStream(new FileOutputStream("results/logs/teste_" + String.format("%03d", cod_test) + ".log" )));
				logger.info("Quinto teste - um país (5000 camaras)");
				
				start_engine(args);
				execute_performance_test(cod_test, sim_time, n_vacinas, n_camaras, n_gerentes, n_cidades, n_estados, n_paises);
				
				cod_test++;
			}
		}

		System.setOut(stdout); 

	}

	public void execute_performance_test(int test_code, long sim_time, int n_vacinas, int n_camaras, int n_gerentes, int n_cidades, int n_estados, int n_paises){
		
		WorkingMemory workingMemory = ruleEngine.ruleEngineManagement.getWorkingMemory();

		AlertaListener alertaListener = new AlertaListener();
		PerigoListener perigoListener = new PerigoListener();
		workingMemory.getKieSession().addEventListener(alertaListener);
		workingMemory.getKieSession().addEventListener(perigoListener);

		try{	
			// Inicializa working memory
			for(int pais = 1; pais <= n_paises; pais++){
				String cod_pais = "P" + String.format("%03d", pais);
				
				for(int estado = 1; estado <= n_estados; estado++){
					String cod_estado = cod_pais + "E" + String.format("%03d", estado);
					
					for(int cidade = 1; cidade <= n_cidades; cidade++){
						String cod_cidade = cod_estado + "Ci" + String.format("%03d", cidade);
						
						// Inicializa gerentes (1 para cada unidade de saúde simulada) 
						for(int gerente = 1; gerente <= n_gerentes; gerente++){
							String cod_gerente = cod_cidade + "G" + String.format("%03d", gerente);
							
							Gerente g = new Gerente(cod_gerente, cod_gerente);
							
							// Inicializa camaras
							for (int camara = 1; camara <= n_camaras; camara++){
								String cod_camara = cod_gerente + "Ca" + String.format("%03d", camara);
								
								Camara c = new Camara(cod_camara);
								
								// Relaciona gerente e câmara
								c.addGerente(g);
								g.addCamara(c);

								// Inicializa vacina
								for (int vacina = 1; vacina <= n_vacinas; vacina++){
									String cod_vacina = cod_camara + "V" + String.format("%03d", vacina);
									int minTemp = -2;
									int maxTemp = 8;
									Vacina.TipoVacina tipo = new Vacina.TipoVacina(
											cod_vacina, 
											(int) ((Math.random() * (minTemp - (-6))) + (-6)), 
											(int) ((Math.random() * (12 - maxTemp)) + maxTemp), 
											sim_time
										);
									Vacina v = new Vacina(
										tipo,
										cod_vacina,
										new Date(),
										null,
										false
									);

									logger.info("Nova vacina: " + v.getTipo().toString());

									// Insere vacina criada na câmara e na working memory
									c.addVacina(v);
									vacinasConfiguration.addVacina(v);
									vacinaTiposConfiguration.addVacinaTipo(tipo);
								}

								// Insere camara criada na working memory e inicializa os sensores
								camarasConfiguration.addCamara(c);
								Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(cod_camara);
								sensorsConfiguration.addSensorWrapper(new Thread(new GpsSensorWrapper(cod_camara + "GPS", workingMemory.getKieSession(), camaraPair.getRight(), "static")));
								sensorsConfiguration.addSensorWrapper(new Thread(new TempSensorWrapper(cod_camara + "TempSensor", workingMemory.getKieSession(), camaraPair.getRight(), "smooth")));
							}

							// Insere gerente criado na working memory e inicializa os sensores
							gerentesConfiguration.addGerente(g);
							Pair<Gerente, FactHandle> gerentePair = gerentesConfiguration.getGerente(cod_gerente);
							sensorsConfiguration.addSensorWrapper(new Thread(new GpsSensorWrapper(cod_gerente, workingMemory.getKieSession(), gerentePair.getRight(), "static")));
						}
					}
				}
			}
			
			logger.info("Inicializando Threads");

			long start_time = System.currentTimeMillis();

			for(Thread t: sensorsConfiguration.getSensorWrappers()){
				t.start();
			}

			try{
				Thread.sleep(sim_time);
			} catch(InterruptedException ex){
				Thread.currentThread().interrupt();
			}

			for(Thread t: sensorsConfiguration.getSensorWrappers()){
				t.interrupt();
			}

			for(Thread t: sensorsConfiguration.getSensorWrappers()){
				t.join();
			}

			long end_time = System.currentTimeMillis();

			logger.info("Threads finalizadas");
			logger.info("Simulation time: " + (end_time - start_time) + " ms");

		}catch(Throwable t){
			t.printStackTrace();
		}

		logger.info("Escrevendo relatórios");
		
		logger.info("Relatório de alertas: results/performance_data/teste_" + String.format("%03d", test_code) + "_alertas.csv");
		alertaListener.writeReport("results/performance_data/teste_" + String.format("%03d", test_code) + "_alertas.csv");
		
		logger.info("Relatório de perigos: results/performance_data/teste_" + String.format("%03d", test_code) + "_perigos.csv");
		perigoListener.writeReport("results/performance_data/teste_" + String.format("%03d", test_code) + "_perigos.csv");

		logger.info("Imagem da working memory: results/working_memories/teste_" + String.format("%03d", test_code) + "_workingmemory.save");
		ruleEngine.ruleEngineManagement.saveWorkingMemory("results/working_memories/teste_" + String.format("%03d", test_code) + "_workingmemory");
		
	}

	public void test_01(String... args){

		String INPUT_FILE = "config/teste_frontend.xml";

		for(String arg : args){
			if (arg.startsWith("test_file=")){
				INPUT_FILE = arg.substring(10);
			}
		}

		logger.info("Input file: " + INPUT_FILE);

		WorkingMemory workingMemory = ruleEngine.ruleEngineManagement.getWorkingMemory();
    	
        try {
        	// load up the knowledge base        	
	        
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
        		
        		vacinaTiposConfiguration.addVacinaTipo(new Vacina.TipoVacina(nome, tempMin, tempMax, tempo));
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("gerente").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("gerente").item(i).getAttributes();
        		
        		String nome = n.getNamedItem("nome").getTextContent();
        		String id = n.getNamedItem("id").getTextContent();
        		String mode = n.getNamedItem("gps-sensor-mode").getTextContent();
				String[] initial_location = n.getNamedItem("gps-sensor-initial-location").getTextContent().split(",");
        		
        		Gerente g = new Gerente(id, nome);
				g.setLocal(new Location(Float.parseFloat(initial_location[0]), Float.parseFloat(initial_location[1])));
        		gerentesConfiguration.addGerente(g);
        		gpsMode.put(id, mode);
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("camara").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("camara").item(i).getAttributes();
        		
        		String id = n.getNamedItem("id").getTextContent();
        		String gps = n.getNamedItem("gps-sensor-mode").getTextContent();
        		String temp = n.getNamedItem("temp-sensor-mode").getTextContent();
				String[] initial_location = n.getNamedItem("gps-sensor-initial-location").getTextContent().split(",");
        		

        		Camara c = new Camara(id);
				c.setLocal(new Location(Float.parseFloat(initial_location[0]), Float.parseFloat(initial_location[1])));
				camarasConfiguration.addCamara(c);
        		gpsMode.put(id, gps);
        		tempMode.put(id, temp);
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("vacina").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("vacina").item(i).getAttributes();
        		
        		String tipo = n.getNamedItem("tipo").getTextContent();
        		String c = n.getNamedItem("camara").getTextContent();
        		Vacina.TipoVacina t = vacinaTiposConfiguration.getVacinaTipos().get(tipo);
        		Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(c);
        		Vacina v = new Vacina(t, camaraPair.getLeft().getObjectId() + t.getNome() + vacinasConfiguration.getVacinas().size(), new Date(), null, false);
        		vacinasConfiguration.addVacina(v);
				camaraPair.getLeft().addVacina(v);
				workingMemory.getKieSession().update(camaraPair.getRight(), camaraPair.getLeft());
        	}
        	
        	for( int i = 0; i < document.getElementsByTagName("responsabilidade").getLength(); i++) {
        		NamedNodeMap n = document.getElementsByTagName("responsabilidade").item(i).getAttributes();
        		
        		String gerente = n.getNamedItem("gerente").getTextContent();
        		String camara = n.getNamedItem("camara").getTextContent();
        		
        		Pair<Camara, FactHandle> camaraPair = camarasConfiguration.getCamara(camara);
        		Pair<Gerente,FactHandle> gerentePair = gerentesConfiguration.getGerente(gerente);
        		camaraPair.getLeft().addGerente(gerentePair.getLeft()); 
				gerentePair.getLeft().addCamara(camaraPair.getLeft());
				workingMemory.getKieSession().update(gerentePair.getRight(), gerentePair.getLeft());
                workingMemory.getKieSession().update(camaraPair.getRight(), camaraPair.getLeft());
            
        	}

        	// Inicialização dos sensores
        	int sensorid = 0;
            for (Map.Entry<String, Camara> entry : camarasConfiguration.getCamaras().entrySet()) {
            	Camara c = entry.getValue();
            	FactHandle f = camarasConfiguration.getCamarasFactHandle().get(c.getObjectId());
				sensorsConfiguration.addSensorWrapper(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), workingMemory.getKieSession(), f, gpsMode.get(entry.getKey()), c.getLocal())));
            	sensorid += 1;
            	sensorsConfiguration.addSensorWrapper(new Thread(new TempSensorWrapper("s" + String.format("%01d", sensorid), workingMemory.getKieSession(), f, tempMode.get(entry.getKey()))));
            	sensorid += 1;
            }
            
            for (Map.Entry<String, Gerente> entry : gerentesConfiguration.getGerentes().entrySet()) {
            	Gerente g = entry.getValue();
            	FactHandle f = gerentesConfiguration.getGerentesFactHandle().get(g.getObjectId());
            	sensorsConfiguration.addSensorWrapper(new Thread(new GpsSensorWrapper("s" + String.format("%01d", sensorid), workingMemory.getKieSession(), f, gpsMode.get(entry.getKey()), g.getLocal())));
            	sensorid += 1;
            }

			logger.info("Inicializando Threads");
            
            for (Thread t : sensorsConfiguration.getSensorWrappers()) {
            	t.start();
            }

			for (Thread t : sensorsConfiguration.getSensorWrappers()){
				t.join();
			}

			logger.info("Threads finalizadas");
        } catch (Throwable t) {
            t.printStackTrace();
        }
		
    }

	public void test_02(String... args){
		WorkingMemory workingMemory = ruleEngine.ruleEngineManagement.getWorkingMemory();
		AlertaListener alertaListener = new AlertaListener();
		PerigoListener perigoListener = new PerigoListener();
    	workingMemory.getKieSession().addEventListener(alertaListener);
		workingMemory.getKieSession().addEventListener(perigoListener);
		test_01(args);
		logger.info("Escrevendo relatórios");
		alertaListener.writeReport("teste_alertas.csv");
		perigoListener.writeReport("teste_perigos.csv");
	}
}
