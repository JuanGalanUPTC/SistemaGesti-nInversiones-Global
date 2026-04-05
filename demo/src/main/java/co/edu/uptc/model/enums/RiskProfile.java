package co.edu.uptc.model.enums;

public enum RiskProfile {
    CONSERVATIVE(2), //Solo tolera bajo (1) y medio-bajo (2)
    MODERATE(3),    //Tolera hasta medio-alto(3)
    AGGRESSIVE(5);   //Tolera hasta alto(5)

    private int maxRisk;

    private RiskProfile(int maxRisk) {
        this.maxRisk=maxRisk;
    }

    public int getMaxRisk(){
        return maxRisk;
    }
    

}
