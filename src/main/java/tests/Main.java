package tests;

import services.AvisService;
import services.HebergementServices;
import services.ReservHebergService;



import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        HebergementServices ps = new HebergementServices();
        AvisService as = new AvisService();
        ReservHebergService rs = new ReservHebergService();



        //AvisHebergement ah = new AvisHebergement("very right very good", 4.9f, 6, 3);

        /*ReservationHebergement rh = new ReservationHebergement(false,
                Timestamp.valueOf(LocalDateTime.now()) ,
                6,
                1);*/

        /*Hebergements H = new Hebergements("RaedPalace",
                "HOTEL",
                "ba7dha brown",
                "5eyba",
                3,
                "raed.pic",
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now().plusDays(4))
                );
*/

        try {
            // ps.ajouter(H);
            //as.ajouter(ah);
           // rs.ajouter(rh);
           // Hebergements h = ps.recupererId(3);

           // Hebergements h = ps.recupererId(1);
            //h.setNomHeberg("5le3a");
                // ps.modifier(h);
             ps.supprimer(1);
             //ps.supprimer(h.getIdHebrg());

           // as.supprimer(6);
            //AvisHebergement ah = as.recupererId(4);
            //ah.setComment("good");
            //as.modifier(ah);

            //ReservationHebergement rh =rs.recupererId(3);
           // rh.setReservationDateHeberg(Timestamp.valueOf(LocalDateTime.now().plusDays(4)));
           // rs.modifier(rh);
            //rs.supprimer(1);
            System.out.println(ps.recuperer());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}