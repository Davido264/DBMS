package app.lib.queryBuilders;


// Pudimos usar Enumerados, pero necesitábamos mutabilidad, así que tomamos lo que nos interesa de los enumerados
public class SQLServerTypes {
    public static final SQLServerTypes BIT = new SQLServerTypes("BIT");
    public static final SQLServerTypes TINYINT = new SQLServerTypes("TINYINT");
    public static final SQLServerTypes SMALLINT = new SQLServerTypes("SMALLINT");
    public static final SQLServerTypes INT = new SQLServerTypes("INT");
    public static final SQLServerTypes BIGINT = new SQLServerTypes("BIGINT");
    public static final SQLServerTypes FLOAT = new SQLServerTypes("FLOAT");
    public static final SQLServerTypes REAL = new SQLServerTypes("REAL");
    public static final SQLServerTypes DECIMAL = new SQLServerTypes("DECIMAL");
    public static final SQLServerTypes NUMERIC = new SQLServerTypes("NUMERIC");
    public static final SQLServerTypes MONEY = new SQLServerTypes("MONEY");
    public static final SQLServerTypes SMALLMONEY = new SQLServerTypes("SMALLMONEY");
    public static final SQLServerTypes DATE = new SQLServerTypes("DATE");
    public static final SQLServerTypes TIME = new SQLServerTypes("TIME");
    public static final SQLServerTypes DATETIME = new SQLServerTypes("DATETIME");
    public static final SQLServerTypes DATETIME2 = new SQLServerTypes("DATETIME2");
    public static final SQLServerTypes DATETIMEOFFSET = new SQLServerTypes("DATETIMEOFFSET");
    public static final SQLServerTypes CHAR = new SQLServerTypes("CHAR");
    public static final SQLServerTypes VARCHAR = new SQLServerTypes("VARCHAR");
    public static final SQLServerTypes TEXT = new SQLServerTypes("TEXT");
    public static final SQLServerTypes NCHAR = new SQLServerTypes("NCHAR");
    public static final SQLServerTypes NVARCHAR = new SQLServerTypes("NVARCHAR");
    public static final SQLServerTypes NTEXT = new SQLServerTypes("NTEXT");
    public static final SQLServerTypes BINARY = new SQLServerTypes("BINARY");
    public static final SQLServerTypes VARBINARY = new SQLServerTypes("VARBINARY");
    public static final SQLServerTypes IMAGE = new SQLServerTypes("IMAGE");
    public static final SQLServerTypes UNIQUEIDENTIFIER = new SQLServerTypes("UNIQUEIDENTIFIER");

  private int length;
  private String name;
  
  private SQLServerTypes(String name) {
	  this.name = name;
  }

  public SQLServerTypes withLengt(int length) {
	SQLServerTypes copy = new SQLServerTypes(this.name);
	copy.length = length;
    return copy;
  }

  @Override
  public String toString() {
    if (this.length > 0) {
      return this.name + "(" + this.length + ")";
    } else {
      return this.name;
    }
  }
  
  public static SQLServerTypes[] values() {
	  return new SQLServerTypes[] {
	      SQLServerTypes.BIT,
	      SQLServerTypes.TINYINT,
	      SQLServerTypes.SMALLINT,
	      SQLServerTypes.INT,
	      SQLServerTypes.BIGINT,
	      SQLServerTypes.FLOAT,
	      SQLServerTypes.REAL,
	      SQLServerTypes.DECIMAL,
	      SQLServerTypes.NUMERIC,
	      SQLServerTypes.MONEY,
	      SQLServerTypes.SMALLMONEY,
	      SQLServerTypes.DATE,
	      SQLServerTypes.TIME,
	      SQLServerTypes.DATETIME,
	      SQLServerTypes.DATETIME2,
	      SQLServerTypes.DATETIMEOFFSET,
	      SQLServerTypes.CHAR,
	      SQLServerTypes.VARCHAR,
	      SQLServerTypes.TEXT,
	      SQLServerTypes.NCHAR,
	      SQLServerTypes.NTEXT,
	      SQLServerTypes.BINARY,
	      SQLServerTypes.VARBINARY,
	      SQLServerTypes.IMAGE,
	      SQLServerTypes.UNIQUEIDENTIFIER,
	  };
  }
}
