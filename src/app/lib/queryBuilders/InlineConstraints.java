package app.lib.queryBuilders;


public enum InlineConstraints {
    PRIMARY_KEY("PRIMARY KEY"),
    NOT_NULL("NOT NULL"),
    UNIQUE("UNIQUE"),
    FOREIGN_KEY("FOREIGN KEY REFERENCES %s(%s) ON DELETE CASCADE");
    
    private String syntax;
    
    private InlineConstraints(String syntax) {
        this.syntax = syntax;
    }
    
    @Override
    public String toString() {
        return syntax;
    }
}

//En este enumerado, se incluyen los siguientes constraints:
//
//    PRIMARY_KEY: Representa la constraint de clave primaria (PRIMARY KEY).
//    UNIQUE: Representa la constraint de valor único (UNIQUE).
//    FOREIGN_KEY: Representa la constraint de clave foránea (FOREIGN KEY).
//    CHECK: Representa la constraint de validación (CHECK).
//
//Cada elemento del enumerado tiene su correspondiente sintaxis en forma de cadena. Al utilizar el método toString() en un elemento del enumerado, obtendrás la sintaxis adecuada para agregar el constraint en la misma línea que declaras la columna.
//
//Aquí tienes un ejemplo de cómo utilizarlo:
//
//java
//
//String createTableStatement = "CREATE TABLE MyTable (" +
//        "ID INT " + SQLServerTableConstraint.PRIMARY_KEY +
//        ", Name VARCHAR(50) " + SQLServerTableConstraint.NOT_NULL +
//        ", CONSTRAINT FK_MyTable_OtherTable FOREIGN KEY (OtherTableID) REFERENCES OtherTable(ID) " + SQLServerTableConstraint.ON_DELETE_CASCADE +
//        ")";
//
//En este ejemplo, se utiliza SQLServerTableConstraint.PRIMARY_KEY para agregar la constraint de clave primaria en la misma línea que se declara la columna ID. Del mismo modo, se muestra cómo agregar otros constraints, como SQLServerTableConstraint.NOT_NULL y SQLServerTableConstraint.ON_DELETE_CASCADE, en la misma línea que declaras otras columnas y constraints.
//
//Recuerda que la sintaxis de los constraints puede variar según la estructura y requisitos específicos de tu base de datos. Asegúrate de adaptar y ajustar el código según tus necesidades.
