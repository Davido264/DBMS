package app.lib.queryBuilders;

public class AlterServerRole implements QueryBuilder {
	private final String template = "ALTER SERVER ROLE [%s] ADD MEMBER %s;";
	private String user;
	private String role;

	public AlterServerRole(String role,String user) {
		this.role = role;
		this.user = user;
	}
	
	@Override
	public String generateQuery(Object... params) {
		return String.format(this.template, this.role,this.user);
	}

}
