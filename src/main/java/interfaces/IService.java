package interfaces;

import models.Hebergements;
import models.Offre;

import java.sql.SQLException;
import java.util.List;

public interface IService<T>{

    void ajouter(T p) throws SQLException;
    void supprimer(T p) throws SQLException;
    void modifier(T p,String nom) throws SQLException;
    List<T> afficher() throws SQLException;


    void modifier(Offre o) throws SQLException;

    void modifier(Hebergements H);

    List<Hebergements> recuperer() throws SQLException;

    void modifier2(Hebergements p, String s) throws SQLException;
}
