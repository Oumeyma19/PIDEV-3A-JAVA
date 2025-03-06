package interfaces;

import exceptions.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface ICrud<T> {
    Boolean ajouter(T p)throws SQLException;
    boolean supprimer(int id);
    void modifier(T p);
    List<T> recuperer() throws SQLException, UserNotFoundException;
    T recupererId(int id) throws SQLException, UserNotFoundException;
}
