package com.sample;

import java.util.Map;

import com.sample.MovingObject.Location;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public class Resources {
	
	public static Map<String, Gerente> gerentes;
	public static Map<String, Camara> camaras;
	
	/* endpoint 1: retornar a lista de câmaras com o id e o status (ativa ou não) - precisa receber o id do gerente
     * endpoint 2: retornar a temperatura da câmara ao longo do tempo - precisa receber o id da câmara
     * endpoint 3: retornar a lista de vacinas da câmara com nome, data de vencimento do lote, temp. max, temp. min, % margem de segurança e data de abastecimento - precisa receber o id da câmara
     * endpoint 4: retornar a localização das câmaras - precisa receber o id do gerente
     * endpoint 5: retornar as mensagens de alerta - precisa receber o id do gerente
	 * */
	@Path("/{username}/camaras")
	public static class CamarasResource{
		
		public static class CamaraBrief{
			public CamaraBrief(boolean ativo, float temperatura, String id) {
				super();
				this.ativo = ativo;
				this.temperatura = temperatura;
				this.id = id;
			}
			public boolean ativo;
			public float temperatura;
			public String id;
			
		}
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public List<CamaraBrief> getCamaraBrief(@PathParam("username") String userName) {
			List<CamaraBrief> camarasbrief = new LinkedList<CamaraBrief>();
			Gerente g = gerentes.get(userName);
			
			for(Camara c : g.getCamaras()) {
				camarasbrief.add( new CamaraBrief(c.isAtiva(), c.getTemp(), c.getObjectId()) );
			}
			
			return camarasbrief;
		}
	}
	
	@Path("/{camaraid}/temperaturas")
	public static class CamaraTemperaturasResource{
		
		//TODO: Pegar temperatura x tempo do sistemas
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Map<Date, Float> getTemperaturas(@PathParam("camaraid") String camaraId){
			Map<Date, Float> response = new HashMap<Date, Float>();
			response.put(new Date(100000), (float) 32.0);
			response.put(new Date(), (float) 30.0);
			return response;
		}
	}
	
	@Path("/{camaraid}/vacinas")
	public static class VacinasResource{
		
		public static class VacinaBrief{
			
			public VacinaBrief(String nome, float tempMax, float tempMin, Date abastecimento, boolean descartada) {
				super();
				this.nome = nome;
				this.tempMax = tempMax;
				this.tempMin = tempMin;
				this.abastecimento = abastecimento;
				this.descartada = descartada;
			}
			
			public String nome;
			public float tempMax;
			public float tempMin;
			public Date abastecimento;
			public boolean descartada;
		
		}
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public List<VacinaBrief> getVacinas(@PathParam("camaraid") String camaraId){
			List<VacinaBrief> vacinas = new LinkedList<VacinaBrief>();
			Camara c = camaras.get(camaraId);
			Vacina v = c.getVacina();
			Vacina.TipoVacina t = v.getTipo();
			vacinas.add(new VacinaBrief(t.getNome(), (float) t.getTempMax(), (float) t.getTempMin(), v.getAbastecimento(), v.isDescartada()));
			return vacinas;
		}
	}
	
	@Path("/{username}/locations")
	public static class LocationsResource{
		
		public static class LocationObject{
			
			public LocationObject(String key, Location value) {
				super();
				this.key = key;
				this.value = value;
			}
			
			public String key;
			public MovingObject.Location value;
		}
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public List<LocationObject> getLocations(@PathParam("username") String userName){
			List<LocationObject> response = new LinkedList<LocationObject>();
			Gerente g = gerentes.get(userName);
			for (Camara c : g.getCamaras()) {
				response.add(new LocationObject(c.getObjectId(), c.getLocal()));
			}
			response.add(new LocationObject(g.getNome(), g.getLocal()));
			return response;
		}
	}
	
	@Path("/{username}/mensagens")
	public static class MensagensResource{
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Map<Date, String> getMensagens(@PathParam("username") String userName){
			return gerentes.get(userName).getMensagens();
		}
	}
}
