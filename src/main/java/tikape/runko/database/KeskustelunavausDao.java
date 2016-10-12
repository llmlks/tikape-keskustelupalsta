/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Keskustelunavaus;

/**
 *
 * @author llmlks
 */
public class KeskustelunavausDao implements Dao<Keskustelunavaus, Integer> {

    private Database database;
    
    public KeskustelunavausDao(Database db) {
        this.database = db;
    }
    
    @Override
    public Keskustelunavaus findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelunavaus WHERE id = " + key);
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        Integer alue_id = rs.getInt("alue_id");
        String nimi = rs.getString("nimi");

        Keskustelunavaus avaus = new Keskustelunavaus(id, alue_id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return avaus;    
    }

    @Override
    public List findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelunavaus");

        ResultSet rs = stmt.executeQuery();
        List<Keskustelunavaus> avaukset = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer alue_id = rs.getInt("alue_id");
            String nimi = rs.getString("nimi");

            avaukset.add(new Keskustelunavaus(id, alue_id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return avaukset;    
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE * FROM Keskustelunavaus WHERE id = " + key);
        stmt.setObject(1, key);

        stmt.executeQuery();
        stmt.close();
        connection.close();
    }
    
    public void save(Keskustelunavaus avaus) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelunavaus (alue_id, nimi) VALUES (" + avaus.getAlue_id() + ", " + avaus.getNimi() + ")");
        stmt.executeQuery();
        stmt.close();
        connection.close();
    }

    @Override
    public Keskustelunavaus create(Keskustelunavaus t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(String key, Keskustelunavaus t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
