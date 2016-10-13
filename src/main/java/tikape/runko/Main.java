package tikape.runko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.KeskustelunavausDao;
import tikape.runko.database.OpiskelijaDao;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Keskustelunavaus;

public class Main {

    public static void main(String[] args) throws Exception {

        Database database = new Database("jdbc:sqlite:opiskelijat.db");

        KeskustelualueDao keskustelualueet = new KeskustelualueDao(database);
        KeskustelunavausDao keskustelunavaus = new KeskustelunavausDao(database);
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("keskustelualue", keskustelualueet.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        post("/keskustelualue", (req, res) -> {
            String nimi = req.queryParams("name");
            keskustelualueet.create(new Keskustelualue(0, nimi));
            
            res.redirect("/");
            return "";
        });

        get("/alue/:id", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            int id = Integer.parseInt(req.params(":id"));
            List<Keskustelunavaus> k = keskustelunavaus.findAllWithId(id);
            Keskustelualue keal = keskustelualueet.findOne(id);
            // k.setItems(items.itemsByCategory(Integer.parseInt(req.params(":id"))));
            map.put("keskustelunavaukset", k);
            map.put("keskustelualue", keal);
            // map.put("items", k.getItems());
            return new ModelAndView(map, "avaukset");

        }, new ThymeleafTemplateEngine());

        post("/alue/:id/avaus", (req, res) -> {
            String name = req.queryParams("name");
            int alueid = Integer.parseInt(req.params(":id"));
            keskustelunavaus.create(new Keskustelunavaus(0, alueid, name));
            res.redirect("/alue/" + alueid);
            return "";
        });
    }
}
