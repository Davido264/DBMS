package app.lib.connector;

public class ConnectionStringBuilder {
  private final String template = "jdbc:sqlserver://";
  private String host;
  private String instance;
  private int port;
  private boolean encrypt;
  private String dbName;
  private String userName;
  private String passoword;
  private boolean trustServerCertificates;

  public String build() {
    var sb = new StringBuilder(template);

    if (this.host != null) {
      sb.append(this.host);
      if (this.instance != null) {
        sb.append("\\" + this.instance);
      }

      if (this.port != 0) {
        sb.append(":" + this.port);
      }
      sb.append(";");
    }

    if (this.encrypt) {
      sb.append("encrypt=true;");
    }

    if (this.dbName != null) {
      sb.append("databaseName=" + this.dbName + ";");
    }

    if (this.userName != null) {
      sb.append("user=" + this.userName + ";");
    }

    if (this.passoword != null) {
      sb.append("password=" + this.passoword + ";");
    }

    if (this.trustServerCertificates) {
      sb.append("trustServerCertificate=true;");
    }

    return sb.toString();
  }
  
  public ConnectionStringBuilder withHost(String host) {
    this.host = host;
    return this;
  }

  public ConnectionStringBuilder withInstance(String instance) {
    this.instance = instance;
    return this;
  }

  public ConnectionStringBuilder withPort(int port) {
    this.port = port;
    return this;
  }

  public ConnectionStringBuilder withEncrypt(boolean encrypt) {
    this.encrypt = encrypt;
    return this;
  }

  public ConnectionStringBuilder withDbName(String dbName) {
    this.dbName = dbName;
    return this;
  }

  public ConnectionStringBuilder withUserName(String userName) {
    this.userName = userName;
    return this;
  }

  public ConnectionStringBuilder withPassword(String passoword) {
    this.passoword = passoword;
    return this;
  }

  public ConnectionStringBuilder withTrustServerCertificates(boolean trustServerCertificates) {
    this.trustServerCertificates = trustServerCertificates;
    return this;
  }

  public String getHost() {
    return this.host;
  }
 
  public String getInstance() {
    return this.instance;
  }

  public int getPort() {
    return this.port;
  }

  public boolean getEncrypt() {
    return this.encrypt;
  }

  public String getDbName() {
    return this.dbName;
  }

  public String getUserName() {
    return this.userName;
  }

  public String getPassword() {
    return this.passoword;
  }

  public boolean getTrustServerCertificates() {
    return this.trustServerCertificates;
  }
}
