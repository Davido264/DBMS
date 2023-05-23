package app.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import app.lib.connector.*;
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
	private JButton btnNewButton_2;
	private JList tableList;
	private Editor editor;
	private String connectionString; 
	
	// localhost:1433  master sa  PasswordO1
	
	private final String getTablesQuery = "SELECT name AS _table FROM sys.tables WHERE type = 'U' AND name NOT LIKE 'sys%' AND name NOT LIKE 'dt%' AND name NOT LIKE 'spt_%' AND name NOT LIKE 'MSreplication_options';";
	private JSplitPane splitPane;
	private ResultReader resultReader;

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
		frame.setBounds(100, 100, 816, 637);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.WEST);
		
		btnNewButton_1 = new JButton("Conectar");
		
		btnNewButton_2 = new JButton("Nueva tabla");
		
		JButton btnNewButton = new JButton("Recargar la lista");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fillList();
			}
		});
		
		tableList = new JList();
		tableList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnNewButton_1, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
								.addComponent(btnNewButton_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
								.addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)))
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(tableList, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tableList, GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
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
		splitPane.setRightComponent(resultReader);
		
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) tableList.getCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		this.connect();
	}

	public void loadResults(String[] commands) {
		for (String command : commands) {
			command = command
					.strip();
			
			try (var operation = new SQLOperation(this.connectionString)) {
				var result = operation.executeRaw(command);
				this.resultReader.loadResult(result);
				this.splitPane.revalidate();
			} catch(Exception e) {
				e.printStackTrace();
			}
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
        	return;
        }
        String result = dialog.getConnectionString();
        System.out.println(result);
        this.connectionString = result;
        this.fillList();
	}
}
