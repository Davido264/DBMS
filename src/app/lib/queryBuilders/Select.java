package app.lib.queryBuilders;

public class Select implements QueryBuilder{
  private final String template = "SELECT%s %s FROM [%s];";
  private String tableName;
  private ColumnName[] columns;  
  private int max;

  public final static Select all(String tableName, int max) {
    return new Select(tableName, max, new ColumnName("*"));
  }

  public final static Select all(String tableName) {
    return new Select(tableName, new ColumnName("*"));
  }

  public Select(String tableName, int max,ColumnName... columns) {
    this.tableName = tableName;
    this.columns = columns;
    this.max = max;
  }

  public Select(String tableName,ColumnName... columns) {
    this.tableName = tableName;
    this.columns = columns;
  }

  @Override
  public String generateQuery(Object... args) {
    var strBuilder = new StringBuilder();
    for (int i = 0 ; i < columns.length; i++) {
      if (columns[i].toString().endsWith("*")) {
    	  strBuilder.append(String.format("[%s].%s", this.tableName,columns[i].toString()));
      } else {
    	  strBuilder.append(String.format("[%s].[%s]", this.tableName,columns[i].toString()));
      }

      if (i != columns.length - 1) {
        strBuilder.append(", ");
      }
    }

    String limit = "";
    if (this.max > 0) {
      limit = " TOP " + this.max;
    }

    return String.format(this.template, limit, strBuilder.toString(), this.tableName);
  }
}
