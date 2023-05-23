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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
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

public class Main {

	private JFrame frame;
	private JPanel panel;
	private JButton btnNewButton_1;
	private JList tableList;
	private Editor editor;
	private String connectionString; 
	private JSplitPane splitPane;
	private ResultReader resultReader;
	private JPanel panel_1;
	
	private final String getTablesQuery = """
SELECT (s.name + '.' + t.name) AS _table 
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
		
		JButton btnNewButton = new JButton("Recargar la lista");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fillList();
			}
		});
		
		tableList = new JList();
		tableList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(tableList);
		
		btnNewButton_2 = new JButton("Visualizar tabla");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tableList.isSelectionEmpty()) {
					return;
				} 
				
				String selectedItem = tableList.getSelectedValue().toString();
				if (selectedItem != null) {
					Select generator = Select.all(selectedItem);
					loadResults(generator.generateQuery());
				}
			}
		});
		
		btnNewButton_3 = new JButton("Eliminar tabla");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tableList.isSelectionEmpty()) {
					return;
				} 
				
				String selectedItem = tableList.getSelectedValue().toString();
				if (selectedItem != null) {
					Drop generator = new Drop(selectedItem);
					loadResults(generator.generateQuery());
				}
				
				fillList();
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNewButton_1, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
						.addComponent(btnNewButton_2, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
						.addComponent(btnNewButton_3, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		this.editor = new Editor(this);
		tabbedPane.addTab("Editor SQL", null, editor, null);

		resultReader = new ResultReader();
		
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(tabbedPane);
		
		panel_1 = new JPanel();
		panel_1.add(new TableProperties(this,true));
		tabbedPane.addTab("Editor de Tablas", null, panel_1, null);
		splitPane.setRightComponent(resultReader);
		splitPane.setDividerLocation(0.9);
		
		
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) tableList.getCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.LEFT);
	
		
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
			
				try (var operation = new SQLOperation(this.connectionString)) {
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

	public void fillList() {
		try (var operation = new SQLOperation(this.connectionString)) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			var result = operation.executeRaw(this.getTablesQuery);
			if (result.getStatus().equals(Status.FAILURE) || result.getType().equals(ResultType.STRING)) {
				this.resultReader.loadResult(result);
				this.splitPane.revalidate();
				return;
			}
			
			var tables = result.getTable().get("_table");
			var listModel = new DefaultListModel<String>();
			tableList.removeAll();
			
			for (Object table : tables) {
				listModel.addElement(table.toString());
			}
		
			tableList.setModel(listModel);
			tableList.revalidate();
			
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
        String result = dialog.getConnectionString();
        this.connectionString = result;
        this.fillList();
	}
}
