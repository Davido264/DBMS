package app.lib.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultFactory {
  public static Result fromResultSet(ResultSet resultSet) {
    try {
      var metaData = resultSet.getMetaData();
      var columnCount = metaData.getColumnCount();

      var table = new HashMap<String,ArrayList<Object>>(columnCount, 1);

      for (int i = 1; i <= columnCount; i++) {
        var columnName = metaData.getColumnName(i);
        table.put(columnName, new ArrayList<Object>());
      }

      while (resultSet.next()) {
        for (int i = 1; i <= columnCount; i++) {
          var columnName = metaData.getColumnName(i);
          Object value = resultSet.getObject(i);
          table.get(columnName).add(value);
        }
      }
      return new Result()
        .withStatus(Status.CORRECT)
        .withType(ResultType.TABLE)
        .withTable(table);
    } catch (SQLException e) {
      return ResultFactory.fromException(e);
    }

  }

  public static Result fromString(String text) {
    return new Result()
      .withStatus(Status.CORRECT)
      .withType(ResultType.STRING)
      .withText(text);
  }

  public static Result fromException(Exception e) {
    return new Result()
      .withStatus(Status.FAILURE)
      .withType(ResultType.STRING)
      .withReason(e.getLocalizedMessage())
      .withStackTrace(e.getStackTrace());
  }
}
