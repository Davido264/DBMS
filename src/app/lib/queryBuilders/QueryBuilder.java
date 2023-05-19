package app.lib.queryBuilders;


public interface QueryBuilder {
  public String generateQuery(Object... params);
}
