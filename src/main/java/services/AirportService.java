package services;

import interfaces.IService;
import models.Airport;
import models.Hebergements;
import models.Offre;
import tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirportService implements IService<Airport> {
    private Connection cnx;


    public AirportService() {
        cnx = MyDataBase.getInstance().getCnx();
    }



    @Override
    public void ajouter(Airport p) {
        String sql = "INSERT INTO airport (idAirport, nameAirport, location , code) " +
                "VALUES (?, ?, ?, ?)";
        try(PreparedStatement set = cnx.prepareStatement(sql)) {
            set.setInt(1, p.getIdAirport());
            set.setString(2, p.getNameAirport());
            set.setString(3, p.getLocation());
            set.setString(4, p.getCode());
            set.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }




    }
    public Airport findAirportByName(String airportName) {
        String sql = "SELECT * FROM airport WHERE nameAirport = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, airportName);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Airport(
                        rs.getInt("idAirport"),
                        rs.getString("nameAirport"),
                        rs.getString("location"),
                        rs.getString("code")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if airport not found
    }

    @Override
    public void supprimer(Airport p) {
        String sql = "DELETE FROM airport WHERE idAirport = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, p.getIdAirport());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Airport deleted successfully.");
            } else {
                System.out.println("No airport found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void modifier(Airport p, String nom) {
        String sql = "UPDATE airport SET nameAirport = ?, location = ?, code = ? WHERE nameAirport = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, p.getNameAirport());
            pst.setString(2, p.getLocation());
            pst.setString(3, p.getCode());
            pst.setString(4, nom);
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Airport updated successfully.");
            } else {
                System.out.println("No airport found with the given name.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Airport> afficher() {
        List<Airport> airports = new ArrayList<>();
        String sql = "SELECT * FROM airport";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Airport airport = new Airport(
                        rs.getInt("idAirport"),
                        rs.getString("nameAirport"),
                        rs.getString("location"),
                        rs.getString("code")
                );
                airports.add(airport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return airports;
    }

    @Override
    public void modifier(Offre o) throws SQLException {

    }

    @Override
    public void modifier(Hebergements H) {

    }

    @Override
    public List<Airport> recuperer() throws SQLException {
        return List.of();
    }

    @Override
    public void modifier2(Hebergements p, String s) throws SQLException {

    }
}
