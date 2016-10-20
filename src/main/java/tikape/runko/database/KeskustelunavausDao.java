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
    private KeskustelualueDao kaDao;
    
    
    public KeskustelunavausDao(Database db) {
        this.database = db;
        kaDao = new KeskustelualueDao(db);
    }

    @Override
    public Keskustelunavaus findOne(Integer key) throws SQLException {
        return (Keskustelunavaus) database.queryAndCollect("SELECT * FROM Keskustelunavaus WHERE avaus_id = ?", rs -> new Keskustelunavaus(rs.getInt("avaus_id"), rs.getInt("alue_id"), rs.getString("nimi")), key).get(0);

    }

    public List findAllWithId(Integer key) throws SQLException {
        return database.queryAndCollect("SELECT k.nimi, k.avaus_id, k.alue_id, COUNT(v.sisalto) AS viesteja, MAX(v.aika) AS viimeisin FROM Keskustelunavaus k INNER JOIN Viesti v ON k.avaus_id = v.avaus_id WHERE k.alue_id = ? GROUP BY k.avaus_id ORDER BY v.aika DESC LIMIT 10", rs -> new Keskustelunavaus(rs.getInt("avaus_id"), rs.getInt("alue_id"), rs.getString("nimi"), rs.getString("viimeisin")), key);
    }
    

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE * FROM Keskustelunavaus WHERE id = ?");
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
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelunavaus(alue_id, nimi) VALUES (?, ?)");
        stmt.setObject(1, t.getAlue_id());
        stmt.setObject(2, t.getNimi());
        stmt.executeUpdate();
        stmt.close();
        connection.close();
        return t;
    }

    @Override
    public void update(String key, Keskustelunavaus t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Keskustelunavaus> findAll() throws SQLException {
        return database.queryAndCollect("SELECT * FROM Keskustelunavaus", rs -> new Keskustelunavaus(rs.getInt("avaus_id"), rs.getInt("alue_id"), rs.getString("nimi")));
    }

}
