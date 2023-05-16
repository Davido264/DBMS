package app.lib.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLConnection implements  java.lang.AutoCloseable {
  private final String classPath = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  private final String connectionString;
  private Connection connection;

  public SQLConnection(String connectionString) {
    this.connectionString = connectionString; 
  }

  private Connection getConnection() throws ClassNotFoundException,SQLException {
    Class.forName(classPath);
    connection = DriverManager.getConnection(this.connectionString);
    return connection;
  }

  public boolean executeRaw(String sqlStatement) {
    try {
      this.connection = this.getConnection();
      Statement statement = this.connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sqlStatement);
      return true;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }


  @Override
  public void close() throws SQLException {
    this.connection.close();
  }

}
