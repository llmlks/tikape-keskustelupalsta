package tikape.runko;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.KeskustelunavausDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Keskustelunavaus;
import tikape.runko.domain.Viesti;

public class Main {
    
    public static void main(String[] args) throws Exception {

        Database database = new Database("jdbc:sqlite:opiskelijat.db");

        KeskustelualueDao keskustelualueet = new KeskustelualueDao(database);
        KeskustelunavausDao keskustelunavaus = new KeskustelunavausDao(database);
        ViestiDao viestit = new ViestiDao(database);
        java.util.Date date = new java.util.Date();
        


        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            List<Keskustelualue> keal = keskustelualueet.findAll();
            for (Keskustelualue k : keal) {
                int viesteja = 0;
                List<Keskustelunavaus> keav = keskustelunavaus.findAllWithId(k.getId());
                for (Keskustelunavaus k1 : keav) {
                    viesteja += viestit.findAllWithId(k1.getId()).size();
                }
                k.setViestit(viesteja);
            }
            
            map.put("keskustelualue", keal);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        post("/keskustelualue", (req, res) -> {
            String nimi = req.queryParams("name");
            
            if (!nimi.isEmpty())
                keskustelualueet.create(new Keskustelualue(0, nimi));

            res.redirect("/");
            return "";
        });

        get("/alue/:id", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            int id = Integer.parseInt(req.params(":id"));
            List<Keskustelunavaus> k = keskustelunavaus.findAllWithId(id);
            Keskustelualue keal = keskustelualueet.findOne(id);
            List<Integer> viestienmaara = new ArrayList<>();
            for (Keskustelunavaus k1 : k) {
                k1.setViestit(viestit.findAllWithId(k1.getId()).size());
            }
            // k.setItems(items.itemsByCategory(Integer.parseInt(req.params(":id"))));
            map.put("keskustelunavaukset", k);
            map.put("keskustelualue", keal);
            map.put("viestimaarat", viestienmaara);
            // map.put("items", k.getItems());
            return new ModelAndView(map, "avaukset");

        }, new ThymeleafTemplateEngine());

        post("/alue/:id/avaus", (req, res) -> {
            String name = req.queryParams("name");
            int alueid = Integer.parseInt(req.params(":id"));
            String viesti = req.queryParams("viesti");
            String kirjoittaja = req.queryParams("nimimerkki");
            if (!(name.isEmpty() || viesti.isEmpty() || kirjoittaja.isEmpty())) {
                List<Keskustelunavaus> k = keskustelunavaus.findAll();
                int vikaId = 0;
                try {
                    vikaId = k.get(k.size() - 1).getId() + 1;
                } catch (Exception e) {

                }
                keskustelunavaus.create(new Keskustelunavaus(0, alueid, name));

                Viesti v = new Viesti(1, vikaId, new Timestamp(date.getTime()).toString(), viesti, kirjoittaja);
                viestit.create(v);
            }
            res.redirect("/alue/" + alueid);
            return "";
        });

        get("/avaus/:id", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            int id2 = Integer.parseInt(req.params(":id"));
            List<Viesti> v = viestit.findAllWithId(id2);
            Keskustelunavaus keav = keskustelunavaus.findOne(id2);
            // k.setItems(items.itemsByCategory(Integer.parseInt(req.params(":id"))));
            map.put("viestit", v);
            map.put("keskustelunavaus", keav);
            // map.put("items", k.getItems());
                        
            res.redirect("/avaus_sivu/" + id2 + "_" + 1);
            
            return new ModelAndView(map, "viestit");

        }, new ThymeleafTemplateEngine());

        get("avaus_sivu/:sivu", (req, res) -> {
            String[] params = req.params(":sivu").split("_");
            HashMap<String, Object> map = new HashMap<>();
            int id2 = Integer.parseInt(params[0]);
            int sivu = Integer.parseInt(params[1]);
            List<Viesti> v = viestit.findAllWithId(id2);
            int alku = (sivu - 1) * 10;
            int loppu = v.size();
            
            if (v.size() - alku + 1 > 10) {
                loppu = alku + 10;
            }
            
            List<Viesti> viesti = v.subList(alku, loppu);
            Keskustelunavaus keav = keskustelunavaus.findOne(id2);
            // k.setItems(items.itemsByCategory(Integer.parseInt(req.params(":id"))));
            map.put("viestit", viesti);
            map.put("keskustelunavaus", keav);
            map.put("sivu", req.params(":sivu"));
                        
            return new ModelAndView(map, "viestit");
            
        }, new ThymeleafTemplateEngine());
        
        post("/avaus_sivu/:sivu/edellinen", (req, res) -> {
            String[] params = req.params(":sivu").split("_");

            int sivu = Integer.parseInt(params[1]);

            if (sivu > 1) {
                sivu--;
            }
            
            res.redirect("/avaus_sivu/" + params[0] + "_" + sivu);
            
            return ""; 
        });
        
        post("/avaus_sivu/:sivu/seuraava", (req, res) -> {
            String[] params = req.params(":sivu").split("_");

            int sivu = Integer.parseInt(params[1]);
 
            List<Viesti> v = viestit.findAllWithId(Integer.parseInt(params[0]));

            
            int sivuja = (int) Math.ceil(v.size() / 10.0);
            
            if (sivu < sivuja) {
                sivu++;
            }

                        
            res.redirect("/avaus_sivu/" + params[0] + "_" + sivu);
            
            return ""; 
        });        
        
        post("/avaus/:id/luoviesti", (req, res) -> {
            String[] params = req.params(":id").split("_");
            int avausid = Integer.parseInt(params[0]);
            String viesti = req.queryParams("viesti");
            String kirjoittaja = req.queryParams("nimimerkki");
            
            if (!viesti.isEmpty() && !kirjoittaja.isEmpty()) {
                List<Viesti> avauksenviestit = viestit.findAllWithId(avausid);
                int viestiId = avauksenviestit.size() + 1;

                Viesti v = new Viesti(viestiId, avausid, new Timestamp(date.getTime()).toString(), viesti, kirjoittaja);
                viestit.create(v);
            }
            res.redirect("/avaus_sivu/" + params[0] + "_" + params[1]);
            return "";
        });
        
        
        post("/avaus_sivu/:sivu/alue", (req, res) -> {
            String[] params = req.params(":sivu").split("_");
            Keskustelunavaus keav = keskustelunavaus.findOne(Integer.parseInt(params[0]));
            
            
            res.redirect("/alue/" + keav.getAlue_id());
            
            return "";
        });
    }
}
