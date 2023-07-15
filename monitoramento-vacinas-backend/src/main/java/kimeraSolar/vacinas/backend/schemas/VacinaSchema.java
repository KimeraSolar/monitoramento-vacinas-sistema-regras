package kimeraSolar.vacinas.backend.schemas;

import java.util.Date;

public class VacinaSchema{
    
    public VacinaSchema(){
        this.name = null;
        this.tempMax = 0;
        this.tempMin = 0;
        this.tempoDescarte = 0;
        this.abastecimentoDate = null;
        this.vencimentoDate = null;
        this.status = null;
    };
    
    public String name;
    public String id;
    public double tempMin;
    public double tempMax;
    public long tempoDescarte;
    public Date abastecimentoDate;
    public Date vencimentoDate;
    public Date desabastecimentoDate;
    public String status;

    public static class TipoSchema{
        public TipoSchema(String name, double tempMin, double tempMax, long tempoDescarte){
            this.name = name;
            this.tempMax = tempMax;
            this.tempMin = tempMin;
            this.tempoDescarte = tempoDescarte;
        }
        public String name;
        public double tempMin;
        public double tempMax;
        public long tempoDescarte;
    }
   
    public static class VacinaMoveSchema{
        public String vacinaId;
        public String camaraAtualId;
        public String camaraNovaId;
    }
}
