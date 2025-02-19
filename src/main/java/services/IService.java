package services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    void ajouter(T p) throws SQLException;
    void supprimer(T p) throws SQLException;;
    void modifier(T p) throws SQLException;;
    List<T> recuperer() throws SQLException;
}
