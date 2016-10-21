package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tikape.runko.domain.Aihe;
import tikape.runko.domain.Otsikko;
import tikape.runko.domain.Viesti;

public class OtsikkoDao implements Dao<Otsikko, Integer> {

    private Database database;
    private ViestiDao viestiDao;
    private Aihe aihe;

    public OtsikkoDao(Database database, Aihe aihe, ViestiDao viesti) {
        this.database = database;
        this.aihe = aihe;
        this.viestiDao = viesti;
    }

    //Hakee kaikkien aiheiden otsikot, joissa viimeisimmät viestit.
    public List<Otsikko> tuoreimmatOtsikot() throws SQLException {

        //voiko tämän tehdä tehokkaammin? mietitään.
        List<Otsikko> otsikot = this.findAll();
        List<Otsikko> jarjestetytOtsikot = new ArrayList<>();
        List<Viesti> vikatViestit = new ArrayList<>();
        Map<Integer, Otsikko> otsikkoMap = new HashMap<>();

        for (Otsikko otsikko : otsikot) {
            otsikkoMap.put(otsikko.getId(), otsikko);
            vikatViestit.add(this.viestiDao.otsikonViimeisinViesti(otsikko.getId()));
        }

        Collections.sort(vikatViestit, new Comparator<Viesti>() {
            @Override
            public int compare(Viesti v1, Viesti v2) {
                return v1.getAika().compareTo(v2.getAika());
            }
        });

        for (Viesti viesti : vikatViestit) {
            //rakennetaan hashmapista uusi lista viestien otsikko-id:iden perusteella            
            jarjestetytOtsikot.add(otsikkoMap.get(viesti.getOtsikkoId()));
        }
        return jarjestetytOtsikot;

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

        while (rs.next()) {
            aiheenOtsikot.add(luoOtsikkoOlio(rs));

        }

        rs.close();
        stmt.close();
        connection.close();

        return aiheenOtsikot;
    }

    //Luo uuden Otsikon. Aikaleima ja otsikko_id on jätetty automaattisiksi.
    //Toimii!
    public void luoUusiOtsikko(String otsikkoteksti, String nimimerkki, String teksti, int aihe_id) throws Exception {
        try (Connection conn = this.database.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO Otsikko(otsikkoteksti, nimimerkki, teksti, aihe) "
                    + "VALUES ('" + otsikkoteksti + "', '" + nimimerkki + "', '" + teksti + "'," + aihe_id + ")");
        }

    }

    public List<Otsikko> otsikotAakkosissa(int aihe_id) throws SQLException {
        List<Otsikko> otsikot = new ArrayList<>();
        otsikot = this.aiheenOtsikot(aihe_id);
        Collections.sort(otsikot, new Comparator<Otsikko>() {
            @Override
            public int compare(Otsikko o1, Otsikko o2) {
                return o1.getoTeksti().compareTo(o2.getoTeksti());
            }
        });
        return otsikot;
    }

    //Tämä saattaa antaa vanhimmat otsikot ensin. Testaa!
    public List<Otsikko> uusimmatOtsikot(int aihe_id) throws SQLException {
        List<Otsikko> otsikot = new ArrayList<>();
        otsikot = this.aiheenOtsikot(aihe_id);
        Collections.sort(otsikot, new Comparator<Otsikko>() {
            @Override
            public int compare(Otsikko o1, Otsikko o2) {
                return o1.getAloitettu().compareTo(o2.getAloitettu());
            }
        });
        return otsikot;
    }

    private Otsikko luoOtsikkoOlio(ResultSet rs) throws SQLException {
        Integer otsikkoId = rs.getInt("otsikko_id");
        String oTeksti = rs.getString("otsikkoteksti");
        String nimiM = rs.getString("nimimerkki");
        String teksti = rs.getString("teksti");
        String aloitettu = rs.getString("keskustelu_aloitettu");
        Integer aiheId = rs.getInt("aihe");
        return new Otsikko(otsikkoId, oTeksti, nimiM, teksti, aloitettu, aiheId);
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

        Otsikko o = luoOtsikkoOlio(rs);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<Otsikko> findAll() throws SQLException {
        //ei toteutettu
        List<Otsikko> otsikot = new ArrayList<>();
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Otsikko;");

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        while (rs.next()) {
            otsikot.add(luoOtsikkoOlio(rs));
        }

        rs.close();
        stmt.close();
        connection.close();

        return otsikot;
    }

    @Override
    public void delete(Integer id) throws SQLException {
        // Toimii!.
        Connection conn = this.database.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM Otsikko WHERE otsikko_id = " + id + "");
        conn.close();
    }

}
