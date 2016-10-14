
package tikape.runko.database;

import java.sql.*;
import java.util.*;
import tikape.runko.domain.Aihe;

public class AlalaudeDao implements Dao<Aihe, Integer> {
    
    /*
    Käytetään tätä vain esimerkkinä, poistan kunhan muut Daot on valmiit    
    */
    
    private Database database;

    public AlalaudeDao(Database database) {
        this.database = database;
    }

    //oli findOne
    public Aihe findAihe(Integer id) throws SQLException {
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

        Aihe o = new Aihe(aiheId, aiheNimi, aiheKuvaus);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<Aihe> findAll() throws SQLException {
        
        //TODO:korjaa tämä!

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Opiskelija");

        ResultSet rs = stmt.executeQuery();
        List<Aihe> opiskelijat = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            opiskelijat.add(new Aihe(id, nimi, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return opiskelijat;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }

    @Override
    public Aihe findOne(Integer key) throws SQLException { 
        //ei toteutettu
        return null;
    }

}
