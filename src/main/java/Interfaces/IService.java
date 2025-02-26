package Interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IService<T>{

    void ajouter(T p) throws SQLException;
    void supprimer(T p);
    void modifier(T p,String nom);
    List<T> afficher() throws SQLException;







}
