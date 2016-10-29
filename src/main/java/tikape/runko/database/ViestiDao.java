package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Otsikko;
import tikape.runko.domain.Viesti;

public class ViestiDao implements Dao<Viesti, Integer> {

    private Database database;
    private Otsikko otsikko;

    public ViestiDao(Database database, Otsikko otsikko) {
        this.database = database;
        this.otsikko = otsikko;
    }

    //TODO metodi sanojen ja nimimerkin perusteella etsimiseen -sanat esim. listan avulla
    public List<Viesti> otsikonViestit(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE otsikko =  ? ORDER BY aika DESC;");
        stmt.setObject(1, id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        List<Viesti> viestit = new ArrayList<>();

        while (rs.next()) {
            viestit.add(luoViestiolio(rs));
        }

        closeConnections(rs, stmt, connection);
        return viestit;
    }

    public List<Viesti> nimimerkinViestit(String nimim) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE nimimerkki =  ? ORDER BY aika DESC;");
        stmt.setObject(1, nimim);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        List<Viesti> viestit = new ArrayList<>();

        while (rs.next()) {
            viestit.add(luoViestiolio(rs));
        }

        closeConnections(rs, stmt, connection);
        return viestit;
    }

    public Viesti otsikonViimeisinViesti(int otsikko_id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT aika FROM Viesti WHERE otsikko =  ? ORDER BY aika DESC LIMIT 1;");
        stmt.setObject(1, otsikko_id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        closeConnections(rs, stmt, connection);
        return luoViestiolio(rs);
    }

    public List<Integer> viimeisimmatOtsikkoIdt() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT otsikko FROM Viesti ORDER BY aika DESC;");
        ResultSet rs = stmt.executeQuery();
        List<Integer> idt = new ArrayList<>();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return idt;
        }

        while (rs.next()) {
            idt.add(rs.getInt("otsikko"));
        }

        closeConnections(rs, stmt, connection);
        return idt;
    }

    public int laskeOtsikonViestit(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) AS lkm FROM Viesti WHERE otsikko = ? GROUP BY viesti_id;");
        stmt.setObject(1, id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return 0;
        }

        int maara = rs.getInt("lkm");

        closeConnections(rs, stmt, connection);
        return maara;
    }

    public List<Viesti> viestienSanahaku(String merkkijono) throws SQLException {
        //yksittäisiä sanoja varten käytä main-luokan sanalistaKyselymuotoon-metodia, muuten toimii fraasihakuna!

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE teksti LIKE '%?%';");
        stmt.setObject(1, merkkijono);

        ResultSet rs = stmt.executeQuery();
        List<Viesti> tulokset = new ArrayList<>();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return tulokset;
        }

        while (rs.next()) {
            tulokset.add(this.luoViestiolio(rs));
        }

        closeConnections(rs, stmt, connection);
        return tulokset;
    }

    //Luo uuden Viestin. Aikaleima ja otsikko_id on jätetty automaattisiksi.
    //
    //Toimii!
    public void luoUusiViesti(String nimimerkki, String viesti, int otsikko_id) throws Exception {
        try (Connection conn = this.database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Viesti(nimimerkki, viesti, otsikko) VALUES (?, ?, ?)");
            stmt.setObject(1, nimimerkki);
            stmt.setObject(2, viesti);
            stmt.setObject(3, otsikko_id);
            stmt.execute();
            stmt.close();
            conn.close();
        }

    }

    private Viesti luoViestiolio(ResultSet rs) throws SQLException {
        Integer viestiId = rs.getInt("viesti_id");
        String nimiM = rs.getString("nimimerkki");
        String viesti = rs.getString("viesti");
        Timestamp aika = null; // rs.getTimestamp("aika");//Timestamp muuttujatyyppi ongelmallinen? Tarkista!
        Integer otsikkoId = rs.getInt("otsikko");

        return new Viesti(viestiId, nimiM, viesti, aika, otsikkoId);
    }

    @Override
    public Viesti findOne(Integer id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?;");
        stmt.setObject(1, id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        closeConnections(rs, stmt, connection);

        return luoViestiolio(rs);
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti;");
        List<Viesti> viestit = new ArrayList<>();

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        while (rs.next()) {
            viestit.add(luoViestiolio(rs));
        }

        closeConnections(rs, stmt, connection);

        return viestit;
    }

    @Override
    public void delete(Integer id) throws SQLException {
        // Toimii!
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Viesti WHERE viesti_id = ?");
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
