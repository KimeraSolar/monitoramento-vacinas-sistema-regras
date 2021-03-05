package com.sample;

public abstract class MovingObject {
	
	public static class Location{
		float longitude;
		float latitude;
		
		public Location(float l1, float l2) {
			longitude = l1;
			latitude = l2;
		}
		
		public void setLatitude(float l) {
			latitude = l;
		}
		
		public float getLatitude() {
			return latitude;
		}
		
		public void setLongitude(float l) {
			longitude = l;
		}
		
		public float getLongitude() {
			return longitude;
		}
		
		public float getDistance(Location other) {
			//TODO: implementar getDistance
			return 0;
		}
	}
	
	private Location local;
	private String objectId;
	
	public void setLocal(float longitude, float latitude) {
		this.local.setLatitude(latitude);
		this.local.setLongitude(longitude);
	}
	
	public Location getLocal() {
		return local;
	}
	
	public void setLocal(Location l) {
		this.local = l;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public float getDistance(MovingObject other) {		
		return this.getLocal().getDistance(other.getLocal());
	}

}
