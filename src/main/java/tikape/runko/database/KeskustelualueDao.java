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
        return (Keskustelualue) database.queryAndCollect("SELECT * FROM Keskustelualue WHERE alue_id = ?", rs -> new Keskustelualue(rs.getInt("alue_id"), rs.getString("nimi")), key).get(0);

    }

    @Override
    public List findAll() throws SQLException {
        return database.queryAndCollect("SELECT Keskustelualue.nimi, Keskustelualue.alue_id, COUNT(Viesti.sisalto) AS viesteja, MAX(Viesti.aika) AS viimeisin FROM Keskustelualue LEFT JOIN Keskustelunavaus ON Keskustelualue.alue_id = Keskustelunavaus.alue_id LEFT JOIN Viesti ON Keskustelunavaus.avaus_id = Viesti.avaus_id GROUP BY Keskustelualue.nimi ORDER BY Keskustelualue.nimi COLLATE NOCASE", rs -> new Keskustelualue(Integer.parseInt(rs.getString("alue_id")), rs.getString("nimi"), rs.getString("viimeisin")));
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
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelualue(nimi) VALUES (?)");
        stmt.setObject(1, t.getNimi());
        stmt.executeUpdate();
        stmt.close();
        connection.close();
        return t;
    }

    @Override
    public void update(String key, Keskustelualue t) throws SQLException {

    }

}
