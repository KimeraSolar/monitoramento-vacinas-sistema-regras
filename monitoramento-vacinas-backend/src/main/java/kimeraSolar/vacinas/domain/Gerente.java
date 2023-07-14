package kimeraSolar.vacinas.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gerente extends MovingObject implements Serializable {
	
	private String nome;
	private List<Camara> camaras;
	private Map<Date, String> mensagens;

	Logger logger = LoggerFactory.getLogger(Gerente.class);
	
	public Gerente(String id, String nome) {
		super();
		this.nome = nome;
		this.setObjectId(id);
		this.mensagens = new HashMap<Date, String>();
		this.camaras = new LinkedList<Camara>();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void sendMensagem(String m) {
		this.mensagens.put(new Date(), m);
		// logger.info("Mensagem recebida pelo gerente" + this.nome + ": " + m);
	}

	public List<Camara> getCamaras() {
		return camaras;
	}

	public void setCamaras(List<Camara> camaras) {
		this.camaras = camaras;
	}
	
	public void addCamara(Camara camara) {
		this.camaras.add(camara);
	}

	public void removeCamara(Camara camara){
		this.camaras.remove(camara);
	}

	public Map<Date, String> getMensagens() {
		return mensagens;
	}

}
