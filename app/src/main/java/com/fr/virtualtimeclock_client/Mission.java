package com.fr.virtualtimeclock_client;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Mission {

    private String titre;
    private String description;
    private String lieu;
    private Date debut;
    private Date fin;
    private GeoPoint localisation;
    private int rayon;

    public Mission() {
        //empty constructor needed
    }

    public Mission(String titre, String description, String lieu, Date debut, Date fin, GeoPoint localisation, int rayon){
        this.titre          = titre;
        this.description    = description;
        this.lieu           = lieu;
        this.debut          = debut;
        this.fin            = fin;
        this.localisation   = localisation;
        this.rayon          = rayon;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getLieu() { return lieu; }

    public Date getDebut() {
        return debut;
    }

    public Date getFin() {
        return fin;
    }

    public GeoPoint getLocalisation() { return localisation; }

    public int getRayon() { return rayon; }
}
