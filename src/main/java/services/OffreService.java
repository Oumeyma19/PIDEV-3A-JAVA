package services;

import models.Offre;
import tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreService implements IService<Offre> {
    private Connection cnx;

    public OffreService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Offre o) throws SQLException {
        String sql = "INSERT INTO offers (title, description, price, start_date, end_date,image_path) VALUES (?, ?, ?, ?, ?,?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, o.getTitle());
        st.setString(2, o.getDescription());
        st.setDouble(3, o.getPrice());
        st.setString(4, o.getStartDate());
        st.setString(5, o.getEndDate());
        st.setString(6, o.getImagePath());
        st.executeUpdate();
        System.out.println("Offre ajoutée   ");
    }

    @Override
    public void supprimer(Offre o) throws SQLException {
        String sql = "DELETE FROM offers WHERE id = ?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, o.getId());
        st.executeUpdate();
        System.out.println("Offre supprimée");
    }

    @Override
    public void modifier(Offre o) throws SQLException {
        String sql = "UPDATE offers SET title = ?, description = ?, price = ?, start_date = ?, end_date = ? WHERE id = ?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, o.getTitle());
        st.setString(2, o.getDescription());
        st.setDouble(3, o.getPrice());
        st.setString(4, o.getStartDate());
        st.setString(5, o.getEndDate());
        st.setInt(6, o.getId());
        st.executeUpdate();
        System.out.println("Offre modifiée");
    }

    @Override
    public List<Offre> recuperer() throws SQLException {
        String sql = "SELECT * FROM offers";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Offre> offres = new ArrayList<>();
        while (rs.next()) {
            Offre o = new Offre();
            o.setId(rs.getInt("id"));
            o.setTitle(rs.getString("title"));
            o.setDescription(rs.getString("description"));
            o.setPrice(rs.getDouble("price"));
            o.setStartDate(rs.getString("start_date"));
            o.setEndDate(rs.getString("end_date"));
            o.setImagePath(rs.getString("image_path"));
            offres.add(o);
        }
        return offres;
    }
    public int getOfferIdByTitle(String title) throws SQLException {
        String sql = "SELECT id FROM offers WHERE title = ?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, title);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("Offer with title '" + title + "' not found.");
        }
    }
}
