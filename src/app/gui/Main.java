package app.gui;

import java.awt.EventQueue;


import javax.swing.JFrame;

import app.lib.connector.*;
import app.lib.result.ResultFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSplitPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

public class Main {

	private JFrame frame;
	private JPanel panel;
	private JButton btnNewButton_1;
	private ConnectionStringBuilder conStrGenerator; 
	private JSplitPane splitPane;
	private ResultReader resultReader;
	private JLabel dbNameLabel;
	private Tabs tabs;
	private TreeView treeView;

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
		
		JButton btnNewButton = new JButton("Refrescar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				treeView.loadDatabaseObjects();
			}
		});
		this.treeView = new TreeView(this);
		
		JLabel lblNewLabel = new JLabel("Base de datos:");
		
		this.dbNameLabel = new JLabel("NULL");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(treeView, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addGap(10)
							.addComponent(dbNameLabel, GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(dbNameLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton_1)
						.addComponent(btnNewButton))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(treeView, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		panel.setLayout(gl_panel);
		
		this.tabs = new Tabs(this);
		
		resultReader = new ResultReader();
		
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(tabs);
		
		splitPane.setRightComponent(resultReader);
		splitPane.setDividerLocation(0.9);
		
		mainPane.setLeftComponent(panel);
		mainPane.setRightComponent(splitPane);
		frame.getContentPane().add(mainPane);
		
		this.connect();
	}

	public void connect() {
		Connector dialog = new Connector(frame);
		dialog.setVisible(true);
        if (!dialog.isConfigured()) {
        	this.resultReader.loadResult(ResultFactory.fromString("Conexión cancelada"));
        	return;
        }
        this.conStrGenerator = dialog.getConnectionStringBuilder();
        this.treeView.loadDatabaseObjects();
	}
	
	
	public void setDbName(String dbName) {
		this.dbNameLabel.setText(dbName);
	    conStrGenerator = conStrGenerator.withDbName(dbName);
	}
	
	public Tabs getTabs() {
		return this.tabs;
	}

	public TreeView getTreeView() {
		return this.treeView;
	}

	public ResultReader getResultReader() {
		return this.resultReader;
	}
	
	public JFrame getFrame() {
		return this.frame;
	}
	
	public ConnectionStringBuilder getConnectionStringBuilder() {
		return this.conStrGenerator;
	}
}
