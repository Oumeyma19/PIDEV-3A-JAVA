package services;



import models.Hebergements;
import tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HebergementsService implements IService<Hebergements> {

    private Connection cnx;



    public HebergementsService() {
        cnx = MyDataBase.getInstance().getCnx();
    }



    @Override
    public void ajouter(Hebergements H) throws SQLException {
        String sql = "insert into hebergements(nomHebrg,typeHeberg,descrHeberg,adresse,nbrClient,imageHeberg,prixHeberg) values(?,?,?,?,?,?,?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, H.getNomHeberg());
        st.setString(2, H.getTypeHeberg());
        st.setString(3, H.getDescrHeberg());
        st.setString(4, H.getAdresse());
        st.setInt(5, H.getNbrClient());
        st.setString(6, H.getImageHebrg());
        st.setFloat(7, H.getPrixHeberg());

        st.executeUpdate();
        System.out.println("Hebergement ajoutée");

    }

    @Override
    public void supprimer(Hebergements H)throws SQLException  {
        try {
            String sql = "DELETE FROM hebergements WHERE idHberg = ?";
            PreparedStatement st = cnx.prepareStatement(sql);
            st.setInt(1,H.getIdHebrg());
            int rowsDeleted = st.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Hébergement supprimé avec succès !");
            } else {
                System.out.println("Aucun hébergement trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Hebergements H) {
        String sql = "UPDATE hebergements SET nomHebrg = ?, typeHeberg = ?, descrHeberg = ?, adresse = ?," +
                " nbrClient = ?, imageHeberg = ?,prixHeberg = ? WHERE idHberg = ?";
        try {
            PreparedStatement st = cnx.prepareStatement(sql);
            st.setString(1, H.getNomHeberg());
            st.setString(2, H.getTypeHeberg());
            st.setString(3, H.getDescrHeberg());
            st.setString(4, H.getAdresse());
            st.setInt(5, H.getNbrClient());
            st.setString(6, H.getImageHebrg());
            st.setFloat(7, H.getPrixHeberg());
            st.setInt(8, H.getIdHebrg());

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
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Hebergements> personnes = new ArrayList<>();
        while (rs.next()) {
            Hebergements H = new Hebergements();

            H.setIdHebrg(rs.getInt("idHberg"));
            H.setNomHeberg(rs.getString("nomHebrg"));
            H.setTypeHeberg(rs.getString("typeHeberg"));
            H.setDescrHeberg(rs.getString("descrHeberg"));
            H.setAdresse(rs.getString("adresse"));
            H.setNbrClient(rs.getInt("nbrClient"));
            H.setImageHebrg(rs.getString("imageHeberg"));

            personnes.add(H);
        }
        return personnes;
    }

    @Override
    public void modifier2(Hebergements p, String s) throws SQLException {

    }

    public Hebergements recupererId(int id) throws SQLException {
        String sql = "select * from hebergements where idHberg = ?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();
        Hebergements H = new Hebergements();

        while (rs.next()) {
            H.setIdHebrg(rs.getInt("idHberg"));
            H.setNomHeberg(rs.getString("nomHebrg"));
            H.setTypeHeberg(rs.getString("typeHeberg"));
            H.setDescrHeberg(rs.getString("descrHeberg"));
            H.setAdresse(rs.getString("adresse"));
            H.setNbrClient(rs.getInt("nbrClient"));
            H.setImageHebrg(rs.getString("imageHeberg"));
            H.setPrixHeberg(rs.getFloat("prixHeberg"));
        }

        return H;
    }

    public Boolean existsByNameAndAddress(String name, String addr) throws SQLException {
        String sql = "select count(1) from hebergements where nomHebrg = ? AND adresse = ?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, name);
        st.setString(2, addr);

        ResultSet rs = st.executeQuery();

        boolean exist = false;

        while (rs.next()) {
            exist = rs.getInt("count(1)") > 0;
        }

        return exist;
    }
}