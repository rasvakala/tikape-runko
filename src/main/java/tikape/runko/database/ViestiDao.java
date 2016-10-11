
package tikape.runko.database;

import java.sql.*;
import java.util.List;
import tikape.runko.domain.Viesti;

public class ViestiDao implements Dao<Viesti, Integer>{
    private Database database;

    public ViestiDao(Database database) {
        this.database = database;
    }
    
    //metodi sanojen ja nimimerkin perusteella etsimiseen -sanat esim. listan avulla
    
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

        Viesti v = new Viesti(id, nimiM, viesti, aika, otsikkoId);

        rs.close();
        stmt.close();
        connection.close();

        return v;
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        //ei toteutettu
    }

    @Override
    public void delete(Integer id) throws SQLException {
        //ei toteutettu
    }
    
}
