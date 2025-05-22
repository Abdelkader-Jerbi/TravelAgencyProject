package entities;

public enum Enumnom {
    economique(1.0),business(1.5),premiereclasse(2.0) ;
    private final double taux;

    Enumnom(double taux) {
        this.taux = taux;
    }

    public double getTaux() {
        return taux;
    }
}
