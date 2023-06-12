package app.lib.queryBuilders;

public class CreateTable implements QueryBuilder {
  private final String template = "CREATE TABLE %s(%s);";
  private String tableName;
  private ColumnEntry[] columns;

  public CreateTable(String tableName, ColumnEntry... columns) {
    this.tableName = tableName;
    this.columns = columns;
  }

  @Override
  public String generateQuery(Object... params) {
    var strBuilder = new StringBuilder();
    for (int i = 0 ; i < columns.length; i++) {
      strBuilder.append(columns[i].toString());

      if (i != columns.length - 1) {
        strBuilder.append(", ");
      }

    }

    return String.format(this.template, this.tableName, strBuilder.toString());
  }
  
}
