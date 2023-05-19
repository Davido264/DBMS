package app.lib.queryBuilders;

public enum SQLServerTypes {
    BIT,
    TINYINT,
    SMALLINT,
    INT,
    BIGINT,
    FLOAT,
    REAL,
    DECIMAL,
    NUMERIC,
    MONEY,
    SMALLMONEY,
    DATE,
    TIME,
    DATETIME,
    DATETIME2,
    DATETIMEOFFSET,
    CHAR,
    VARCHAR,
    TEXT,
    NCHAR,
    NVARCHAR,
    NTEXT,
    BINARY,
    VARBINARY,
    IMAGE,
    UNIQUEIDENTIFIER;

  private int length;
  public SQLServerTypes withLengt(int length) {
    this.length = length;
    return this;
  }

  @Override
  public String toString() {
    if (this.length > 0) {
      return name() + "(" + this.length + ")";
    }
    return name();
  }
}
