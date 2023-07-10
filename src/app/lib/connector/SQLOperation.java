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

  public SQLOperation(String connectionString, boolean autoCommit) throws ClassNotFoundException,SQLException {
    this.connectionString = connectionString; 
    this.connect(autoCommit); 
  }
  
  public SQLOperation(String connectionString) throws ClassNotFoundException,SQLException {
    this.connectionString = connectionString; 
    this.connect(); 
  }
  

  private void connect(boolean autoCommit) throws ClassNotFoundException,SQLException {
    Class.forName(CLASSPATH);
    this.connection = DriverManager.getConnection(this.connectionString);
    this.connection.setAutoCommit(autoCommit);
    if (!this.connection.getAutoCommit()) {
      this.latestSavePoint = this.connection.setSavepoint();
    }
  }
  
  private void connect() throws ClassNotFoundException,SQLException {
    Class.forName(CLASSPATH);
    this.connection = DriverManager.getConnection(this.connectionString);
    this.connection.setAutoCommit(false);
    this.latestSavePoint = this.connection.setSavepoint();
  }
  
  public Result executeRaw(String sqlStatement) {
    try {
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

  public void toggleAutoCommit() throws SQLException {
	  this.connection.setAutoCommit(!this.connection.getAutoCommit());
  }

  @Override
  public void close() throws SQLException {
	  if (this.statement != null && !this.statement.isClosed()) {
		  this.statement.close();
	  }
	  if (this.connection != null && !this.connection.isClosed()) {
		  if (!this.connection.getAutoCommit()) {
			  this.connection.commit();
			  this.latestSavePoint = this.connection.setSavepoint();
		  }
		  this.connection.close();
	  }
  }

}
