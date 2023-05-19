package app.lib.queryBuilders;


public class ColumnEntry {
  private String name;
  private SQLServerTypes type;
  private InlineConstraints[] constraints;

  public ColumnEntry(String name, SQLServerTypes type, InlineConstraints... constraints) {
    this.name = name;
    this.type = type;
    this.constraints = constraints;
  }

  @Override
  public String toString() {
    var strBuilder = new StringBuilder();

    if (constraints.length != 0) {
      strBuilder.append(" ");
    }

    for (int i = 0 ; i < constraints.length; i++) {
      strBuilder.append(constraints[i].toString());

      if (i != constraints.length - 1) {
        strBuilder.append(" ");
      }

    }

    return this.name + " " + this.type.toString() + strBuilder.toString();
  }

}
