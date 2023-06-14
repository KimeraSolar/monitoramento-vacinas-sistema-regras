package kimeraSolar.vacinas.domain;

public abstract class MovingObject {
	
	public static class Location{
		float longitude;
		float latitude;
		
		public Location(float l1, float l2) {
			latitude = l1;
			longitude = l2;
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
		
		public double getDistance(Location other) {
			double R = 6371e3; //raio da terra em metros
			double φ1 = this.latitude * Math.PI/180; // φ, λ in radians
			double φ2 = other.latitude * Math.PI/180;
			double Δφ = (this.latitude-other.latitude) * Math.PI/180;
			double Δλ = (this.longitude-other.longitude) * Math.PI/180;

			double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
			Math.cos(φ1) * Math.cos(φ2) *
			Math.sin(Δλ/2) * Math.sin(Δλ/2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

			return R * c; // em metros
			
		}

		@Override
		public String toString() {
			return "Location [longitude=" + longitude + ", latitude=" + latitude + "]";
		}
		
	}
	
	private Location local;
	private String objectId;
	
	public void setLocal(float latitude, float longitude) {
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
	
	public double getDistance(MovingObject other) {		
		return this.getLocal().getDistance(other.getLocal());
	}

}
