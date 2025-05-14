package services;

import interfaces.ICrud;
import models.Hebergements;
import tools.MyConnection;

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
        String sql = "insert into hebergements(nom_Hebrg,typeHeberg,descr_Heberg,adresse,nbr_Client,image_Heberg,prix_Heberg) values(?,?,?,?,?,?,?)";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setString(1, H.getNomHeberg());
        st.setString(2, H.getTypeHeberg());
        st.setString(3, H.getDescrHeberg());
        st.setString(4, H.getAdresse());
        st.setInt(5, H.getNbrClient());
        st.setString(6, H.getImageHebrg());
        st.setFloat(7, H.getPrixHeberg());

        st.executeUpdate();
        System.out.println("Hebergement ajoutée");

        return true;
    }

    @Override
    public boolean supprimer(int id) {
        try {
            String sql = "DELETE FROM hebergements WHERE id = ?";
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
        String sql = "UPDATE hebergements SET nom_hebrg = ?, typeHeberg = ?, descr_heberg = ?, adresse = ?," +
                " nbr_client = ?, image_heberg = ?,prix_heberg = ? WHERE id = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
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
        Statement st = conn.createStatement();
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
            H.setPrixHeberg(rs.getFloat("prix_Heberg"));

            personnes.add(H);
        }
        return personnes;
    }

    @Override
    public Hebergements recupererId(int id) throws SQLException {
        String sql = "select * from hebergements where id = ?";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();
        Hebergements H = new Hebergements();

        while (rs.next()) {
            H.setIdHebrg(rs.getInt("id"));
            H.setNomHeberg(rs.getString("nom_hebrg"));
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
        String sql = "select count(1) from hebergements where nom_Hebrg = ? AND adresse = ?";
        PreparedStatement st = conn.prepareStatement(sql);
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
