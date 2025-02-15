package interfaces;

import java.sql.SQLException;
import java.util.List;

public interface iCrud<T> {
    void ajouter(T p)throws SQLException;
    void supprimer(int id);
    void modifier(T p);
    List<T> recuperer()throws SQLException;
    T recupererId(int id)throws SQLException;
}
