package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AiheDao;
import tikape.runko.database.OtsikkoDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Aihe;
import tikape.runko.domain.Otsikko;
import tikape.runko.database.Database;

public class Main {

    //TODO: Timestampit Stringeiks (for now)
    public static void main(String[] args) throws Exception {

        // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:alalaude.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }

        Database database = new Database(jdbcOsoite);
        //Database database = new Database("jdbc:sqlite:alalaude.db");
//        database.init();

        Aihe aihe = new Aihe(1, "nimi", "kuvaus");
        Otsikko otsikko = new Otsikko(1, null, "nimiM", "teksti", null, 2);
        AiheDao aiheDao = new AiheDao(database);
        ViestiDao viestiDao = new ViestiDao(database, otsikko);
        OtsikkoDao otsikkoDao = new OtsikkoDao(database, aihe, viestiDao);

        //aiheDao.aiheetAakkosissa();
        //System.out.println(otsikkoDao.aiheenOtsikot(1));
        //Testaa uuden viestin luomista.
        //Se toimii!
        //viestiDao.luoUusiViesti("testaaja", "Testing, testing!", 2);
        //Testaa uuden otsikon luomista:
        //otsikkoDao.luoUusiOtsikko("Kylläpä ärsyttää!", "Testaaja", "Vaikka ei enää ärsytäkään.", 2);
        //Testaa uuden aiheen luomista:
        //aiheDao.luoUusiAihe("Rupattelu", "Keskustelua laidasta laitaan");
        //otsikkoDao.delete(3);
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("aiheet5", aiheDao.suosituimmatAiheet());
            map.put("viestit10", otsikkoDao.top10Otsikot());
            // aseta aiheet5, viestit 10 yms
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

//Tää on hyvä tässä ja lisäksi voisi laittaa vielä pari muuta, esim. otsikot
        get("/aiheet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("aiheet", aiheDao.findAll());

            return new ModelAndView(map, "hakuTulosAiheet");
        }, new ThymeleafTemplateEngine());
        get("/aiheet/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            // vai otsikon viestit
            map.put("aihe", aiheDao.findOne(Integer.parseInt(req.params("id"))));
            return new ModelAndView(map, "viestiKirjoitus");
        }, new ThymeleafTemplateEngine());

        post("/otsikot", (req, res) -> {
            String otsikkoteksti = req.queryParams("oTeksti");
            String nimimerkki = req.queryParams("nimiM");
            String teksti = req.queryParams("Viesti");
            int aiheid = Integer.parseInt(req.queryParams("aihe"));
            System.out.println("Vastaanotettiin " + teksti);
            otsikkoDao.luoUusiOtsikko(otsikkoteksti, nimimerkki, teksti, aiheid);

            return "Uusi viesti kirjoittajalta: " + nimimerkki + "lisätty";
        });

        //uusi5 lisää viestit
        get("/viestit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viestit", viestiDao.findAll());

            return new ModelAndView(map, "vastausViestiin");
        }, new ThymeleafTemplateEngine());
        get("/viestit/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            // vai otsikon viestit
            map.put("viesti", viestiDao.findOne(Integer.parseInt(req.params("id"))));
            return new ModelAndView(map, "vastausViestiin");
        }, new ThymeleafTemplateEngine());

        post("/viestit", (req, res) -> {
            String nimimerkki = req.queryParams("nimiM");
            String viesti = req.queryParams("viesti");
            int otsikko_id = Integer.parseInt(req.queryParams("otsikkoId"));
            System.out.println("Vastaanotettiin " + viesti);
            viestiDao.luoUusiViesti(nimimerkki, viesti, otsikko_id);

            return "Kirjoita ketjuun: " + viesti;
            //Viesti(viestiId, nimiM, viesti, aika, otsikkoId);
        });
        //uusi5 tähän asti

        //uusi4 lisää aihe
//        get("/aiheet", (req, res) -> {
//            HashMap map = new HashMap<>();
//            map.put("aiheet", aiheDao.findAll());
//
//            return new ModelAndView(map, "hakuTulosAiheet");
//        }, new ThymeleafTemplateEngine());
//        get("/aiheet/:id", (req, res) -> {
//            HashMap map = new HashMap<>();
//            // vai otsikon viestit
//            map.put("aihe", aiheDao.findOne(Integer.parseInt(req.params("id"))));
//            return new ModelAndView(map, "viestiKirjoitus");
//        }, new ThymeleafTemplateEngine());
//
//        post("/aiheet", (req, res) -> {
//            String nimi = req.queryParams("nimi");
//            String kuvaus = req.queryParams("kuvaus");
//            System.out.println("Vastaanotettiin " + teksti);
//            aiheDao.luoUusiAihe(nimi, kuvaus);
//
//            return "Lisättiin aihe nimeltä: " + aihe;
//        });
        //uusi4 tähän asti
//uusi3
        get("/otsikot", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("otsikot", otsikkoDao.findAll());

            return new ModelAndView(map, "hakuTulosOtsikot");
        }, new ThymeleafTemplateEngine());
        get("/otsikot/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            // vai otsikon viestit
            map.put("otsikko", otsikkoDao.findOne(Integer.parseInt(req.params("id"))));
            //uusi hakuTulosOtsikot, jossa vain hakee yhden
            return new ModelAndView(map, "hakuTulosOtsikko");
        }, new ThymeleafTemplateEngine());

//        post("/otsikot", (req, res) -> {
//            String otsikkoteksti = req.queryParams("oTeksti");
//            String nimimerkki = req.queryParams("nimiM");
//            String teksti = req.queryParams("teksti");
//            int aihe_id = Integer.parseInt(req.queryParams("aihe"));
//            System.out.println("Vastaanotettiin " + teksti);
//            otsikkoDao.luoUusiOtsikko(otsikkoteksti, nimimerkki, teksti, aihe_id);
//
//            return "Kerrotaan siitä tiedon lähettäjälle: " + nimimerkki;
//        });
        //luoUusiOtsikko(String otsikkoteksti, String nimimerkki, String teksti, int aihe_id)
//public class Viesti {
//    private Integer id;
//    private String nimiM;
//    private String viesti;
//    private Timestamp aika;
//    private Integer otsikkoId;
//tähän asti
//
//        get("/opiskelijat/:id", (req, res) -> {
//            HashMap map = new HashMap<>();
//            map.put("opiskelija", alalaudeDao.findOne(Integer.parseInt(req.params("id"))));
//
//            return new ModelAndView(map, "opiskelija");
//        }, new ThymeleafTemplateEngine());
    }

    public static String sanalistaKyselymuotoon(String[] sanaLista) {
        StringBuilder kysely = new StringBuilder();

//        kysely.append("'%");
        for (int i = 0; i < sanaLista.length; i++) {
            kysely.append(sanaLista[i]);

            if (i < sanaLista.length - 1) {
                kysely.append("%' OR '%");
            }
        }
//        kysely.append("%'");
        return kysely.toString();
    }

}
