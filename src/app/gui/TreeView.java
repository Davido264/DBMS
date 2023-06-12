package app.gui;

import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import app.lib.connector.SQLOperation;
import app.lib.queryBuilders.DefaultQuerys;
import app.lib.result.ResultFactory;
import app.lib.result.ResultType;
import app.lib.result.Status;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class TreeView extends JPanel {

	
	private JTree tree;
	private Main parent;
	private JScrollPane scrollPane;
	private JPopupMenu popupMenu;
	final int ROOT_LEVEL = 0;
	final int SECTION_LEVEL = 1;
	final int DATABASE_LEVEL = 2;
	final int TABLES_LEVEL = 3;
	final int COLUMNS_LEVEL = 5;
	final int USERS_LEVEL = 2;
	
	
	/**
	 * Create the panel.
	 */
	public TreeView(Main parent) {
		this.parent = parent;
		this.scrollPane = new JScrollPane();
		this.popupMenu = new JPopupMenu();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(this.scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(this.scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
		);
		setLayout(groupLayout);

	}

	
	public void loadDatabaseObjects() {
		try (var operation = new SQLOperation(this.parent.getConnectionStringBuilder().build())) {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			var result = operation.executeRaw(DefaultQuerys.getDatabasesQuery);
			if (result.getStatus().equals(Status.FAILURE) || result.getType().equals(ResultType.STRING)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}
			
			var databases = result.getTable().get("name");
			
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.parent.getConnectionStringBuilder().getHost());
			DefaultTreeModel treeModel = new DefaultTreeModel(root);
        
			this.tree = new JTree(treeModel);
			
			tree.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseReleased(MouseEvent e) {
	                if (SwingUtilities.isRightMouseButton(e)) {
	                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
	                    tree.setSelectionRow(row);

	                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

	                    if (selectedNode == null) {
	                    	return;
	                    }
	                    
	                    int level = selectedNode.getLevel();

	                    popupMenu.removeAll();

	                    if (level == ROOT_LEVEL) {
	                    	PopupMenuItems.fillRootPopupMenu(popupMenu, parent);
	                    } else if (level == SECTION_LEVEL) {
	                    	if (((DefaultMutableTreeNode) selectedNode).getUserObject().toString().equals("Usuarios"))  {
	                    		PopupMenuItems.fillUsersSectionPopupMenu(popupMenu,parent);
	                    	} else {
	                    		PopupMenuItems.fillDatabaseSectionPopupMenu(popupMenu,parent);
	                    	}
	                    } else if (level == DATABASE_LEVEL && !((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject().toString().equals("Usuarios")) {
	                    	PopupMenuItems.fillDatabasePopupMenu(popupMenu, parent, selectedNode.getUserObject().toString());
	                    } else if (level == DATABASE_LEVEL) {
	                    	PopupMenuItems.fillUsersPopupMenu(popupMenu,parent,selectedNode.getUserObject().toString());
	                    } else if (level == TABLES_LEVEL) {
	                    	DefaultMutableTreeNode parentNode = selectedNode;
	                    	while (parentNode.getLevel() > DATABASE_LEVEL) {
	                    		parentNode = (DefaultMutableTreeNode) parentNode.getParent();
	                    	}
	               
	                    	String parentText = parentNode.getUserObject().toString();
	                    	PopupMenuItems.fillTablesPopupMenu(popupMenu, parent, selectedNode.getUserObject().toString(), parentText);
	                    }

	                    popupMenu.show(tree, e.getX(), e.getY());
	                    
	                }
	            }
	        });
			
			
			this.tree.addTreeWillExpandListener(new TreeWillExpandListener() {
				public void treeWillCollapse(TreeExpansionEvent event) {}
				
				public void treeWillExpand(TreeExpansionEvent event) {
					DefaultMutableTreeNode expandedNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();					
					if (expandedNode.getChildCount() != 0 || expandedNode.getLevel() == COLUMNS_LEVEL) {
						return;
					}
					
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) expandedNode;
	                while (parentNode.getLevel() > DATABASE_LEVEL) {
	                	parentNode = (DefaultMutableTreeNode) parentNode.getParent();
	                }
	               
	                String parentText = parentNode.getUserObject().toString();
					String conStr = parent.getConnectionStringBuilder()
							.withDbName(parentText)
							.build();
					
					try (var operation = new SQLOperation(conStr)) {
						parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						if (expandedNode.getLevel() == DATABASE_LEVEL) {
							var result = operation.executeRaw(DefaultQuerys.getTablesQuery);
							var tables = result.getTable().get("name");
						
							for (Object table : tables) {
								DefaultMutableTreeNode child = new NeverLeafNode(table.toString());
								expandedNode.add(child);
							}
						} else if (expandedNode.getLevel() == TABLES_LEVEL) {
							var information = expandedNode.getUserObject()
									.toString()
									.split("\\.");
						
							
							var columnsNode = new NeverLeafNode("Columnas");
							expandedNode.add(columnsNode);
							var result = operation.executeRaw(String.format(DefaultQuerys.getColumnsQuery, information[0], information[1]));
							var columns = result.getTable().get("name");

						
							for (Object column : columns) {
								DefaultMutableTreeNode child = new DefaultMutableTreeNode(column.toString());
								columnsNode.add(child);
							}
							
						
							var constraitsNode = new NeverLeafNode("Constraits");
							expandedNode.add(constraitsNode);
							result = operation.executeRaw(String.format(DefaultQuerys.getConstraitsQuery, information[0], information[1]));
							var constraits = result.getTable().get("name");
						
							for (Object constrait : constraits) {
								DefaultMutableTreeNode child = new DefaultMutableTreeNode(constrait.toString());
								constraitsNode.add(child);
							}
							
							
							var triggersNode = new NeverLeafNode("Triggers");
							expandedNode.add(triggersNode);
							result = operation.executeRaw(String.format(DefaultQuerys.getTriggersQuery, information[1]));
							var triggers = result.getTable().get("name");
						
							for (Object trigger : triggers) {
								DefaultMutableTreeNode child = new DefaultMutableTreeNode(trigger.toString());
								triggersNode.add(child);
							}

							
							var indexesNode = new NeverLeafNode("Índices");
							expandedNode.add(indexesNode);
							result = operation.executeRaw(String.format(DefaultQuerys.getIndexesQuery, information[1]));
							var indexes = result.getTable().get("name");
						
							for (Object index : indexes) {
								DefaultMutableTreeNode child = new DefaultMutableTreeNode(index.toString());
								indexesNode.add(child);
							}
							
							var partitionsNode = new NeverLeafNode("Particiones");
							expandedNode.add(partitionsNode);
							result = operation.executeRaw(String.format(DefaultQuerys.getPartitionsQuery, information[1]));
							var partitions = result.getTable().get("name");
						
							for (Object partition : partitions) {
								DefaultMutableTreeNode child = new DefaultMutableTreeNode(partition.toString());
								partitionsNode.add(child);
							}
							
						}
						
						
					} catch(Exception e) {
						parent.getResultReader().loadResult(ResultFactory.fromException(e));
					} finally {
						parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					
					tree.revalidate();
				}
			});
			this.scrollPane.setViewportView(tree);
			
			var databaseNode = new NeverLeafNode("Bases de datos");
			root.add(databaseNode);
			
			for (Object database : databases) {
				DefaultMutableTreeNode child = new NeverLeafNode(database.toString());
				databaseNode.add(child);
			}
			
			var usersNode = new NeverLeafNode("Usuarios");
			
			root.add(usersNode);
			result = operation.executeRaw(DefaultQuerys.getUsersQuery);
			var users = result.getTable().get("name");
						
			for (Object user : users) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(user.toString());
				usersNode.add(child);
			}
			
		} catch(Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}	


	private class NeverLeafNode extends DefaultMutableTreeNode {
		public NeverLeafNode(String content) {
			super(content);
		}
		@Override
		public boolean isLeaf() {
			return false; // El nodo siempre será tratado como padre 
		}
	}
	
}
