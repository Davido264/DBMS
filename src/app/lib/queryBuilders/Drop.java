package app.lib.queryBuilders;

public class Drop implements QueryBuilder {
  private final String template = "DROP TABLE %s;";
  private String tableName;

  public Drop(String tableName) {
    this.tableName = tableName;
  }

  @Override
  public String generateQuery(Object... params) {
    return String.format(this.template, this.tableName);
  }

}
