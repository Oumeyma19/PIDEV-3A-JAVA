package models;

import java.sql.Timestamp;

public class Hebergements {
    private int idHebrg;
    private String nomHeberg;
    private String typeHeberg;
    private String descrHeberg;
    private String adresse;
    private Timestamp dateCheckin;
    private Timestamp dateCheckout;
    private int nbrClient;
    private String imageHebrg;

    public Hebergements() {
    }

    public Hebergements(String adresse, Timestamp dateCheckin, Timestamp dateCheckout, String descrHeberg, int idHebrg, String imageHebrg, int nbrClient, String nomHeberg, String typeHeberg) {
        this.adresse = adresse;
        this.dateCheckin = dateCheckin;
        this.dateCheckout = dateCheckout;
        this.descrHeberg = descrHeberg;
        this.idHebrg = idHebrg;
        this.imageHebrg = imageHebrg;
        this.nbrClient = nbrClient;
        this.nomHeberg = nomHeberg;
        this.typeHeberg = typeHeberg;
    }

    public Hebergements(String nomHeberg, String typeHeberg, String adresse, String descrHeberg, int nbrClient, String imageHebrg, Timestamp dateCheckin, Timestamp dateCheckout) {
        this.nomHeberg = nomHeberg;
        this.typeHeberg = typeHeberg;
        this.adresse = adresse;
        this.descrHeberg = descrHeberg;
        this.nbrClient = nbrClient;
        this.imageHebrg = imageHebrg;
        this.dateCheckin = dateCheckin;
        this.dateCheckout = dateCheckout;

    }

    public String getAdresse() {
        return adresse;
    }

    public Timestamp getDateCheckin() {
        return dateCheckin;
    }

    public Timestamp getDateCheckout() {
        return dateCheckout;
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

    public void setDateCheckin(Timestamp dateCheckin) {
        this.dateCheckin = dateCheckin;
    }

    public void setDateCheckout(Timestamp dateCheckout) {
        this.dateCheckout = dateCheckout;
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
                ", dateCheckin=" + dateCheckin +
                ", dateCheckout=" + dateCheckout +
                ", nbrClient=" + nbrClient +
                ", imageHebrg='" + imageHebrg + '\'' +
                '}';
    }
}
