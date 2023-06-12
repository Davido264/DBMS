package app.lib.queryBuilders;

public class Login implements QueryBuilder {
	private final static String template = "CREATE LOGIN %s WITH PASSWORD = '%s';";
	private String username;
	private String password;
	
	public Login(String username,String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public String generateQuery(Object... params) {
		return String.format(this.template, this.username, this.password);
	}

}
