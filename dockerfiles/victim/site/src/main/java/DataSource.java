import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import io.github.cdimascio.dotenv.Dotenv;

public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    private static Dotenv dotenv = Dotenv.load();
    static {
        config.setJdbcUrl( dotenv.get("URL") );
        config.setUsername( dotenv.get("USERNAME") );
        config.setPassword( dotenv.get("PASSWORD") );
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    public static void setup() {
        try(Connection con = getConnection();
            PreparedStatement stmt1 = con.prepareStatement("CREATE TABLE IF NOT EXISTS user(username TEXT, password TEXT, isAdmin BOOL);");
            PreparedStatement stmt2 = con.prepareStatement("INSERT IGNORE INTO user VALUES('user', 'user', False);")) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}