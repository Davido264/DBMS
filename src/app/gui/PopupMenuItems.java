package app.gui;

import app.lib.connector.ConnectionStringBuilder;
import app.lib.connector.SQLOperation;
import app.lib.queryBuilders.DefaultQuerys;
import app.lib.queryBuilders.Select;
import app.lib.queryBuilders.Drop;
import app.lib.result.ResultFactory;
import app.lib.result.Status;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;

public class PopupMenuItems {

	
	private static JMenuItem createQueryItem(Main parent, String databaseName) {
		JMenuItem menuItem = new JMenuItem("Nueva consulta");
		menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	if (databaseName == null) {
            		parent.getTabs().createNewEditorTab(parent.getConnectionStringBuilder().copy());
            	} else {
            		parent.getTabs().createNewEditorTab(parent.getConnectionStringBuilder().copy().withDbName(databaseName));
            	}
            }
		});
		return menuItem;
	}
	
	private static JMenuItem createTablesItem(Main parent, String databaseName) {
		JMenuItem menuItem = new JMenuItem("Nueva tabla");
		menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	if (databaseName == null) {
            		parent.getTabs().createNewTablePropertiesTab(true, parent.getConnectionStringBuilder().copy());
            	} else {
            		parent.getTabs().createNewTablePropertiesTab(true, parent.getConnectionStringBuilder().copy().withDbName(databaseName));
            	}
            }
		});
		return menuItem;
		
	}
	
	private static JMenuItem createUsersItem(Main parent) {
		JMenuItem menuItem = new JMenuItem("Nuevo usuario");
		menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ConnectionStringBuilder newConnectionStringBuilder = parent.getConnectionStringBuilder().copy().withDbName("master");
            	Settings s = parent.getSettings();
            	
            	if (s.adminIntegrado) {
            		newConnectionStringBuilder = newConnectionStringBuilder.withIntegratedSecurity(true);
            	} else {
            		newConnectionStringBuilder = newConnectionStringBuilder.withUserName(s.usuarioAdmin).withPassword(s.claveAdmin);
            	}
            	
            	parent.getTabs().createNewUserTab(newConnectionStringBuilder,"");
            }
		});
		return menuItem;
	}

	private static JMenuItem createNewDatabaseItem(Main parent) {
		JMenuItem menuItem = new JMenuItem("Crear nueva base de datos");
		menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
				String databaseName = JOptionPane.showInputDialog(parent.getFrame(), "Ingrese el nombre de la nueva base de datos");
				final String regex = "[^a-zA-Z0-9_]";
				boolean validDBName = true;
				
				for (char a : databaseName.toCharArray()) {
					if (Character.toString(a).matches(regex)) {
						validDBName = false;
						break;
					}
				}
				
				if (databaseName == null || !validDBName) {
					JOptionPane.showMessageDialog(parent.getFrame(), "El nombre ingresado contiene caracteres restringidos por el programa","Error de entrada", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try (var sqlOperation = new SQLOperation(parent.getConnectionStringBuilder().copy().withDbName("master").build())) {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					var result = sqlOperation.executeRaw(String.format(DefaultQuerys.createDatabaseQuery, databaseName));
					parent.getResultReader().loadResult(result);
					// TODO: split pane revalidate
					parent.getTreeView().loadDatabaseObjects();
				} catch(Exception ex) {
					parent.getResultReader().loadResult(ResultFactory.fromException(ex));
				} finally {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

            }
        });
		
		return menuItem;
	}
	
	
	public static void fillRootPopupMenu(JPopupMenu popupMenu, Main parent) {
		
	    popupMenu.add(createNewDatabaseItem(parent));
	    popupMenu.add(createQueryItem(parent,"master"));
	}
	
	public static void fillDatabasePopupMenu(JPopupMenu popupMenu, Main parent, String database) {
		JMenuItem menuItem1 = new JMenuItem("Eliminar Base de datos");
		menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
				try (var sqlOperation = new SQLOperation(parent.getConnectionStringBuilder().copy().withDbName("master").build())) {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					var query = Drop.database(database).generateQuery();
					var result = sqlOperation.executeRaw(query);
					parent.getResultReader().loadResult(result);
					parent.getTreeView().loadDatabaseObjects();
				} catch(Exception ex) {
					parent.getResultReader().loadResult(ResultFactory.fromException(ex));
				} finally {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
            } 
		});
		
		popupMenu.add(createQueryItem(parent,database));
		popupMenu.add(createTablesItem(parent,database));
		popupMenu.add(menuItem1);
	}
	
	public static void fillTablesPopupMenu(JPopupMenu popupMenu, Main parent, String table, String database) {
		String[] arr = table.split("\\.");
		String splittedTable = arr[arr.length - 1];
		JMenuItem menuItem1 = new JMenuItem("Seleccionar los primeros 100");
		menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
				try (var sqlOperation = new SQLOperation(parent.getConnectionStringBuilder().copy().withDbName(database).build())) {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					var query = Select.all(splittedTable, 100).generateQuery();
					var result = sqlOperation.executeRaw(query);
					parent.getTabs().createNewEditorTab(query,parent.getConnectionStringBuilder().copy());
					parent.getResultReader().loadResult(result);
				} catch(Exception ex) {
					parent.getResultReader().loadResult(ResultFactory.fromException(ex));
				} finally {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
            } 
		});

		
		JMenuItem menuItem2 = new JMenuItem("Eliminar Tabla");
		menuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
				try (var sqlOperation = new SQLOperation(parent.getConnectionStringBuilder().build())) {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					var query = Drop.table(splittedTable).generateQuery();
					var result = sqlOperation.executeRaw(query);
					parent.getResultReader().loadResult(result);
					parent.getTreeView().loadDatabaseObjects();
				} catch(Exception ex) {
					parent.getResultReader().loadResult(ResultFactory.fromException(ex));
				} finally {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
            } 
		});
		
		JMenuItem menuItem3 = new JMenuItem("Particionar Tabla");
		menuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
				try (var sqlOperation = new SQLOperation(parent.getConnectionStringBuilder().build())) {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					var query = Drop.table(table).generateQuery();
					var result = sqlOperation.executeRaw(query);
					parent.getResultReader().loadResult(result);
					parent.getTreeView().loadDatabaseObjects();
				} catch(Exception ex) {
					parent.getResultReader().loadResult(ResultFactory.fromException(ex));
				} finally {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
            } 
		});
		
		
		popupMenu.add(menuItem1);
		popupMenu.add(createQueryItem(parent,database));
		popupMenu.add(createTablesItem(parent,database));
		popupMenu.add(menuItem2);
	}
	
	
	public static void fillUsersPopupMenu(JPopupMenu popupMenu, Main parent, String user) {		
		String[] arr = user.split("\\\\");
		String splittedUser = arr[arr.length - 1];
		
		JMenuItem menuItem1 = new JMenuItem("Eliminar Usuario");
		menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ConnectionStringBuilder newConnectionStringBuilder = parent.getConnectionStringBuilder().copy().withDbName("master");
            	Settings s = parent.getSettings();
            	
            	if (s.adminIntegrado) {
            		newConnectionStringBuilder = newConnectionStringBuilder.withIntegratedSecurity(true);
            	} else {
            		newConnectionStringBuilder = newConnectionStringBuilder.withUserName(s.usuarioAdmin).withPassword(s.claveAdmin);
            	}
            	
				try (var sqlOperation = new SQLOperation(newConnectionStringBuilder.build())) {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					var query = Drop.login(user).generateQuery();
					var result = sqlOperation.executeRaw(query);
					parent.getResultReader().loadResult(result);
					query = DefaultQuerys.getDatabasesQuery;
					result = sqlOperation.executeRaw(query);
					if (result.getStatus().equals(Status.FAILURE)) {
						parent.getResultReader().loadResult(result);
						return;
					}
			
					ConnectionStringBuilder copy = newConnectionStringBuilder.copy();
					for (Object database : result.getTable().get("name")) {
						try (SQLOperation op = new SQLOperation(copy.withDbName((String)database).build())) {
							query = String.format(DefaultQuerys.dropUserIfExistsQuery, splittedUser,splittedUser);
							System.out.println(query);
							result = op.executeRaw(query);
							System.out.println(result.getStatus());
							parent.getResultReader().loadResult(result);
						}
					}
			
					parent.getTreeView().loadDatabaseObjects();
				} catch(Exception ex) {
					parent.getResultReader().loadResult(ResultFactory.fromException(ex));
				} finally {
					parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
            } 
		});
	
		JMenuItem menuItem2 = new JMenuItem("Modificar Usuario");
		menuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ConnectionStringBuilder newConnectionStringBuilder = parent.getConnectionStringBuilder().copy().withDbName("master");
            	Settings s = parent.getSettings();
            	
            	if (s.adminIntegrado) {
            		newConnectionStringBuilder = newConnectionStringBuilder.withIntegratedSecurity(true);
            	} else {
            		newConnectionStringBuilder = newConnectionStringBuilder.withUserName(s.usuarioAdmin).withPassword(s.claveAdmin);
            	}
            	
            	parent.getTabs().createModifyUserTab(newConnectionStringBuilder,user);
            } 
		});
		
		popupMenu.add(createUsersItem(parent));
		popupMenu.add(menuItem2);
		popupMenu.add(menuItem1);
	}
	
	public static void fillUsersSectionPopupMenu(JPopupMenu popupMenu, Main parent) {
		popupMenu.add(createQueryItem(parent,"master"));
		popupMenu.add(createUsersItem(parent));
	}
	
	public static void fillDatabaseSectionPopupMenu(JPopupMenu popupMenu, Main parent) {
		popupMenu.add(createQueryItem(parent,"master"));
		popupMenu.add(createNewDatabaseItem(parent));
	}
	
}
