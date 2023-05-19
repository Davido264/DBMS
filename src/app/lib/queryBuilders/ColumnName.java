package app.lib.queryBuilders;


public class ColumnName {
  private String name;
  private String alias;

  public ColumnName(String name, String alias) {
    this.name = name;
    this.alias = alias;
  }

  public ColumnName(String name) {
    this.name = name;
    this.alias = null;
  }

  @Override
  public String toString() {
    if (this.alias == null) {
      return this.name;
    }
    
    return this.name + " AS " + this.alias;
  }
}
