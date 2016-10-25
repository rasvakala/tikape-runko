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

//        Connection connection = database.getConnection();
//        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihe ORDER BY nimi;");
//
//        ResultSet rs = stmt.executeQuery();
//        boolean hasOne = rs.next();
//        if (!hasOne) {
//            return null;
//        }
//
//        while (rs.next()) {                               
//            aiheet.add(luoAiheolio(rs));            
//        }    
//        
//        rs.close();
//        stmt.close();
//        connection.close();
        return aiheet;
    }
    
    public List<Aihe> aiheetNurinAakkosissa() throws SQLException {
        List<Aihe> aiheet = aiheetAakkosissa();
        Collections.reverse(aiheet);
        return aiheet;
    }    

    //palauttaa kaikki aiheet suosituimmuusjärjestyksessä viestien lukumäärän perusteella
    public List<Aihe> suosituimmatAiheet() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT aihe_id FROM Aihe;");

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

        for (Integer aiheId : aihe_idit) {
            Integer lkm = this.otsikkoDao.aiheenOtsikonViestilkm(aiheId);
            aiheMap.put(lkm, findOne(aiheId));
        }

        //järjestetään avaimet
        List<Integer> avaimet = new ArrayList<Integer>(aiheMap.keySet());
        Collections.sort(avaimet);
        Collections.reverse(avaimet);

        List<Aihe> aiheet = new ArrayList<>();
        for (Integer avain : avaimet) {
            aiheet.add(aiheMap.get(avain));
        }

        rs.close();
        stmt.close();
        connection.close();
        return aiheet;
    }
    
    public List<Aihe> epasuosituimmatAiheet() throws SQLException{
        List<Aihe> aiheet = suosituimmatAiheet();
        Collections.reverse(aiheet);
        return aiheet;
    }

    public List<Aihe> aiheenOtsikonJaKuvauksenSanahaku(List<String> sanaLista) throws SQLException {
        //muokataan lista SQL-kyselymuotoon
        StringBuilder kysely = new StringBuilder();
        for (int i = 0; i < sanaLista.size(); i++) {
            kysely.append("'%");
            kysely.append(sanaLista.get(i));
            kysely.append("%'");

            if (i < sanaLista.size() - 1) {
                kysely.append(" OR ");
            }
        }

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihe WHERE nimi LIKE ? OR kuvaus LIKE ?;");
        stmt.setObject(1, kysely.toString());
        stmt.setObject(2, kysely.toString());

        ResultSet rs = stmt.executeQuery();
        List<Aihe> tulokset = new ArrayList<>();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return tulokset;
        }

        while (rs.next()) {
            tulokset.add(this.luoAiheolio(rs));
        }

        rs.close();
        stmt.close();
        connection.close();
        return tulokset;
    }

    public List<Aihe> aiheenOtsikonJaKuvauksenFraasihaku(String sanat) throws SQLException {
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihe WHERE nimi LIKE '%?%' OR kuvaus LIKE '%?%';");
        stmt.setObject(1, sanat);
        stmt.setObject(2, sanat);

        ResultSet rs = stmt.executeQuery();
        List<Aihe> tulokset = new ArrayList<>();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return tulokset;
        }

        while (rs.next()) {
            tulokset.add(this.luoAiheolio(rs));
        }

        rs.close();
        stmt.close();
        connection.close();
        return tulokset;
    }

    private Aihe luoAiheolio(ResultSet rs) throws SQLException {
        Integer aiheId = rs.getInt("aihe_id");
        String aiheNimi = rs.getString("nimi");
        String aiheKuvaus = rs.getString("kuvaus");
        Aihe a = new Aihe(aiheId, aiheNimi, aiheKuvaus);
        return a;
    }

    public void luoUusiAihe(String nimi, String kuvaus) throws Exception {
        try (Connection conn = this.database.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO Aihe(nimi, kuvaus)"
                    + "VALUES ('" + nimi + "', '" + kuvaus + "')");
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
        rs.close();
        stmt.close();
        connection.close();

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

        rs.close();
        stmt.close();
        connection.close();

        return aiheet;
    }

    @Override
    public void delete(Integer id) throws SQLException {
        // Toimii!.
        Connection conn = this.database.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM Aihe WHERE aihe_id = " + id + "");
        conn.close();
    }

}
