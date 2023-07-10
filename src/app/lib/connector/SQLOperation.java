package app.lib.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Savepoint;
import app.lib.result.*;


public class SQLOperation implements  java.lang.AutoCloseable {
  private final String CLASSPATH = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  private final String connectionString;
  private Connection connection;
  private Statement statement;
  private Savepoint latestSavePoint;

  public SQLOperation(String connectionString) {
    this.connectionString = connectionString; 
  }

  private void connect() throws ClassNotFoundException,SQLException {
    Class.forName(CLASSPATH);
    this.connection = DriverManager.getConnection(this.connectionString);
  }

  public Result executeRaw(String sqlStatement) {
    try {
      this.connect();
      this.connection.setAutoCommit(false);
      this.latestSavePoint = this.connection.setSavepoint();
      this.statement = this.connection.createStatement();
      var hasResultSet = statement.execute(sqlStatement);
      var rowsAffected = statement.getUpdateCount();


      if (hasResultSet) {
        return ResultFactory.fromResultSet(statement.getResultSet());
      } else {
        var warning = statement.getWarnings();
        if (warning != null) {
          return ResultFactory.fromString(warning.getMessage());
        }
        return ResultFactory.fromString("Filas afectadas: " + rowsAffected);
      }

    } catch (Exception e) {
      try {
        this.connection.rollback(this.latestSavePoint);
      } catch (SQLException ex) {
    	  ex.printStackTrace();
      }
      return ResultFactory.fromException(e);
    }
  }


  @Override
  public void close() throws SQLException {
	  if (this.statement != null && !this.statement.isClosed()) {
		  this.statement.close();
	  }
	  if (this.connection != null && !this.connection.isClosed()) {
		  this.connection.commit();
		  this.latestSavePoint = this.connection.setSavepoint();
		  this.connection.close();
	  }
  }

}
