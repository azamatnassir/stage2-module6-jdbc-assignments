package jdbc;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(CustomDataSource.class.getName()).severe("Failed to load JDBC driver: " + driver);
        }
    }

    public static CustomDataSource getInstance(){
        if (instance == null) {
            synchronized (CustomDataSource.class) {
                if (instance == null) {
                    Properties properties = loadProperties("app.properties");
                    String driver = properties.getProperty("postgres.driver");
                    String url = properties.getProperty("postgres.url");
                    String name = properties.getProperty("postgres.name");
                    String password = properties.getProperty("postgres.password");
                    instance = new CustomDataSource(driver, url, name, password);
                }
            }
        }
        return instance;
    }

    private static Properties loadProperties(String propertiesFilename){
        Properties properties = new Properties();
        ClassLoader loader = CustomDataSource.class.getClassLoader();
        try (InputStream stream = loader.getResourceAsStream(propertiesFilename)){
            if (stream == null){
                throw new FileNotFoundException();
            }
            properties.load(stream);
        }catch (IOException e){
            e.fillInStackTrace();
        }
        return properties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new CustomConnector().getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new CustomConnector().getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {

    }

    @Override
    public void setLoginTimeout(int seconds) {

    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public Logger getParentLogger() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }
}
