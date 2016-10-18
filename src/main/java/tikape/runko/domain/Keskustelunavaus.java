/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

import java.sql.Timestamp;

/**
 *
 * @author llmlks
 */
public class Keskustelunavaus {

    private Integer id;
    private Integer alue_id;
    private String nimi;
    private Integer viestit;
    private String viimeisin;

    public Keskustelunavaus(Integer id, Integer alue_id, String nimi) {
        this.id = id;
        this.alue_id = alue_id;
        this.nimi = nimi;
    }
    
    


    public Keskustelunavaus(Integer id, Integer alue_id, String nimi, String viimeisin) {
        this.id = id;
        this.alue_id = alue_id;
        this.nimi = nimi;
        this.viimeisin = viimeisin;
    }

    public Integer getId() {
        return id;
    }

    public void setViestit(Integer v) {
        this.viestit = v;
    }
    
    public Integer getViestit() {
        return this.viestit;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAlue_id() {
        return alue_id;
    }

    public void setAlue_id(Integer alue_id) {
        this.alue_id = alue_id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public String getViimeisin() {
        return viimeisin;
    }

    public void setViimeisin(String viimeisin) {
        this.viimeisin = viimeisin;
    }

    public String toString() {
        return this.id + " " + this.nimi;
    }

}
