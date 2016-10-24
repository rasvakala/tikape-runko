package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import tikape.runko.domain.Aihe;
import tikape.runko.domain.Otsikko;

public class AiheDao implements Dao<Aihe, Integer> {

    private Database database;
    private Dao<Otsikko, Integer> otsikkoDao;

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
