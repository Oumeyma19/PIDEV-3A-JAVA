package com.example.pidev.services;

import com.example.pidev.interfaces.ICrud;
import com.example.pidev.models.Hebergements;
import com.example.pidev.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HebergementService implements ICrud<Hebergements> {

    private final Connection conn = MyConnection.getInstance().getConnection();
    private static HebergementService instance;

    private HebergementService() {
    }

    public static HebergementService getInstance() {
        if (instance == null) {
            instance = new HebergementService();
        }
        return instance;
    }


    @Override
    public Boolean ajouter(Hebergements H) throws SQLException {
        // String sql ="insert into personne (nom,prenom,age)" +
        //        "values('"+p.getNom()+"','"+p.getPrenom()+"',"+p.getAge()+")";
        //Statement st = conn.createStatement();
        //st.executeUpdate(sql);
        String sql = "insert into hebergements(nomHebrg,typeHeberg,descrHeberg,adresse,dateCheckin,dateCheckout,nbrClient,imageHeberg,prixHeberg) values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setString(1, H.getNomHeberg());
        st.setString(2, H.getTypeHeberg());
        st.setString(3, H.getDescrHeberg());
        st.setString(4, H.getAdresse());
        st.setTimestamp(5, H.getDateCheckin());
        st.setTimestamp(6, H.getDateCheckout());
        st.setInt(7, H.getNbrClient());
        st.setString(8, H.getImageHebrg());
        st.setFloat(9, H.getPrixHeberg());

        st.executeUpdate();
        System.out.println("Hebergement ajoutée");

        return true;
    }

    @Override
    public boolean supprimer(int id) {
        try {
            String sql = "DELETE FROM hebergements WHERE idHberg = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            int rowsDeleted = st.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Hébergement supprimé avec succès !");
                return true;
            } else {
                System.out.println("Aucun hébergement trouvé avec cet ID.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
        return false;
    }

    @Override
    public void modifier(Hebergements H) {
        String sql = "UPDATE hebergements SET nomHebrg = ?, typeHeberg = ?, descrHeberg = ?, adresse = ?, " +
                "dateCheckin = ?, dateCheckout = ?, nbrClient = ?, imageHeberg = ?,prixHeberg = ? WHERE idHberg = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, H.getNomHeberg());
            st.setString(2, H.getTypeHeberg());
            st.setString(3, H.getDescrHeberg());
            st.setString(4, H.getAdresse());
            st.setTimestamp(5, H.getDateCheckin());
            st.setTimestamp(6, H.getDateCheckout());
            st.setInt(7, H.getNbrClient());
            st.setString(8, H.getImageHebrg());
            st.setFloat(9, H.getPrixHeberg());
            st.setInt(10, H.getIdHebrg());

            int rowsUpdated = st.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Hébergement mis à jour avec succès !");
            } else {
                System.out.println("Aucun hébergement trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public List<Hebergements> recuperer() throws SQLException {
        String sql = "select * from hebergements";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Hebergements> personnes = new ArrayList<>();
        while (rs.next()) {
            Hebergements H = new Hebergements();

            H.setIdHebrg(rs.getInt("idHberg"));
            H.setNomHeberg(rs.getString("nomHebrg"));
            H.setTypeHeberg(rs.getString("typeHeberg"));
            H.setDescrHeberg(rs.getString("descrHeberg"));
            H.setAdresse(rs.getString("adresse"));
            H.setDateCheckin(rs.getTimestamp("dateCheckin"));
            H.setDateCheckout(rs.getTimestamp("dateCheckout"));
            H.setNbrClient(rs.getInt("nbrClient"));
            H.setImageHebrg(rs.getString("imageHeberg"));
            H.setPrixHeberg(rs.getFloat("prixHeberg"));

            personnes.add(H);
        }
        return personnes;
    }

    @Override
    public Hebergements recupererId(int id) throws SQLException {
        String sql = "select * from hebergements where idHberg = ?";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();
        Hebergements H = new Hebergements();

        while (rs.next()) {
            H.setIdHebrg(rs.getInt("idHberg"));
            H.setNomHeberg(rs.getString("nomHebrg"));
            H.setTypeHeberg(rs.getString("typeHeberg"));
            H.setDescrHeberg(rs.getString("descrHeberg"));
            H.setAdresse(rs.getString("adresse"));
            H.setDateCheckin(rs.getTimestamp("dateCheckin"));
            H.setDateCheckout(rs.getTimestamp("dateCheckout"));
            H.setNbrClient(rs.getInt("nbrClient"));
            H.setImageHebrg(rs.getString("imageHeberg"));
            H.setPrixHeberg(rs.getFloat("prixHeberg"));
        }

        return H;
    }
}
