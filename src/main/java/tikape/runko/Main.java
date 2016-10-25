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
        
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:forum.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        } 

        Database database = new Database(jdbcOsoite);

        KeskustelualueDao keskustelualueet = new KeskustelualueDao(database);
        KeskustelunavausDao keskustelunavaus = new KeskustelunavausDao(database);
        ViestiDao viestit = new ViestiDao(database);
        java.util.Date date = new java.util.Date();
        
        database.init();

        //Etusivu, listataan keskustelualueet
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

        //Lisätään keskustelualue
        post("/keskustelualue", (req, res) -> {
            String nimi = req.queryParams("name");
            
            if (!nimi.isEmpty())
                keskustelualueet.create(new Keskustelualue(0, nimi));

            res.redirect("/");
            return "";
        });

        //Avaa tietyn keskustelualueen ja listaa siihen liittyvät keskustelunavaukset
        get("/alue/:id", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            int id = Integer.parseInt(req.params(":id"));
            List<Keskustelunavaus> k = keskustelunavaus.findAllWithId(id);
            Keskustelualue keal = keskustelualueet.findOne(id);
            List<Integer> viestienmaara = new ArrayList<>();
            for (Keskustelunavaus k1 : k) {
                k1.setViestit(viestit.findAllWithId(k1.getId()).size());
            }
            map.put("keskustelunavaukset", k);
            map.put("keskustelualue", keal);
            map.put("viestimaarat", viestienmaara);
            return new ModelAndView(map, "avaukset");

        }, new ThymeleafTemplateEngine());

        //Lisää uuden keskustelunavauksen, sekä siihen liittyvän avausviestin nimimerkin kanssa
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

        //Ohjaa tiettyyn keskustelunavaukseen liittyvien viestien 1. sivulle
        get("/avaus/:id", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            int id2 = Integer.parseInt(req.params(":id"));
                        
            res.redirect("/avaus_sivu/" + id2 + "_" + 1);
            
            return new ModelAndView(map, "viestit");

        }, new ThymeleafTemplateEngine());

        //Listaa tietyllä sivulla olevat keskustelualueen viestit (10 kerrallaan)
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
            for (int i = alku; i < loppu; i++) {
                v.get(i).setRivi(i + 1);
            }            
            List<Viesti> viesti = v.subList(alku, loppu);

            Keskustelunavaus keav = keskustelunavaus.findOne(id2);
            map.put("viestit", viesti);
            map.put("keskustelunavaus", keav);
            map.put("sivu", req.params(":sivu"));
                        
            return new ModelAndView(map, "viestit");
            
        }, new ThymeleafTemplateEngine());
        
        //Ohjaa viestien edelliselle sivulle, jos sellainen on
        post("/avaus_sivu/:sivu/edellinen", (req, res) -> {
            String[] params = req.params(":sivu").split("_");

            int sivu = Integer.parseInt(params[1]);

            if (sivu > 1) {
                sivu--;
            }
            
            res.redirect("/avaus_sivu/" + params[0] + "_" + sivu);
            
            return ""; 
        });
        
        //Ohjaa viestien seuraavalle sivulle, jos sellainen on 
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
        
        //Luo uuden viestin keskustelunavaukseen
        post("/avaus/:id/luoviesti", (req, res) -> {
            String[] params = req.params(":id").split("_");
            int avausid = Integer.parseInt(params[0]);
            String viesti = req.queryParams("viesti");
            String kirjoittaja = req.queryParams("nimimerkki");
            List<Viesti> v = viestit.findAllWithId(Integer.parseInt(params[0]));
            
            if (!viesti.isEmpty() && !kirjoittaja.isEmpty()) {
                List<Viesti> avauksenviestit = viestit.findAllWithId(avausid);
                int viestiId = avauksenviestit.size() + 1;

                Viesti vi = new Viesti(viestiId, avausid, new Timestamp(date.getTime()).toString(), viesti, kirjoittaja);
                viestit.create(vi);
            }
            res.redirect("/avaus_sivu/" + params[0] + "_" + (int) Math.ceil(v.size() / 10.0));
            return "";
        });
        
        //Ohjaa takaisin keskustelualueelle, johon keskustelunavaus liittyy
        post("/avaus_sivu/:sivu/alue", (req, res) -> {
            String[] params = req.params(":sivu").split("_");
            Keskustelunavaus keav = keskustelunavaus.findOne(Integer.parseInt(params[0]));
            
            
            res.redirect("/alue/" + keav.getAlue_id());
            
            return "";
        });
    }
}
