package app.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import app.lib.connector.*;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JList;
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

public class Main {

	private JFrame frame;
	private ResultReader resultReader;
	private JPanel jpanel;
	private JPanel panel;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private Editor editor;
	private String connectionString;

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
		
		JButton btnNewButton = new JButton("Modificar tabla");
		
		JButton btnNewButton_3 = new JButton("Eliminar Tabla");
		
		JList list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNewButton_1, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
						.addComponent(btnNewButton_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
						.addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
						.addComponent(btnNewButton_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
					.addContainerGap())
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(185)
					.addComponent(list)
					.addGap(0, 0, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(5)
					.addComponent(btnNewButton_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_3)
					.addPreferredGap(ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
					.addComponent(list)
					.addGap(450))
		);
		panel.setLayout(gl_panel);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		this.editor = new Editor(this);
		tabbedPane.addTab("Editor SQL", null, editor, null);
		
		this.jpanel = new JPanel();
		this.resultReader = new ResultReader();
		this.jpanel.add(resultReader);
		frame.getContentPane().add(this.jpanel, BorderLayout.SOUTH);
	}

	public void loadResults(String[] commands) {
		var connectionString = new ConnectionStringBuilder()
				.withHost("localhost")
				.withEncrypt(true)
				.withPort(1433)
				.withDbName("master")
				.withUserName("sa")
				.withPassword("PasswordO1")
				.withTrustServerCertificates(true)
				.build();
	
// SELECT name FROM sys.tables WHERE type = 'U' AND name NOT LIKE 'sys%' AND name NOT LIKE 'dt%' AND name NOT LIKE 'spt_%' AND name NOT LIKE 'MSreplication_options';
		
		for (String command : commands) {
			command = command
					.strip();
			
			try (var operation = new SQLOperation(connectionString)) {
				var result = operation.executeRaw(command);
				this.resultReader.loadResult(result);
				this.jpanel.revalidate();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
