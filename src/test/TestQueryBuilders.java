package test;

import app.lib.queryBuilders.*;

public class TestQueryBuilders {

  public static void main(String[] args) {
    testCreateWithoutConstraints();
    testCreateWithConstraints();
    testSelectWithLimit();
    testSelectWithoutLimit();
    testSelectWithLimitAndJustName();
    testSelectWithoutLimitAndJustName();
    testDrop();
    testAlterAdd();
    testAlterDrop();
    testAlterAlter();
    testAlterRename();
    
  }


  static void testCreateWithoutConstraints() {
    var expected = "CREATE TABLE Personas(ID INT, Nombre VARCHAR(50), Apellido VARCHAR(50), Nacimiento DATE, Genero CHAR(1));";
  
    var tablename = "Personas";
    var columns = new ColumnEntry[5];
    columns[0] = new ColumnEntry("ID", SQLServerTypes.INT);
    columns[1] = new ColumnEntry("Nombre", SQLServerTypes.VARCHAR.withLengt(50));
    columns[2] = new ColumnEntry("Apellido", SQLServerTypes.VARCHAR.withLengt(50));
    columns[3] = new ColumnEntry("Nacimiento", SQLServerTypes.DATE);
    columns[4] = new ColumnEntry("Genero", SQLServerTypes.CHAR.withLengt(1));

    var createQuery = new CreateTable(tablename,columns).generateQuery();

    Utils.print("CREATE Without constraints");

    if (expected.equals(createQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testCreateWithConstraints() {
    var expected = "CREATE TABLE Personas(ID INT PRIMARY KEY, Nombre VARCHAR(50) NOT NULL, Apellido VARCHAR(50) NOT NULL, Nacimiento DATE, Genero CHAR(1));";
  
    var tablename = "Personas";
    var columns = new ColumnEntry[5];
    columns[0] = new ColumnEntry("ID", SQLServerTypes.INT, InlineConstraints.PRIMARY_KEY);
    columns[1] = new ColumnEntry("Nombre", SQLServerTypes.VARCHAR.withLengt(50), InlineConstraints.NOT_NULL);
    columns[2] = new ColumnEntry("Apellido", SQLServerTypes.VARCHAR.withLengt(50), InlineConstraints.NOT_NULL);
    columns[3] = new ColumnEntry("Nacimiento", SQLServerTypes.DATE);
    columns[4] = new ColumnEntry("Genero", SQLServerTypes.CHAR.withLengt(1));

    var createQuery = new CreateTable(tablename,columns).generateQuery();

    Utils.print("CREATE With constraints");

    if (expected.equals(createQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testSelectWithLimit() {
    var expected = "SELECT TOP 100 * FROM Personas;";
  
    var tablename = "Personas";
    var selectQuery = Select.all(tablename, 100).generateQuery();

    Utils.print("SELECT * With limit");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testSelectWithoutLimit() {
    var expected = "SELECT * FROM Personas;";
  
    var tablename = "Personas";
    var selectQuery = Select.all(tablename).generateQuery();

    Utils.print("SELECT * Without limit");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testSelectWithoutLimitAndJustName() {
    var expected = "SELECT Nombre FROM Personas;";
  
    var tableName = "Personas";
    var selectQuery = new Select(tableName, new ColumnName("Nombre")).generateQuery();

    Utils.print("SELECT Nombre Without limit");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testSelectWithLimitAndJustName() {
    var expected = "SELECT TOP 100 Nombre FROM Personas;";
  
    var tableName = "Personas";
    var selectQuery = new Select(tableName,100, new ColumnName("Nombre")).generateQuery();

    Utils.print("SELECT Nombre With limit");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testDrop() {
    var expected = "DROP TABLE Personas;";
  
    var tableName = "Personas";
    var selectQuery = Drop.table(tableName).generateQuery();

    Utils.print("DROP Table");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testAlterAdd() {
    var expected = "ALTER TABLE Personas ADD Email VARCHAR(100);";
  
    var tableName = "Personas";
    var selectQuery = AlterTable.add(tableName, new ColumnEntry("Email", SQLServerTypes.VARCHAR.withLengt(100))).generateQuery();

    Utils.print("ALTER TABLE ADD... ");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testAlterDrop() {
    var expected = "ALTER TABLE Personas DROP Email;";
  
    var tableName = "Personas";
    var selectQuery = AlterTable.drop(tableName, "Email").generateQuery();

    Utils.print("ALTER TABLE DROP... ");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testAlterRename() {
    var expected = "ALTER TABLE Personas RENAME Email TO Email2;";
  
    var tableName = "Personas";
    var selectQuery = AlterTable.rename(tableName, "Email", "Email2").generateQuery();

    Utils.print("ALTER TABLE RENAME ... TO ...");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }

  static void testAlterAlter() {
    var expected = "ALTER TABLE Personas ALTER COLUMN Email VARCHAR(50);";
  
    var tableName = "Personas";
    var selectQuery = AlterTable.alter(tableName, new ColumnEntry("Email", SQLServerTypes.VARCHAR.withLengt(50))).generateQuery();

    Utils.print("ALTER TABLE ALTER ...");

    if (expected.equals(selectQuery)) {
      Utils.print("PASS"); 
    } else {
      Utils.print("FAIL"); 
    }
  }
}
