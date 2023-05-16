package test;

import app.lib.connector.*;


public class TestConnection {

  public static void main(String[] args) {
    Utils.print("Test connection to master with credentials");

    var query = "select * from sys.database_principals";
    var connectionString = new ConnectionStringBuilder()
      .withHost("localhost")
      .withEncrypt(true)
      .withPort(1433)
      .withDbName("master")
      .withUserName("sa")
      .withPassword("PasswordO1")
      .withTrustServerCertificates(true)
      .build();
    
    try (var sqlConnection = new SQLConnection(connectionString)) {
      Utils.print(sqlConnection.executeRaw(query));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
