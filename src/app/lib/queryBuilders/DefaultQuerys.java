package app.lib.queryBuilders;

public class DefaultQuerys {
	public static final String createDatabaseQuery = "CREATE DATABASE %s;";
	public static final String dropDatabaseQuery = "DROP DATABASE %s;";
	public static final String getDatabasesQuery = "SELECT name FROM sys.databases WHERE database_id > 4 AND HAS_DBACCESS(name) = 1;";
	public static final String getColumnsQuery = "SELECT CONCAT(column_name,'(',data_type,' ',character_maximum_length,')') as name FROM information_schema.columns WHERE table_schema = '%s' AND table_name = '%s';";
	public static final String getColumnNamesQuery = "SELECT column_name as name FROM information_schema.columns WHERE table_schema = '%s' AND table_name = '%s';";
	public static final String getTriggersQuery = "SELECT name FROM sys.triggers WHERE parent_id = OBJECT_ID('%s');";
	public static final String getIndexesQuery = "SELECT name FROM sys.indexes WHERE object_id = OBJECT_ID('%s');";
	public static final String getConstraitsQuery = "SELECT CONCAT(constraint_name, '(', constraint_type, ')') as name FROM information_schema.table_constraints WHERE table_schema = '%s' AND table_name = '%s';";
	public static final String getUsersQuery = "SELECT name FROM sys.server_principals WHERE type IN ('S', 'U', 'G');";
	public static final String getTablesQuery = """
SELECT CONCAT(s.name, '.', t.name) AS name 
FROM sys.tables t
INNER JOIN sys.schemas s ON t.schema_id = s.schema_id
WHERE t.type = 'U' AND t.name NOT LIKE 'sys%' AND t.name NOT LIKE 'dt%' AND t.name NOT LIKE 'spt_%' AND t.name NOT LIKE 'MSreplication_options'		
UNION ALL
SELECT CONCAT(s.name, '.', v.name) AS name
FROM sys.views v
INNER JOIN sys.schemas s ON v.schema_id = s.schema_id
WHERE v.is_ms_shipped = 0;
""";
		
	public static final String getPartitionsQuery = "SELECT partition_id as name FROM sys.partitions WHERE object_id = OBJECT_ID('%s');";

	public static final String getPermissionsOverATableQuery = "SELECT DISTINCT(PERMISSION_NAME) FROM fn_my_permissions('%s', 'OBJECT');";
	
	public static final String getRolesQuery = "SELECT name AS name FROM sys.server_principals WHERE type = 'R' AND is_disabled <> 1;";
	public static final String getDBRolesQuery = "SELECT name AS name FROM sys.database_principals WHERE type = 'R';";
	public static final String getUserDBRolesQuery = """
SELECT DP1.name AS name 
FROM sys.database_role_members AS DRM  
RIGHT OUTER JOIN sys.database_principals AS DP1  
    ON DRM.role_principal_id = DP1.principal_id  
LEFT OUTER JOIN sys.database_principals AS DP2  
    ON DRM.member_principal_id = DP2.principal_id  
WHERE DP1.type = 'R'
AND DP2.name = '%s'
ORDER BY DP1.name; 			
""";

	public static final String dropUserIfExistsQuery = """
IF EXISTS (SELECT 1 FROM sys.database_principals WHERE name = '%s' AND type  in ('S', 'U'))
BEGIN
    DROP USER [%s];
END
""";
}
