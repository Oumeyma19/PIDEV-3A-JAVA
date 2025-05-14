package models;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class Airport {

    private int id;
    private StringProperty nameAirport;
    private StringProperty location;
    private StringProperty code;

    public Airport(int id, String nameAirport, String location, String code) {
        this.id = id;
        this.nameAirport = new SimpleStringProperty(nameAirport);
        this.location = new SimpleStringProperty(location);
        this.code = new SimpleStringProperty(code);
    }
    public Airport( String nameAirport, String location, String code) {
        this.nameAirport = new SimpleStringProperty(nameAirport);
        this.location = new SimpleStringProperty(location);
        this.code = new SimpleStringProperty(code);
    }

    public Airport() {
    }

    public Airport(int id, String nameAirport) {
        this.id = id;
        this.nameAirport = new SimpleStringProperty(nameAirport);
    }
    public Airport(int departureAirportId) {
        this.id = departureAirportId;

    }

    public Airport(String nameAirport){
        this.nameAirport = new SimpleStringProperty(nameAirport);
    }




    public int getIdAirport() {
        return id;
    }

    public void setIdAirport(int id) {
        this.id = id;
    }

    public String getNameAirport() {
        return nameAirport.get();
    }

    public StringProperty nameProperty() {
        return nameAirport;
    }
    public void setNameAirport(String nameAirport) {
        this.nameAirport.set(nameAirport);
    }

    public StringProperty locationProperty() {
        return location;
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }


    public StringProperty codeProperty() {
        return code;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "id=" + id +
                ", nameAirport='" + nameAirport + '\'' +
                ", location='" + location + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Airport airport)) return false;
        return id == airport.id  && Objects.equals(nameAirport, airport.nameAirport) && Objects.equals(location, airport.location) && Objects.equals(code, airport.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameAirport, location, code);
    }
}
