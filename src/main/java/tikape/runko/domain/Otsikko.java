package tikape.runko.domain;

import java.sql.Timestamp;

public class Otsikko {

//otsikko_id integer PRIMARY KEY NOT NULL,
//otsikkoteksti varchar(60) NOT NULL,
//nimimerkki varchar(60) NOT NULL,
//teksti varchar(1000),
//keskustelu_aloitettu DATETIME DEFAULT CURRENT_TIMESTAMP,
//aihe integer NOT NULL,
//FOREIGN KEY (aihe) REFERENCES Aihe(aihe_id)
    private Integer id;
    private String oTeksti;
    private String nimiM;
    private String teksti;
    private String aloitettu;
    private Integer aiheId;
    public Object getId;

    public Otsikko(Integer id, String oTeksti, String nimiM, String teksti, String aloitettu, Integer aiheId) {
        this.id = id;
        this.oTeksti = oTeksti;
        this.nimiM = nimiM;
        this.teksti = teksti;
        this.aloitettu = aloitettu;
        this.aiheId = aiheId;
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getoTeksti() {
        return oTeksti;
    }

    public void setoTeksti(String oTeksti) {
        this.oTeksti = oTeksti;
    }

    public String getNimiM() {
        return nimiM;
    }

    public void setNimiM(String nimiM) {
        this.nimiM = nimiM;
    }

    public String getTeksti() {
        return teksti;
    }

    public void setTeksti(String teksti) {
        this.teksti = teksti;
    }

    public String getAloitettu() {
        return aloitettu;
    }

    public void setAloitettu(String aloitettu) {
        this.aloitettu = aloitettu;
    }

    public Integer getAiheId() {
        return aiheId;
    }

    public void setAiheId(Integer aiheId) {
        this.aiheId = aiheId;
    }


}
