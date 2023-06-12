package app.lib.queryBuilders;

public class Drop implements QueryBuilder {
  private final String template = "DROP %s %s;";
  private String tableName;
  private DropType type;

  public static Drop table(String tableName) {
	  return new Drop(tableName,DropType.TABLE);
  }
  
  public static Drop database(String databaseName) {
	  return new Drop(databaseName,DropType.DATABASE);
  }
  
  public static Drop trigger(String triggerName) {
	  return new Drop(triggerName,DropType.TRIGGER);
  }
  
  public static Drop user(String username) {
	  return new Drop(username,DropType.USER);
  }
  
  public static Drop login(String login) {
	  return new Drop(login,DropType.LOGIN);
  }
  
  private Drop(String tableName, DropType type) {
    this.tableName = tableName;
    this.type = type;
  }

  @Override
  public String generateQuery(Object... params) {
    return String.format(this.template, this.type.toString(),this.tableName);
  }

  
  
  private enum DropType {
	  TABLE,DATABASE,TRIGGER,USER,LOGIN;
  }
  
}
