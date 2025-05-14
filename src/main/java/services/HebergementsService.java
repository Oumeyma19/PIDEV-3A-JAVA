package services;



import interfaces.IService;
import models.Hebergements;
import models.Offre;
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
        String sql = "insert into hebergements(nom_Hebrg,typeHeberg,descr_Heberg,adresse,nbr_Client,image_Heberg,prix_Heberg) values(?,?,?,?,?,?,?)";
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
    public void supprimer(Hebergements H) {
        try {
            String sql = "DELETE FROM hebergements WHERE id = ?";
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
    public void modifier(Hebergements p, String nom) {

    }

    @Override
    public List<Hebergements> afficher() throws SQLException {
        return List.of();
    }

    @Override
    public void modifier(Offre o) throws SQLException {

    }

    @Override
    public void modifier(Hebergements H) {
        String sql = "UPDATE hebergements SET nom_Hebrg = ?, typeHeberg = ?, descr_Heberg = ?, adresse = ?," +
                " nbr_Client = ?, image_Heberg = ?,prix_Heberg = ? WHERE id = ?";
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

            H.setIdHebrg(rs.getInt("id"));
            H.setNomHeberg(rs.getString("nom_Hebrg"));
            H.setTypeHeberg(rs.getString("typeHeberg"));
            H.setDescrHeberg(rs.getString("descr_Heberg"));
            H.setAdresse(rs.getString("adresse"));
            H.setNbrClient(rs.getInt("nbr_Client"));
            H.setImageHebrg(rs.getString("image_Heberg"));

            personnes.add(H);
        }
        return personnes;
    }

    @Override
    public void modifier2(Hebergements p, String s) throws SQLException {

    }

    public Hebergements recupererId(int id) throws SQLException {
        String sql = "select * from hebergements where id = ?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();
        Hebergements H = new Hebergements();

        while (rs.next()) {
            H.setIdHebrg(rs.getInt("id"));
            H.setNomHeberg(rs.getString("nom_Hebrg"));
            H.setTypeHeberg(rs.getString("typeHeberg"));
            H.setDescrHeberg(rs.getString("descr_Heberg"));
            H.setAdresse(rs.getString("adresse"));
            H.setNbrClient(rs.getInt("nbr_Client"));
            H.setImageHebrg(rs.getString("image_Heberg"));
            H.setPrixHeberg(rs.getFloat("prix_Heberg"));
        }

        return H;
    }

    public Boolean existsByNameAndAddress(String name, String addr) throws SQLException {
        String sql = "select count(1) from hebergements where nom_Hebrg = ? AND adresse = ?";
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