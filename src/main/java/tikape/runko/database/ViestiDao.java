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
        
        rs.close();
        stmt.close();
        connection.close();
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
        
        rs.close();
        stmt.close();
        connection.close();
        return viestit;
    }
    
    public Viesti otsikonViimeisinViesti(int otsikko_id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT aika FROM Viesti WHERE otsikko =  " + otsikko_id + " ORDER BY aika DESC LIMIT 1;");
        stmt.setObject(1, otsikko_id);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        //Timestamp muuttujatyyppi! Tarkista!

        rs.close();
        stmt.close();
        connection.close();
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

        rs.close();
        stmt.close();
        connection.close();
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

        rs.close();
        stmt.close();
        connection.close();
        return maara;
    }

    public List<Viesti> viestienSanahaku(List<String> sanaLista) throws SQLException {
        //muokataan lista SQL-kyselymuotoon
        StringBuilder kysely = new StringBuilder();
        for (int i = 0; i < sanaLista.size(); i++) {
            kysely.append("'%");
            kysely.append(sanaLista.get(i));
            kysely.append("%'");
            
            if (i < sanaLista.size()-1) {
                 kysely.append(" OR ");
            }
        }
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE teksti LIKE ? ;");
        stmt.setObject(1, kysely.toString());
        
        ResultSet rs = stmt.executeQuery();
        List<Viesti> tulokset = new ArrayList<>();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return tulokset;
        }

        while (rs.next()) {
            tulokset.add(this.luoViestiolio(rs));
        }

        rs.close();
        stmt.close();
        connection.close();
        return tulokset;
    }
    
    public List<Viesti> viestienFraasihaku(String sanat) throws SQLException {        
        //Jos tulee ongelmia, tsekkaa kyselyn välilyönni @ '%?%'
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE teksti LIKE '%?%' ;");
        stmt.setObject(1, sanat);
        
        ResultSet rs = stmt.executeQuery();
        List<Viesti> tulokset = new ArrayList<>();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return tulokset;
        }

        while (rs.next()) {
            tulokset.add(this.luoViestiolio(rs));
        }

        rs.close();
        stmt.close();
        connection.close();
        return tulokset;
    }
    
    //Luo uuden Viestin. Aikaleima ja otsikko_id on jätetty automaattisiksi.
    //
    //Toimii!
    public void luoUusiViesti(String nimimerkki, String viesti, int otsikko_id) throws Exception {
        try (Connection conn = this.database.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO Viesti(nimimerkki, viesti, otsikko) "
                    + "VALUES ('" + nimimerkki + "', '" + viesti + "'," + otsikko_id + ")");
        }

    }

    private Viesti luoViestiolio(ResultSet rs) throws SQLException {
        Integer viestiId = rs.getInt("viesti_id");
        String nimiM = rs.getString("nimimerkki");
        String viesti = rs.getString("viesti");
        Timestamp aika = rs.getTimestamp("aika");
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

        rs.close();
        stmt.close();
        connection.close();

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

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
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
