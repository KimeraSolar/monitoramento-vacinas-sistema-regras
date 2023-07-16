package kimeraSolar.vacinas.backend.schemas;

public class ConfigurationSchema {

    public static class VariacaoBruscaConfigurationSchema{
        public String tempo;
        public double temperatura;
    }

    public static class ManutencaoConfigurationSchema{
        public long eventos;
        public String tempo;
    }

    public static class PerigoConfigurationSchema{
        public double temperatura;
    }
    
}
