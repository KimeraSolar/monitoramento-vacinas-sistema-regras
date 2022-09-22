package com.sample;

import java.util.Random;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class GpsSensorWrapper implements Runnable {
	
	KieSession kSession;
	FactHandle fact;
	Random rand;
	String sensorId;
	String opMode;
	SensorBehaviour sensor;
	
	public GpsSensorWrapper(String id, KieSession k, FactHandle f, String mode) {
		super();
		sensorId = id;
		kSession = k;
		fact = f;
		rand = new Random();
		this.setOpMode(mode);
	}
	
	@Override
	public void run() {
		while(true) {
			try {	
				MovingObject m = (MovingObject) kSession.getObject(fact);
				m.setLocal(sensor.getSensorValue());
				//System.out.println("Localização de " + m.getObjectId() + ": " + m.getLocal().getLatitude() + ", " + m.getLocal().getLongitude());
				kSession.update(fact, m);
				kSession.fireAllRules();
				
				Thread.sleep(1000*rand.nextInt(10)+1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getOpMode() {
		return opMode;
	}
	
	public void setOpMode(String mode) {
		opMode = mode;
		if(opMode.equals("static")) {
			sensor = new StaticSensor(-10,10,-20,20,rand);
		}else {
			sensor = new RandomSensor(-10,10,-20,20,rand);
		}
	}
	
	public static interface SensorBehaviour{
		public MovingObject.Location getSensorValue();
	}
	
	public static class StaticSensor implements SensorBehaviour{
		public StaticSensor(float min_lat, float max_lat, float min_lon, float max_lon, Random rand) {
			super();
			float latitude = (float) (min_lat + rand.nextDouble() * (max_lat - min_lat));
			float longitude = (float) (min_lon + rand.nextDouble() * (max_lon - min_lon));
			this.location = new MovingObject.Location(latitude, longitude);
		}

		private MovingObject.Location location;
		
		public MovingObject.Location getSensorValue(){
			return location;
		}

		public MovingObject.Location getLocation() {
			return location;
		}

		public void setLocation(MovingObject.Location location) {
			this.location = location;
		}
		
	}

	public static class RandomSensor implements SensorBehaviour{
		public RandomSensor(float min_lat, float max_lat, float min_lon, float max_lon, Random rand) {
			super();
			this.min_lat = min_lat;
			this.max_lat = max_lat;
			this.min_lon = min_lon;
			this.max_lon = max_lon;
			this.rand = rand;
		}

		private float min_lat, max_lat, min_lon, max_lon;
		private Random rand;
		
		public MovingObject.Location getSensorValue() {
			float latitude = (float) (min_lat + rand.nextDouble() * (max_lat - min_lat));
			float longitude = (float) (min_lon + rand.nextDouble() * (max_lon - min_lon));
			return new MovingObject.Location(latitude, longitude);
		}

		public float getMin_lat() {
			return min_lat;
		}

		public void setMin_lat(float min_lat) {
			this.min_lat = min_lat;
		}

		public float getMax_lat() {
			return max_lat;
		}

		public void setMax_lat(float max_lat) {
			this.max_lat = max_lat;
		}

		public float getMin_lon() {
			return min_lon;
		}

		public void setMin_lon(float min_lon) {
			this.min_lon = min_lon;
		}

		public float getMax_lon() {
			return max_lon;
		}

		public void setMax_lon(float max_lon) {
			this.max_lon = max_lon;
		}

		public Random getRand() {
			return rand;
		}

		public void setRand(Random rand) {
			this.rand = rand;
		}
	}
	
}
