package test;

import java.nio.charset.Charset;
import java.util.Scanner;

import app.lib.connector.ConnectionStringBuilder;
import app.lib.connector.SQLOperation;
import app.lib.result.*;

public class TestOperationWithSimpleApplication {

  final static Scanner scanner = new Scanner(System.in, Charset.forName("UTF8"));

  public static void main(String[] args) {
    var connectionString = new ConnectionStringBuilder()
      .withHost("localhost")
      .withEncrypt(true)
      .withPort(1433)
      .withDbName("master")
      .withUserName("sa")
      .withPassword("PasswordO1")
      .withTrustServerCertificates(true)
      .build();


    System.out.println("Aplicacion simple, escribe tu código sql y ejecútalo!");  
    while (true) {
      String input = "";
      while (true) {
        System.out.print("> "); 
        input = input + scanner.nextLine();

        if (input.endsWith(";")) {
          break;
        }

      }

      if (input.equalsIgnoreCase("exit;")) {
        System.out.println("Sesión terminada.");
        break;
      }

      try (var operation = new SQLOperation(connectionString)) {
        var result = operation.executeRaw(input);

        if (result.getStatus().equals(Status.FAILURE)) {
          System.out.println(result.getReason());
          continue;
        }

        if (result.getType().equals(ResultType.TABLE)) {
          for (String key : result.getTable().keySet()) {
            System.out.print(key + ": { ");
            result.getTable()
              .get(key)
              .stream()
              .map(e -> e == null ? "null": e.toString())
              .forEach(e -> System.out.print(e + ", "));
            System.out.println(" }");
          }

          continue;
        }

        System.out.println(result.getText());

      } catch(Exception e) {
        e.printStackTrace();
        break;
      }

    }
  }


}
