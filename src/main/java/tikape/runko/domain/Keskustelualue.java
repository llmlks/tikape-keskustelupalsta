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
public class Keskustelualue {
    
    private Integer id;
    private String nimi;
    private Integer viestit;
    private String viimeisin;

    public String getViimeisin() {
        return viimeisin;
    }

    public void setViimeisin(String viimeisin) {
        this.viimeisin = viimeisin;
    }
    
    public Keskustelualue(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }
    
    public Keskustelualue(int id, String nimi, String viimeisin) {
        this.id = id;
        this.nimi = nimi;
        this.viimeisin = viimeisin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public Integer getViestit() {
        return this.viestit;
    }
    
    public void setViestit(int v) {
        this.viestit = v;
    }
    
    public String toString() {
        return this.id + " " + this.nimi;
    }
    
    
}
