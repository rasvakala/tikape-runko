
package tikape.runko.database;

import java.sql.*;
import java.util.List;
import tikape.runko.domain.Otsikko;

public class OtsikkoDao implements Dao<Otsikko, Integer>{
    private Database database;

    public OtsikkoDao(Database database) {
        this.database = database;
    }
    
    //metodit sanojen ja nimimerkin perusteella etsimiseen

    @Override
    public Otsikko findOne(Integer id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Otsikko WHERE id = " + id + ";");
        stmt.setObject(1, id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        //otsikon muuttujat tähän

        Otsikko o = new Otsikko(id, oTeksti, nimiM, teksti, aloitettu, otsikkoId);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<Otsikko> findAll() throws SQLException {
        //ei toteutettu
    }

    @Override
    public void delete(Integer id) throws SQLException {
        //ei toteutettu
    }
}
