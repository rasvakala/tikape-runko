
package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Aihe;
import tikape.runko.domain.Otsikko;

public class AiheDao implements Dao<Aihe, Integer>{
    private Database database;
    private Dao<Otsikko, Integer> otsikkoDao;

    public AiheDao(Database database) {
        this.database = database;
    }
    
    

    @Override
    public Aihe findOne(Integer id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihe WHERE id = " + id + ";");
        stmt.setObject(1, id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer aiheId = rs.getInt("aihe.id");
        String aiheNimi = rs.getString("aihe.nimi");
        String aiheKuvaus = rs.getString("aihe.kuvaus");

        Aihe a = new Aihe(aiheId, aiheNimi, aiheKuvaus);

        rs.close();
        stmt.close();
        connection.close();

        return a;
    }

    @Override
    public List<Aihe> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihe;");

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        List<Aihe> aiheet = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("aihe_id");
            String nimi = rs.getString("nimi");
            String kuvaus = rs.getString("kuvaus");
                                
            aiheet.add(new Aihe(id, nimi, kuvaus));
            
        }
        
        
        rs.close();
        stmt.close();
        connection.close();
        
        return aiheet;
    }

    @Override
    public void delete(Integer id) throws SQLException {
        //ei toteutettu
    }
    
}
