
package tikape.runko.database;

import java.sql.*;
import java.util.List;
import tikape.runko.domain.Otsikko;
import tikape.runko.domain.Viesti;

public class ViestiDao implements Dao<Viesti, Integer>{
    private Database database;
    //private Dao<Otsikko, Integer> otsikkoDao;
    private Otsikko otsikko; 

    public ViestiDao(Database database, Otsikko otsikko) {
        this.database = database;
        this.otsikko = otsikko;
    }

    
    //metodi sanojen ja nimimerkin perusteella etsimiseen -sanat esim. listan avulla
    
    public Timestamp alueenViimeisinViesti() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT aika FROM Viesti WHERE otsikko =  " + otsikko.getId + " ORDER BY aika DESC LIMIT 1;");
        stmt.setObject(1, otsikko.getId);
        
         ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        
        Timestamp aikaleima = rs.getTimestamp("aika"); //Timestamp muuttujatyyppi! Tarkista!
        
        stmt.close();
        connection.close();
        return aikaleima;
    }
    
    //Luo uuden Viestin. Aikaleima ja otsikko_id on jätetty automaattisiksi.
    //Toimii!
        public void luoUusiViesti(String nimimerkki, String viesti, int otsikko_id) throws Exception {
        try (Connection conn = this.database.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO Viesti(nimimerkki, viesti, otsikko) "
                    + "VALUES ('" + nimimerkki + "', '" + viesti + "'," + otsikko_id + ")");
        }

    }
    
    @Override
    public Viesti findOne(Integer id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = " + id + ";");
        stmt.setObject(1, id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        //alusta viestin muuttujat

        Viesti v = new Viesti(null, null, null, null, null);

        rs.close();
        stmt.close();
        connection.close();

        return v;
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        //ei toteutettu
        return null;
    }

    @Override
    public void delete(Integer id) throws SQLException {
        // Toimii!.
        Connection conn = this.database.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM Viesti WHERE viesti_id = " + id + "");
        conn.close();
    }
    
    
    
}
