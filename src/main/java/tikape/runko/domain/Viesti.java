
package tikape.runko.domain;

import java.sql.Timestamp;

/**
 *
 * @author llmlks
 */
public class Viesti {
    
    private Integer id;
    private Integer avaus_id;
    private String aika;
    private String sisalto;
    private String nimimerkki;
    private Integer rivi;

    public Viesti(Integer id, Integer avaus_id, String aika, String sisalto, String nimimerkki) {
        this.id = id;
        this.avaus_id = avaus_id;
        this.aika = aika;
        this.sisalto = sisalto;
        this.nimimerkki = nimimerkki;
    }

    public Integer getId() {
        return id;
    }
    
    public String getTime() {
        return aika;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAvaus_id() {
        return avaus_id;
    }

    public void setAvaus_id(Integer avaus_id) {
        this.avaus_id = avaus_id;
    }

    public String getSisalto() {
        return sisalto;
    }

    public void setSisalto(String sisalto) {
        this.sisalto = sisalto;
    }

    public String getNimimerkki() {
        return nimimerkki;
    }

    public void setNimimerkki(String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }

    public Integer getRivi() {
        return rivi;
    }

    public void setRivi(Integer rivi) {
        this.rivi = rivi;
    }
    
    public String toString() {
        return this.id + " " + this.sisalto;
    }
    
}
