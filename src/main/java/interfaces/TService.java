package interfaces;

import java.sql.SQLException;
import java.util.List;

public interface TService<T>{

    boolean ajouter(T p) throws SQLException;
    boolean supprimer(T p);


    boolean modifier(T p);
    List<T> afficher() throws SQLException;






}