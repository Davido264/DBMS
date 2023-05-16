package test;

import app.lib.connector.*;


public class TestConnectionStringBuilder {
  
  public static void main(String[] args) {

    Utils.print("Test case: jdbc:sqlserver://localhost;encrypt=true;user=MyUserName;password=1234;");
    var expected = "jdbc:sqlserver://localhost;encrypt=true;user=MyUserName;password=1234;";
    var connectionString = new ConnectionStringBuilder()
      .withHost("localhost")
      .withEncrypt(true)
      .withUserName("MyUserName")
      .withPassword("1234")
      .build();
      
    Utils.test(expected,connectionString);
      
    Utils.print("Test case: jdbc:sqlserver://localhost;encrypt=true;databaseName=AdventureWorks;user=MyUserName;trustServerCertificate=true;");
    expected = "jdbc:sqlserver://localhost;encrypt=true;databaseName=AdventureWorks;user=MyUserName;trustServerCertificate=true;";
    connectionString = new ConnectionStringBuilder()
      .withHost("localhost")
      .withEncrypt(true)
      .withDbName("AdventureWorks")
      .withUserName("MyUserName")
      .withTrustServerCertificates(true)
      .build();
      
    Utils.test(expected,connectionString);

    Utils.print("Test case: jdbc:sqlserver://localhost:1433;encrypt=true;databaseName=AdventureWorks;password=1234;trustServerCertificate=true;");
    expected = "jdbc:sqlserver://localhost:1433;encrypt=true;databaseName=AdventureWorks;password=1234;trustServerCertificate=true;";
    connectionString = new ConnectionStringBuilder()
      .withHost("localhost")
      .withPort(1433)
      .withEncrypt(true)
      .withDbName("AdventureWorks")
      .withPassword("1234")
      .withTrustServerCertificates(true)
      .build();
      
    Utils.test(expected,connectionString);
  }
}
