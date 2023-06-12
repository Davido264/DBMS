package app.lib.queryBuilders;

public class User implements QueryBuilder {
	private final static String template = "CREATE USER %s FOR LOGIN %s;";
	private String username;
	private String login;
	
	public User(String username,String login) {
		this.username = username;
		this.login = login;
	}
	
	@Override
	public String generateQuery(Object... params) {
		return String.format(this.template, this.username, this.login);
	}
}
