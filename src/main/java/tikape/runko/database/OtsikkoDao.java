package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Aihe;
import tikape.runko.domain.Otsikko;
import tikape.runko.domain.Viesti;

public class OtsikkoDao implements Dao<Otsikko, Integer> {

    private Database database;
    private Dao<Viesti, Integer> viestiDao;
    private Aihe aihe;

    public OtsikkoDao(Database database, Aihe aihe, ViestiDao viesti) {
        this.database = database;
        this.aihe = aihe;
        this.viestiDao = viesti;
    }



    //metodit sanojen ja nimimerkin perusteella etsimiseen
    public List<Otsikko> aiheenOtsikot(Integer aihe_id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Otsikko WHERE aihe = ?;");
        stmt.setObject(1, aihe_id);
        
        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        List<Otsikko> aiheenOtsikot = new ArrayList<>();

        /*
        Otsikko(Integer id, String oTeksti, String nimiM, String teksti, 
        Timestamp aloitettu, Integer aiheId)
         */
        while (rs.next()) {
            int id = rs.getInt("otsikko_id");
            String oTeksti = rs.getString("otsikkoteksti");
            String nimiM = rs.getString("nimimerkki");
            String teksti = rs.getString("teksti");
            String aloitettu = rs.getString("keskustelu_aloitettu");
            Integer aiheId = rs.getInt("aihe");
                    
            aiheenOtsikot.add(new Otsikko(id, oTeksti, nimiM, teksti, aloitettu, aiheId));
            
        }
        
        
        rs.close();
        stmt.close();
        connection.close();
        
        return aiheenOtsikot;
    }
    
    

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
        Otsikko o = new Otsikko(null, null, null, null, null, null); //KORJAA TÄÄ

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<Otsikko> findAll() throws SQLException {
        //ei toteutettu
        return null;
    }

    @Override
    public void delete(Integer id) throws SQLException {
        //ei toteutettu
    }
}
