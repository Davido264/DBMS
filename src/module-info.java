/**
 * 
 */
/**
 * @author david
 *
 */
module DatabaseMagementSystem {
	requires java.sql;
	requires java.desktop;
	requires com.google.gson;
	opens app.gui to com.google.gson;
}