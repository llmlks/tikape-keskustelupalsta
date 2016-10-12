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
import tikape.runko.domain.Keskustelualue;

/**
 *
 * @author llmlks
 */
public class KeskustelualueDao implements Dao<Keskustelualue, Integer> {
    
    private Database database;
    
    public KeskustelualueDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Keskustelualue findOne(Integer key) throws SQLException {
        return (Keskustelualue) database.queryAndCollect("SELECT * FROM Keskustelualue WHERE id = ?", rs -> new Keskustelualue(Integer.parseInt(rs.getString("id")), rs.getString("nimi")), key);
  
    }

    @Override
    public List findAll() throws SQLException {
       return database.queryAndCollect("SELECT * FROM Keskustelualue", rs -> new Keskustelualue(Integer.parseInt(rs.getString("alue_id")), rs.getString("nimi")));
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE * FROM Keskustelualue WHERE id = ?");
        stmt.setObject(1, key);

        stmt.executeQuery();
        stmt.close();
        connection.close();
    }

    @Override
    public Keskustelualue create(Keskustelualue t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(String key, Keskustelualue t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
