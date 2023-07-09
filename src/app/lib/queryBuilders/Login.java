package app.lib.queryBuilders;

public class Login implements QueryBuilder {
	private final static String template = "CREATE LOGIN %s WITH PASSWORD = '%s';";
	private final static String templateW = "CREATE LOGIN [%s] FROM WINDOWS;";
	private String username;
	private String password;
	private boolean windowsAuth;
	
	public Login(String username,String password) {
		this.username = username;
		this.password = password;
	}

	public Login(String username,String password, boolean windowsAuth) {
		this.username = username;
		this.password = password;
		this.windowsAuth = windowsAuth;
	}
	
	@Override
	public String generateQuery(Object... params) {
		if (!windowsAuth) {
			return String.format(Login.template, this.username, this.password);
		}
		
		return String.format(Login.templateW, this.username);
	}

}
