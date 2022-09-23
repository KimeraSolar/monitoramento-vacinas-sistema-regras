package kimeraSolar.vacinas;

import java.util.Date;

public class Vacina {

	private TipoVacina tipo;
	private Date abastecimento;
	private Date retirada;
	private boolean descartada;

	
	public Vacina(TipoVacina tipo, Date abastecimento, Date retirada, boolean descartada) {
		super();
		this.tipo = tipo;
		this.abastecimento = abastecimento;
		this.retirada = retirada;
		this.descartada = descartada;
	}
	
	public static class TipoVacina{

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
