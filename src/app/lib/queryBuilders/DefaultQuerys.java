package app.lib.queryBuilders;

public class DefaultQuerys {
	public static final String createDatabaseQuery = "CREATE DATABASE %s;";
	public static final String dropDatabaseQuery = "DROP DATABASE %s;";
	public static final String getDatabasesQuery = "SELECT name FROM sys.databases WHERE database_id > 4;";
	public static final String getColumnsQuery = "SELECT CONCAT(column_name,'(',data_type,')') as name FROM information_schema.columns WHERE table_schema = '%s' AND table_name = '%s';";
	public static final String getTriggersQuery = "SELECT name FROM sys.triggers WHERE parent_id = OBJECT_ID('%s');";
	public static final String getIndexesQuery = "SELECT name FROM sys.indexes WHERE object_id = OBJECT_ID('%s');";
	public static final String getConstraitsQuery = "SELECT CONCAT(constraint_name, '(', constraint_type, ')') as name FROM information_schema.table_constraints WHERE table_schema = '%s' AND table_name = '%s';";
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
	
}
