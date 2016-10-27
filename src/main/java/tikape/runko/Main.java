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
        Database database = new Database("jdbc:sqlite:alalaude.db");
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
            map.put("viesti", "tervehdys");
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
            map.put("aihe", aiheDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "aihe");
        }, new ThymeleafTemplateEngine());
        post("/aiheet", (req, res) -> {
            String nimi = req.queryParams("aiheNimi");
            String kuvaus = req.queryParams("aiheKuvaus");
            System.out.println("Vastaanotettiin " + nimi);
            aiheDao.luoUusiAihe(nimi, kuvaus);
            return "Kerrotaan siitä tiedon lähettäjälle: " + nimi;
        });
//Tähän asti
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
