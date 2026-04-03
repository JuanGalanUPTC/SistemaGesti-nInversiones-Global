package co.edu.uptc.model.enums;

public enum AssetType {

    //1 -> Bajo Riesgo
    //2 -> Medio-Bajo Riesgo
    //3 -> Medio-Alto Riesgo
    //4 -> Alto Riesgo
    //5 -> Muy Alto Riesgo

    BOND(1), //Bono
    STOCK(1), //Acción
    ETF(2),    //ETF
    PROPERTY(2),   //Propiedad(inmueble)
    BADGE(1), //Divisa (USD,COP)
    CRYPTO(4), //Cryptomoneda
    NFT(5);//Token No Fungible

    private int riskLevel;

    AssetType(int riskLevel){
        this.riskLevel=riskLevel;
    }

    public int getRiskLevel(){
        return riskLevel;
    }

}
