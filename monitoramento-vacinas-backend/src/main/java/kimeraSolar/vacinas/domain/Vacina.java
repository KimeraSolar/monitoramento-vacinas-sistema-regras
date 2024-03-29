package kimeraSolar.vacinas.domain;

import java.io.Serializable;
import java.util.Date;

public class Vacina implements Serializable {

	private TipoVacina tipo;
	private String vacinaId;
	private Date abastecimento;
	private Date vencimento;
	private Date retirada;
	private boolean descartada;
	private boolean hasPerigo;
	private boolean hasAlerta;

	
	public Vacina(TipoVacina tipo, String vacinaId, Date abastecimento, Date retirada, boolean descartada) {
		super();
		this.tipo = tipo;
		this.vacinaId = vacinaId;
		this.abastecimento = abastecimento;
		this.retirada = retirada;
		this.descartada = descartada;
		this.hasAlerta = false;
		this.hasPerigo = false;
	}

	public Vacina(TipoVacina tipo, String vacinaId, Date abastecimento, Date vencimento, Date retirada, boolean descartada) {
		super();
		this.tipo = tipo;
		this.vacinaId = vacinaId;
		this.abastecimento = abastecimento;
		this.vencimento = vencimento;
		this.retirada = retirada;
		this.descartada = descartada;
		this.hasAlerta = false;
		this.hasPerigo = false;
	}

	public void setVencimento(Date vencimento){
		this.vencimento = vencimento;
	}

	public Date getVencimento(){
		return this.vencimento;
	}

	public String getVacinaId(){
		return this.vacinaId;
	}

	public void setPerigo(boolean perigo){
		this.hasPerigo = perigo;
	}

	public void setAlerta(boolean alerta){
		this.hasAlerta = alerta;
	}

	public String getStatus(){
		if(this.isDescartada()) return "Descartada";
		if(this.hasAlerta) return "Alerta";
		if(this.hasPerigo) return "Perigo";
		return "Apropriada";
	}

	public static class TipoVacina implements Serializable{

		private String nome;
		private double tempMin;
		private double tempMax;
		private long tempoDescarte;

		public TipoVacina(String nome, double tempMin, double tempMax, long tempoDescarte) {
			super();
			this.nome = nome;
			this.tempMin = tempMin;
			this.tempMax = tempMax;
			this.tempoDescarte = tempoDescarte;
		}
		
		public String getNome() {
			return nome;
		}
		
		public void setNome(String nome) {
			this.nome = nome;
		}
		
		public double getTempMin() {
			return tempMin;
		}
		
		public void setTempMin(double tempMin) {
			this.tempMin = tempMin;
		}
		
		public double getTempMax() {
			return tempMax;
		}
		
		public void setTempMax(double tempMax) {
			this.tempMax = tempMax;
		}
		
		public long getTempoDescarte() {
			return tempoDescarte;
		}
		
		public void setTempoDescarte(long tempoDescarte) {
			this.tempoDescarte = tempoDescarte;
		}

		public String toString(){
			return "Tipo de Vacina: " + this.getNome() + " { minTemp: " + this.getTempMin() + ", maxTemp: " + this.getTempMax() + ", descarte: " + this.getTempoDescarte() + " }";
		}
		
		
	}

	public TipoVacina getTipo() {
		return tipo;
	}

	public void setTipo(TipoVacina tipo) {
		this.tipo = tipo;
	}

	public Date getAbastecimento() {
		return abastecimento;
	}

	public void setAbastecimento(Date abastecimento) {
		this.abastecimento = abastecimento;
	}

	public Date getRetirada() {
		return retirada;
	}

	public void setRetirada(Date retirada) {
		this.retirada = retirada;
	}

	public boolean isDescartada() {
		return descartada;
	}

	public void setDescartada(boolean descartada) {
		this.descartada = descartada;
	}

}
