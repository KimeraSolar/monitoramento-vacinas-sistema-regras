package com.sample;

import java.util.Random;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class GpsSensorWrapper implements Runnable {
	
	KieSession kSession;
	FactHandle fact;
	Random rand;
	String sensorId;
	
	public GpsSensorWrapper(String id, KieSession k, FactHandle f) {
		super();
		sensorId = id;
		kSession = k;
		fact = f;
		rand = new Random();
	}
	
	@Override
	public void run() {
		while(true) {
			try {	
				MovingObject m = (MovingObject) kSession.getObject(fact);
				m.setLocal(getSensorValue());
				System.out.println("Localização de " + m.getObjectId() + ": " + m.getLocal().getLatitude() + ", " + m.getLocal().getLongitude());
				kSession.update(fact, m);
				kSession.fireAllRules();
				
				Thread.sleep(1000*rand.nextInt(10)+1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public MovingObject.Location getSensorValue() {
		int latitude = rand.nextInt(8);
		int longitude = rand.nextInt(8);
		return new MovingObject.Location(latitude, longitude);
	}
}
