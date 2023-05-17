package test;

import app.lib.connector.*;

public class TestConnection {

  public static void main(String[] args) {
    Utils.print("Test connection to master with credentials");

    var querySelect = "select * from sys.database_principals";
    var connectionString = new ConnectionStringBuilder()
      .withHost("localhost")
      .withEncrypt(true)
      .withPort(1433)
      .withDbName("master")
      .withUserName("sa")
      .withPassword("PasswordO1")
      .withTrustServerCertificates(true)
      .build();
    
    try (var sqlConnection = new SQLOperation(connectionString)) {
      var result = sqlConnection.executeRaw(querySelect);

      for (String key : result.getTable().keySet()) {
        System.out.print(key + ": { ");
        result.getTable()
          .get(key)
          .stream()
          .map(e -> e == null ? "null": e.toString())
          .forEach(e -> System.out.print(e + "\t"));
        System.out.println(" }");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    
    var queryPrint = "print 'Hello World'";
    try(var sqlConnection = new SQLOperation(connectionString)) {
      var result = sqlConnection.executeRaw(queryPrint);
      System.out.println(result.getText());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  static String rightPad(String inputString,int minLength) {
    int paddingLength = Math.max(0, minLength - inputString.length()); // Calcular la cantidad de espacios necesarios
    
    StringBuilder paddedString = new StringBuilder(minLength);
    paddedString.append(inputString);
    for (int i = 0; i < paddingLength; i++) {
        paddedString.append(" "); // Agregar espacios a la derecha
    }
    
    return paddedString.toString();
  }
}
