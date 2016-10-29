package tikape.runko.database;

import java.sql.*;
import java.util.*;
import java.net.*;
//import java.util.ArrayList;
//import java.util.List;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
        init();
    }

    private void init() {
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

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

//    public Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(databaseAddress);
//    }
    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }

    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE Aihe;");
        lista.add("DROP TABLE Otsikko;");
        lista.add("DROP TABLE Viesti");
        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Aihe (aihe_id SERIAL PRIMARY KEY, nimi varchar(60) NOT NULL UNIQUE, kuvaus varchar(120);");
        lista.add("INSERT INTO Aihe (nimi, kuvaus) VALUES ('postgresql-aihe', 'Postgresql-keskustelua');");
        lista.add("CREATE TABLE Otsikko (otsikko_id integer SERIAL PRIMARY KEY NOT NULL, otsikkoteksti varchar(60) NOT NULL, nimimerkki varchar(60) NOT NULL,\n"
                + "teksti varchar(1000),\n"
                + "keskustelu_aloitettu TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "aihe integer NOT NULL,\n"
                + "FOREIGN KEY (aihe) REFERENCES Aihe(aihe_id)\n"
                + ");");
        lista.add("INSERT INTO Otsikko (otsikkoteksti, teksti, aihe, nimimerkki) VALUES ('postgresql-otsikko', 'Näin vaan luodaan otsikko', 1, 'postgresql-keskustelija');");
        lista.add("CREATE TABLE Viesti (\n"
                + "viesti_id integer SERIAL PRIMARY KEY,\n"
                + "nimimerkki varchar(60) NOT NULL,\n"
                + "viesti varchar(1000) NOT NULL,\n"
                + "aika TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "otsikko integer NOT NULL,\n"
                + "FOREIGN KEY (otsikko) REFERENCES Otsikko(otsikko_id)\n"
                + ");");
        lista.add("INSERT INTO Viesti (nimimerkki, viesti, otsikko) VALUES ('postgresql-keskustelija', 'Ja vastailenkin!', 1);");
        //lista.add("INSERT INTO Tuote (nimi) VALUES ('postgresql-tuote');");

        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        //lista.add("CREATE TABLE Tuote (id integer PRIMARY KEY, nimi varchar(255));");
        //lista.add("INSERT INTO Tuote (nimi) VALUES ('sqlite-tuote');");
        lista.add("CREATE TABLE Aihe (aihe_id integer PRIMARY KEY, nimi varchar(60) NOT NULL UNIQUE, kuvaus varchar(120));");
        lista.add("INSERT INTO Aihe (nimi, kuvaus) VALUES ('sqlite-aihe', 'Käytetään sqliteä');");
        lista.add("CREATE TABLE Otsikko (otsikko_id integer PRIMARY KEY NOT NULL, otsikkoteksti varchar(60) NOT NULL, nimimerkki varchar(60) NOT NULL,\n"
                + "teksti varchar(1000),\n"
                + "keskustelu_aloitettu DATETIME DEFAULT CURRENT_TIMESTAMP,\n"
                + "aihe integer NOT NULL,\n"
                + "FOREIGN KEY (aihe) REFERENCES Aihe(aihe_id)\n"
                + ");");
        lista.add("INSERT INTO Otsikko (otsikkoteksti, teksti, aihe, nimimerkki) VALUES ('sqlite-otsikko', 'Näin vaan luodaan otsikko', 1, 'sqlite-keskustelija');");
        lista.add("CREATE TABLE Viesti (\n"
                + "viesti_id integer PRIMARY KEY,\n"
                + "nimimerkki varchar(60) NOT NULL,\n"
                + "viesti varchar(1000) NOT NULL,\n"
                + "aika DATETIME DEFAULT CURRENT_TIMESTAMP,\n"
                + "otsikko integer NOT NULL,\n"
                + "FOREIGN KEY (otsikko) REFERENCES Otsikko(otsikko_id)\n"
                + ");");
        lista.add("INSERT INTO Viesti (nimimerkki, viesti, otsikko) VALUES ('sqlite-keskustelija', 'Ja vastailenkin!', 1);");

        return lista;
    }

}
