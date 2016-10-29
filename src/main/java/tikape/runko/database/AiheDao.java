package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import tikape.runko.domain.Aihe;
import tikape.runko.domain.Otsikko;

public class AiheDao implements Dao<Aihe, Integer> {

    private Database database;
    private OtsikkoDao otsikkoDao;

    public AiheDao(Database database) {
        this.database = database;
    }

    //Toteutettu sort java:n puolella, jotta kopioitu koodi vähenee.
    //Varmuuden vuoksi myös select-kyselyversio kommenttina.
    public List<Aihe> aiheetAakkosissa() throws SQLException {
        List<Aihe> aiheet = new ArrayList<>();
        aiheet = this.findAll();
        Collections.sort(aiheet, new Comparator<Aihe>() {
            @Override
            public int compare(Aihe a1, Aihe a2) {
                return a1.getNimi().compareTo(a2.getNimi());
            }
        });
        return aiheet;
    }

    /*        Connection connection = database.getConnection();
     PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihe ORDER BY nimi;");

     ResultSet rs = stmt.executeQuery();
     boolean hasOne = rs.next();
     if (!hasOne) {
     return null;
     }

     while (rs.next()) {                               
     aiheet.add(luoAiheolio(rs));            
     }    
        
     closeConnections(rs, stmt, connection); 
     */
    public List<Aihe> aiheetNurinAakkosissa() throws SQLException {
        List<Aihe> aiheet = aiheetAakkosissa();
        Collections.reverse(aiheet);
        return aiheet;
    }

    //palauttaa kaikki aiheet suosituimmuusjärjestyksessä viestien lukumäärän perusteella
    public List<Aihe> suosituimmatAiheet() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT aihe_id FROM Aihe LEFT JOIN Otsikko ON (Otsikko.aihe = Aihe.aihe_id) GROUP BY Aihe.aihe_id ORDER BY COUNT(Otsikko.otsikko_id) "
                + "DESC LIMIT 5;");

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        List<Integer> aihe_idit = new ArrayList<>();

        while (rs.next()) {
            aihe_idit.add(rs.getInt("aihe_id"));
        }

        HashMap<Integer, Aihe> aiheMap = new HashMap<>();

        List<Aihe> aiheet = new ArrayList<>();
        for (Integer aiheId : aihe_idit) {
            aiheet.add(findOne(aiheId));
        }
        closeConnections(rs, stmt, connection);
        return aiheet;
    }

    public List<Aihe> epasuosituimmatAiheet() throws SQLException {
        List<Aihe> aiheet = suosituimmatAiheet();
        Collections.reverse(aiheet);
        return aiheet;
    }

    public List<Aihe> aiheenOtsikonJaKuvauksenSanahaku(String merkkijono) throws SQLException {
        //yksittäisiä sanoja varten käytä main-luokan sanalistaKyselymuotoon-metodia, muuten toimii fraasihakuna!

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihe WHERE nimi LIKE '%'||?||'%' OR kuvaus LIKE '%'||?||'%';");
        stmt.setObject(1, merkkijono);
        stmt.setObject(2, merkkijono);

        ResultSet rs = stmt.executeQuery();
        List<Aihe> tulokset = new ArrayList<>();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return tulokset;
        }

        while (rs.next()) {
            tulokset.add(this.luoAiheolio(rs));
        }

        closeConnections(rs, stmt, connection);
        return tulokset;
    }

    private Aihe luoAiheolio(ResultSet rs) throws SQLException {
        Integer aiheId = rs.getInt("aihe_id");
        String aiheNimi = rs.getString("nimi");
        String aiheKuvaus = rs.getString("kuvaus");
        return new Aihe(aiheId, aiheNimi, aiheKuvaus);
    }

    public void luoUusiAihe(String nimi, String kuvaus) throws Exception {
        try (Connection conn = this.database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Aihe(nimi, kuvaus) VALUES ('?', '?')");
            stmt.setObject(1, nimi);
            stmt.setObject(2, kuvaus);
            stmt.execute();
            stmt.close();
            conn.close();
        }
    }

    @Override
    public Aihe findOne(Integer id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihe WHERE aihe_id = ?;");
        stmt.setObject(1, id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        Aihe aihe = luoAiheolio(rs); // ennen sulkemista
        closeConnections(rs, stmt, connection);

        return aihe;
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
            aiheet.add(luoAiheolio(rs));
        }

        closeConnections(rs, stmt, connection);

        return aiheet;
    }

    @Override
    public void delete(Integer id) throws SQLException {
        // Toimii!
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Aihe WHERE aihe_id = ?");
        stmt.setObject(1, id);
        stmt.execute();
        stmt.close();
        conn.close();
    }

    private void closeConnections(ResultSet rs, PreparedStatement stmt, Connection connection) throws SQLException {
        rs.close();
        stmt.close();
        connection.close();
    }
}
