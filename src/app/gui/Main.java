package app.gui;

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JToolBar;

import app.lib.connector.*;
import app.lib.queryBuilders.Drop;
import app.lib.queryBuilders.Select;
import app.lib.result.ResultFactory;
import app.lib.result.ResultType;
import app.lib.result.Status;
import javax.swing.tree.*;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JTabbedPane;
import javax.swing.JEditorPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

public class Main {

	private JFrame frame;
	private JPanel panel;
	private JButton btnNewButton_1;
	private Editor editor;
	private JScrollPane scrollPane;
	private ConnectionStringBuilder conStrGenerator; 
	private JSplitPane splitPane;
	private ResultReader resultReader;
	private JLabel dbNameLabel;
	private JTree tree;

	private final String getDatabasesQuery = "SELECT name FROM sys.databases WHERE database_id > 4;";
	private final String getTablesQuery = """
SELECT (s.name + '.' + t.name) AS name 
FROM sys.tables t 
INNER JOIN sys.schemas s ON t.schema_id = s.schema_id 
WHERE t.type = 'U' AND t.name NOT LIKE 'sys%' AND t.name NOT LIKE 'dt%' AND t.name NOT LIKE 'spt_%' AND t.name NOT LIKE 'MSreplication_options';		
			""";
	private JButton btnNewButton_2;
	private JButton btnNewButton_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 904, 637);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		JSplitPane mainPane = new JSplitPane();
		
		panel = new JPanel();
		
		btnNewButton_1 = new JButton("Conectar");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		
		JButton btnNewButton = new JButton("Recargar el arbol de objetos");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadDatabseObjects();
			}
		});
		this.scrollPane = new JScrollPane();
		
		btnNewButton_2 = new JButton("Visualizar tabla");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				var selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (selected == null) {
					JOptionPane.showMessageDialog(frame, "No se ha seleccionado ninguna tabla", "No se ha seleccionado ninguna tabla", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				Select generator = Select.all(selected.getUserObject().toString());
				loadResults(generator.generateQuery());
			}
		});
		
		btnNewButton_3 = new JButton("Eliminar tabla");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				var selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (selected == null) {
					JOptionPane.showMessageDialog(frame, "No se ha seleccionado ninguna tabla", "No se ha seleccionado ninguna tabla", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				Drop generator = new Drop(selected.getUserObject().toString());
				loadResults(generator.generateQuery());
				loadDatabseObjects();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Base de datos:");
		
		this.dbNameLabel = new JLabel("NULL");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnNewButton_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
						.addComponent(btnNewButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
						.addComponent(btnNewButton_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
						.addComponent(btnNewButton_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addGap(10)
							.addComponent(this.dbNameLabel, GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(this.dbNameLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		panel.setLayout(gl_panel);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		this.editor = new Editor(this);
		tabbedPane.addTab("Editor SQL", null, new JScrollPane(editor), null);

		resultReader = new ResultReader();
		
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(tabbedPane);
		
		tabbedPane.addTab("Editor de Tablas", null, new JScrollPane(new TableProperties(this,true)), null);
		splitPane.setRightComponent(resultReader);
		splitPane.setDividerLocation(0.9);
		
		mainPane.setLeftComponent(panel);
		mainPane.setRightComponent(splitPane);
		frame.getContentPane().add(mainPane);
		
		this.connect();
	}

	public void loadResults(String... commands) {
		try {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			for (String command : commands) {
				command = command
						.strip();
			
				try (var operation = new SQLOperation(this.conStrGenerator.build())) {
					var result = operation.executeRaw(command);
					this.resultReader.loadResult(result);
					this.splitPane.revalidate();
				}
			}
		} catch(Exception e) {
			this.resultReader.loadResult(ResultFactory.fromException(e));
		} finally {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	public void loadDatabseObjects() {
		try (var operation = new SQLOperation(this.conStrGenerator.build())) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			var result = operation.executeRaw(this.getDatabasesQuery);
			if (result.getStatus().equals(Status.FAILURE) || result.getType().equals(ResultType.STRING)) {
				this.resultReader.loadResult(result);
				this.splitPane.revalidate();
				return;
			}
			
			var databases = result.getTable().get("name");
			
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.conStrGenerator.getHost());
			DefaultTreeModel treeModel = new DefaultTreeModel(root);
        
			this.tree = new JTree(treeModel);
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
	                TreePath path = e.getPath();
	                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
	                
	                if (selectedNode.getLevel() == 0) {
	                	return;
	                }
	                
	                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
	                String parentText = (selectedNode.getLevel() == 1) ? selectedNode.getUserObject().toString() :
	                        (parentNode != null) ? parentNode.getUserObject().toString() : "";

	                dbNameLabel.setText(parentText);
	                conStrGenerator = conStrGenerator.withDbName(parentText);
				}
			});
			this.tree.addTreeWillExpandListener(new TreeWillExpandListener() {
				public void treeWillCollapse(TreeExpansionEvent event) {}
				public void treeWillExpand(TreeExpansionEvent event) {
					DefaultMutableTreeNode expandedNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();					
					if (expandedNode.getChildCount() != 0) {
						return;
					}
				
					String conStr = conStrGenerator.withDbName(expandedNode.getUserObject().toString()).build();
					try (var operation = new SQLOperation(conStr)) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						var result = operation.executeRaw(getTablesQuery);
						var tables = result.getTable().get("name");
						
						for (Object table : tables) {
							DefaultMutableTreeNode child = new DefaultMutableTreeNode(table.toString());
							expandedNode.add(child);
						}
						
						
					} catch(Exception e) {
						resultReader.loadResult(ResultFactory.fromException(e));
					} finally {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					
					tree.revalidate();
				}
			});
			this.scrollPane.setViewportView(tree);
			
			for (Object database : databases) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(database.toString()) {
					private static final long serialVersionUID = 1L;

					@Override
			        public boolean isLeaf() {
			            return false; // El nodo siempre será tratado como padre 
			        }
				};
				root.add(child);
			}
			this.dbNameLabel.setText(this.conStrGenerator.getDbName());
			
		} catch(Exception e) {
			this.resultReader.loadResult(ResultFactory.fromException(e));
		} finally {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}	
	
	public void connect() {
		Connector dialog = new Connector(frame);
		dialog.setVisible(true);
        if (!dialog.isConfigured()) {
        	this.resultReader.loadResult(ResultFactory.fromString("Conexión cancelada"));
        	return;
        }
        this.conStrGenerator = dialog.getConnectionStringBuilder();
        this.loadDatabseObjects();
	}
}
