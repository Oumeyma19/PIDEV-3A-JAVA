package models;

import javafx.beans.property.*;

import java.sql.Timestamp;

public class Flight {
    private final IntegerProperty idFlight = new SimpleIntegerProperty();
    private final StringProperty departure = new SimpleStringProperty();
    private final StringProperty destination = new SimpleStringProperty();
    private final ObjectProperty<Timestamp> departureTime = new SimpleObjectProperty<>();
    private final ObjectProperty<Timestamp> arrivalTime = new SimpleObjectProperty<>();
    private final IntegerProperty price = new SimpleIntegerProperty();
    private final StringProperty flightNumber = new SimpleStringProperty();
    private final ObjectProperty<Airport> departureAirport = new SimpleObjectProperty<>();
    private final ObjectProperty<Airport> arrivalAirport = new SimpleObjectProperty<>();
    private final IntegerProperty numbre_place = new SimpleIntegerProperty();

    public Flight(int idFlight, String departure, String destination, Timestamp departureTime, Timestamp arrivalTime, int price, String flightNumber, Airport departureAirport, Airport arrivalAirport , int numbre_place) {
        this.idFlight.set(idFlight);
        this.departure.set(departure);
        this.destination.set(destination);
        this.departureTime.set(departureTime);
        this.arrivalTime.set(arrivalTime);
        this.price.set(price);
        this.flightNumber.set(flightNumber);
        this.departureAirport.set(departureAirport);
        this.arrivalAirport.set(arrivalAirport);
        this.numbre_place.set(numbre_place);

    }

    public Flight() {}

    public Flight(int idFlight) {
        this.idFlight.set(idFlight);
    }

    // Getters and Setters with Properties
    public int getIdFlight() { return idFlight.get(); }
    public void setIdFlight(int id) { this.idFlight.set(id); }
    public IntegerProperty idFlightProperty() { return idFlight; }

    public String getDeparture() { return departure.get(); }
    public void setDeparture(String dep) { this.departure.set(dep); }
    public StringProperty departureProperty() { return departure; }

    public String getDestination() { return destination.get(); }
    public void setDestination(String dest) { this.destination.set(dest); }
    public StringProperty destinationProperty() { return destination; }

    public Timestamp getDepartureTime() { return departureTime.get(); }
    public void setDepartureTime(Timestamp depTime) { this.departureTime.set(depTime); }
    public ObjectProperty<Timestamp> departureTimeProperty() { return departureTime; }

    public Timestamp getArrivalTime() { return arrivalTime.get(); }
    public void setArrivalTime(Timestamp arrTime) { this.arrivalTime.set(arrTime); }
    public ObjectProperty<Timestamp> arrivalTimeProperty() { return arrivalTime; }

    public int getPrice() { return price.get(); }
    public void setPrice(int p) { this.price.set(p); }
    public IntegerProperty priceProperty() { return price; }

    public String getFlightNumber() { return flightNumber.get(); }
    public void setFlightNumber(String fn) { this.flightNumber.set(fn); }
    public StringProperty flightNumberProperty() { return flightNumber; }

    public Airport getDepartureAirport() { return departureAirport.get(); }
    public void setDepartureAirport(Airport depAirport) { this.departureAirport.set(depAirport); }
    public ObjectProperty<Airport> departureAirportProperty() { return departureAirport; }

    public Airport getArrivalAirport() { return arrivalAirport.get(); }
    public void setArrivalAirport(Airport arrAirport) { this.arrivalAirport.set(arrAirport); }
    public ObjectProperty<Airport> arrivalAirportProperty() { return arrivalAirport; }

    public String getDepartureAirportName() {
        return departureAirport.get() != null ? departureAirport.get().getNameAirport() : "N/A";
    }

    public String getArrivalAirportName() {
        return arrivalAirport.get() != null ? arrivalAirport.get().getNameAirport() : "N/A";
    }

    public int getNumbre_place() {
        return numbre_place.get();
    }

    public void setNumbre_place(int numbre_place) {
        this.numbre_place.set(numbre_place);
    }

    public IntegerProperty numbre_placeProperty() {
        return numbre_place;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "idFlight=" + idFlight.get() +
                ", departure='" + departure.get() + '\'' +
                ", destination='" + destination.get() + '\'' +
                ", departureTime=" + departureTime.get() +
                ", arrivalTime=" + arrivalTime.get() +
                ", price=" + price.get() +
                ", flightNumber='" + flightNumber.get() + '\'' +
                ", departureAirport=" + departureAirport.get() +
                ", arrivalAirport=" + arrivalAirport.get() +
                ", numbre_place=" + numbre_place.get() +
                '}';
    }
}