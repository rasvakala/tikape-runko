
package tikape.runko.domain;
//CREATE TABLE Viesti (
//viesti_id integer PRIMARY KEY,

import java.sql.Timestamp;

//nimimerkki varchar(60) NOT NULL,
//viesti varchar(1000) NOT NULL,
//aika DATETIME DEFAULT CURRENT_TIMESTAMP,
//otsikko integer NOT NULL,
//FOREIGN KEY (otsikko) REFERENCES Otsikko(otsikko_id)
//);
//
public class Viesti {
    private Integer id;
    private String nimiM;
    private String viesti;
    private Timestamp aika;
    private Integer otsikkoId;

    public Viesti(Integer id, String nimiM, String viesti, Timestamp aika, Integer otsikkoId) {
        this.id = id;
        this.nimiM = nimiM;
        this.viesti = viesti;
        this.aika = aika;
        this.otsikkoId = otsikkoId;
    }
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimiM() {
        return nimiM;
    }

    public void setNimiM(String nimiM) {
        this.nimiM = nimiM;
    }

    public String getViesti() {
        return viesti;
    }

    public void setViesti(String viesti) {
        this.viesti = viesti;
    }

    public Timestamp getAika() {
        return aika;
    }

    public void setAika(Timestamp aika) {
        this.aika = aika;
    }

    public Integer getOtsikkoId() {
        return otsikkoId;
    }

    public void setOtsikkoId(Integer otsikkoId) {
        this.otsikkoId = otsikkoId;
    }
    
}

