package app.lib.queryBuilders;

public class AlterUser implements QueryBuilder {
	private final static String template = "ALTER LOGIN %s WITH %s = '%s';";
	private Fields field;
	private String login;
	private String newValue;

	public AlterUser(String login, Fields field, String newValue) {
		this.login = login;
		this.field = field;
		this.newValue = newValue;
	}
	
	@Override
	public String generateQuery(Object... params) {
		System.out.println(String.format(template,this.login, this.field.toString(), this.newValue));
		return String.format(template,this.login, this.field.toString(), this.newValue);
	}
		
	public enum Fields {
		NAME,PASSWORD
	}

}
