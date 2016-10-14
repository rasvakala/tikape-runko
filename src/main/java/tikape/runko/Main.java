package tikape.runko;

//import java.sql.*;
//import java.util.HashMap;
//import spark.ModelAndView;
//import static spark.Spark.*;
//import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AiheDao;
import tikape.runko.database.Database;
import tikape.runko.database.OtsikkoDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Aihe;
import tikape.runko.domain.Otsikko;
//import tikape.runko.database.Database;
//import tikape.runko.database.AlalaudeDao;

public class Main {
    //TODO: Timestampit Stringeiks (for now)
    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:alalaude.db");
        database.init();
        
        Aihe aihe = new Aihe(1, "nimi", "kuvaus");
        Otsikko otsikko = new Otsikko(1, null, "nimiM", "teksti", null, 2);
        AiheDao aiheDao = new AiheDao(database);
        ViestiDao viestiDao = new ViestiDao(database, otsikko);
        OtsikkoDao otsikkoDao = new OtsikkoDao(database, aihe, viestiDao);

        System.out.println(aiheDao.findAll());
        System.out.println(otsikkoDao.aiheenOtsikot(1));

//        get("/", (req, res) -> {
//            HashMap map = new HashMap<>();
//            map.put("viesti", "tervehdys");
//
//            return new ModelAndView(map, "index");
//        }, new ThymeleafTemplateEngine());
//
//        get("/opiskelijat", (req, res) -> {
//            HashMap map = new HashMap<>();
//            map.put("opiskelijat", alalaudeDao.findAll());
//
//            return new ModelAndView(map, "opiskelijat");
//        }, new ThymeleafTemplateEngine());
//
//        get("/opiskelijat/:id", (req, res) -> {
//            HashMap map = new HashMap<>();
//            map.put("opiskelija", alalaudeDao.findOne(Integer.parseInt(req.params("id"))));
//
//            return new ModelAndView(map, "opiskelija");
//        }, new ThymeleafTemplateEngine());
    }
}
