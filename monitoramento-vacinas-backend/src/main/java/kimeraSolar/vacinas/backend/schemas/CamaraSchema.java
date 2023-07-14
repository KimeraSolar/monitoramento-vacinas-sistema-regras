package kimeraSolar.vacinas.backend.schemas;

public class CamaraSchema {
    public CamaraSchema(String status, float temperatura, String id, float[] location) {
        super();
        this.camaraStatus = status;
        this.camaraTemperature = temperatura;
        this.camaraName = id;
        this.camaraLocation = location;
    }
    public String camaraStatus;
    public float camaraTemperature;
    public String camaraName;
    public float[] camaraLocation;

    public static class CamaraInsertSchema{
        public CamaraInsertSchema(String camaraName, String gerenteName){
            this.camaraName = camaraName;
            this.gerenteName = gerenteName;
        }
        public CamaraInsertSchema(String camaraName){
            this.camaraName = camaraName;
            this.gerenteName = null;
        }
        public String camaraName;
        public String gerenteName;
    }
}
