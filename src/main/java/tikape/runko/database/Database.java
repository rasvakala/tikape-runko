package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
    }
// lauseet = sql-lauseet, joilla luodaan alkutilanne tietokantaan.

    public void init() {
        List<String> lauseet = sqliteLauseet();

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }
// lista = kaikki tietokantataulun sarakkeet ja rivit alkutilanteessa

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        //muutettu Opiskelija-taulu vastaamaan
        //poistetaan myöhemmin taulu "Opiskelija"
        //poistetaan myöh. Opiskelija-taulun testidata
        
        /*
        lista.add("CREATE TABLE Aihe (aihe_id integer PRIMARY KEY, nimi varchar(60) NOT NULL UNIQUE, kuvaus varchar(120));");
        lista.add("CREATE TABLE Otsikko (otsikko_id integer PRIMARY KEY NOT NULL,otsikkoteksti varchar(60) NOT NULL, nimimerkki varchar(60) NOT NULL,teksti varchar(1000),keskustelu_aloitettu DATETIME DEFAULT CURRENT_TIMESTAMP, aihe integer NOT NULL, FOREIGN KEY (aihe) REFERENCES Aihe(aihe_id));");
        lista.add("CREATE TABLE Viesti (viesti_id integer PRIMARY KEY,nimimerkki varchar(60) NOT NULL,viesti varchar(1000) NOT NULL,aika DATETIME DEFAULT CURRENT_TIMESTAMP,otsikko integer NOT NULL, FOREIGN KEY (otsikko) REFERENCES Otsikko(otsikko_id)));");
        */

        return lista;
    }
}
