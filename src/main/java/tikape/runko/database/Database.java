package tikape.runko.database;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.h2.tools.RunScript;
import tikape.runko.database.Collector;

public class Database<T> {

    private boolean debug;
    private String address;

    public Database(String address) throws Exception {
        this.address = address;

        Connection conn = getConnection();

        try {
            // If database has not yet been created, insert content
            RunScript.execute(conn, new FileReader("database-schema.sql"));
            RunScript.execute(conn, new FileReader("database-import.sql"));
        } catch (Throwable t) {
        }

        conn.close();
    }

    public void init() {
        List<String> lauseet = sqliteLauseet();

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
}    
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(address, "sa", "");
    }

    public void setDebugMode(boolean d) {
        debug = d;
    }

    public int update(String updateQuery, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(updateQuery);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        int changes = stmt.executeUpdate();

        if (debug) {
            System.out.println("---");
            System.out.println(updateQuery);
            System.out.println("Changed rows: " + changes);
            System.out.println("---");
        }

        stmt.close();
        conn.close();

        return changes;
    }

    public List<T> queryAndCollect(String query, Collector<T> col, Object... params) throws SQLException {
        Connection conn = getConnection();
        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }

        List<T> rows = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            if (debug) {
                System.out.println("---");
                System.out.println(query);
                debug(rs);
                System.out.println("---");
            }

            rows.add(col.collect(rs));
        }

        rs.close();
        stmt.close();
        conn.close();

        return rows;
    }

    private void debug(ResultSet rs) throws SQLException {
        int columns = rs.getMetaData().getColumnCount();
        for (int i = 0; i < columns; i++) {
            System.out.print(
                    rs.getObject(i + 1) + ":"
                    + rs.getMetaData().getColumnName(i + 1) + "  ");
        }

        System.out.println();
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE Keskustelualue (alue_id integer PRIMARY KEY, nimi varchar(50) NOT NULL);");
        lista.add("CREATE TABLE Keskustelunavaus (avaus_id integer PRIMARY KEY, alue_id integer NOT NULL, nimi varchar(75) NOT NULL, FOREIGN KEY (alue_id) REFERENCES Keskustelualue (alue_id));");
        lista.add("CREATE TABLE Viesti (viesti_id integer PRIMARY KEY, avaus_id integer NOT NULL, aika varchar NOT NULL, sisalto varchar(1000) NOT NULL, nimimerkki varchar(30) NOT NULL, FOREIGN KEY (avaus_id) REFERENCES Keskustelunavaus (avaus_id));");
        lista.add("INSERT INTO Keskustelualue (nimi) VALUES('Tietokoneet');");
        lista.add("INSERT INTO Keskustelualue (nimi) VALUES('Elokuvat');");
        lista.add("INSERT INTO Keskustelualue (nimi) VALUES('Musiikki');");


        return lista;
}
}
