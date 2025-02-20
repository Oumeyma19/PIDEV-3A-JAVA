package com.example.pidev.models;

import java.sql.Timestamp;

public class Hebergements {
    private int idHebrg;
    private String nomHeberg;
    private String typeHeberg;
    private String descrHeberg;
    private String adresse;
    private int nbrClient;
    private String imageHebrg;
    private float prixHeberg;
    private boolean statusHeberg;

    public Hebergements() {
    }

    public Hebergements(String adresse, String descrHeberg, int idHebrg, String imageHebrg, int nbrClient, String nomHeberg, String typeHeberg, float prixHeberg, boolean statusHeberg) {
        this.adresse = adresse;

        this.descrHeberg = descrHeberg;
        this.idHebrg = idHebrg;
        this.imageHebrg = imageHebrg;
        this.nbrClient = nbrClient;
        this.nomHeberg = nomHeberg;
        this.typeHeberg = typeHeberg;
        this.prixHeberg = prixHeberg;
        this.statusHeberg = statusHeberg;
    }

    public Hebergements(String nomHeberg, String typeHeberg, String adresse, String descrHeberg, int nbrClient, String imageHebrg, float prixHeberg, boolean statusHeberg) {
        this.nomHeberg = nomHeberg;
        this.typeHeberg = typeHeberg;
        this.adresse = adresse;
        this.descrHeberg = descrHeberg;
        this.nbrClient = nbrClient;
        this.imageHebrg = imageHebrg;
        this.prixHeberg = prixHeberg;

    }

    public float getPrixHeberg() {
        return prixHeberg;
    }

    public void setPrixHeberg(float prixHeberg) {
        this.prixHeberg = prixHeberg;
    }

    public String getAdresse() {
        return adresse;
    }

    public boolean getStatusHeberg() {
        return statusHeberg;
    }

    public void setStatusHeberg(boolean statusHeberg) {
        this.statusHeberg = statusHeberg;
    }


    public String getDescrHeberg() {
        return descrHeberg;
    }

    public int getIdHebrg() {
        return idHebrg;
    }

    public String getImageHebrg() {
        return imageHebrg;
    }

    public int getNbrClient() {
        return nbrClient;
    }

    public String getNomHeberg() {
        return nomHeberg;
    }

    public String getTypeHeberg() {
        return typeHeberg;
    }


    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setDescrHeberg(String descrHeberg) {
        this.descrHeberg = descrHeberg;
    }

    public void setIdHebrg(int idHebrg) {
        this.idHebrg = idHebrg;
    }

    public void setImageHebrg(String imageHebrg) {
        this.imageHebrg = imageHebrg;
    }

    public void setNbrClient(int nbrClient) {
        this.nbrClient = nbrClient;
    }

    public void setNomHeberg(String nomHeberg) {
        this.nomHeberg = nomHeberg;
    }

    public void setTypeHeberg(String typeHeberg) {
        this.typeHeberg = typeHeberg;
    }


    @Override
    public String toString() {
        return "Hebergements{" +
                "adresse='" + adresse + '\'' +
                ", idHebrg=" + idHebrg +
                ", nomHeberg='" + nomHeberg + '\'' +
                ", typeHeberg='" + typeHeberg + '\'' +
                ", descrHeberg='" + descrHeberg + '\'' +
                ", nbrClient=" + nbrClient +
                ", imageHebrg='" + imageHebrg + '\'' +
                ", prixHeberg=" + prixHeberg +
                ", statusHeberg=" + statusHeberg +
                '}';
    }
}

