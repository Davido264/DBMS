package app.gui;

import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import app.lib.result.ResultFactory;
import app.lib.connector.SQLOperation;
import app.lib.connector.ConnectionStringBuilder;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JEditorPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Editor extends JPanel {

	private JEditorPane editorPane;
	private Main parent;
	private JLabel contextDisplayer;
	private ConnectionStringBuilder conStrGenerator;
	
	public Editor(Main parent, String initialText, ConnectionStringBuilder conStrGenerator) {
		this(parent,conStrGenerator);
		this.editorPane.setText(initialText);
	}
	
	
	/**
	 * Create the panel.
	 */
	/**
	 * @wbp.parser.constructor
	 */
	public Editor(Main parent,ConnectionStringBuilder conStrGenerator) {
		this.parent = parent;
		this.editorPane = new JEditorPane();
		this.conStrGenerator = conStrGenerator;
	
		JScrollPane scrollPane = new JScrollPane(editorPane);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		this.contextDisplayer = new JLabel(String.format("%s\\%s", conStrGenerator.getHost(),conStrGenerator.getDbName()));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(this.contextDisplayer, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(toolBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(this.contextDisplayer)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JButton btnNewButton = new JButton("Ejecutar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					try (var operation = new SQLOperation(conStrGenerator.build())) {
						parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						var result = operation.executeRaw(getCommands());
						parent.getResultReader().loadResult(result);
					} catch(Exception ex) {
						parent.getResultReader().loadResult(ResultFactory.fromException(ex));
					} finally {
						parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
			}
		});
		toolBar.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Guardar");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = spawnFileChooser();
				if (path != null) {
					writeFile(path);
				}
			}
		});
		toolBar.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Cargar");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = spawnFileChooser();
				if (path != null) {
					readFile(path);
				}
			}
		});
		toolBar.add(btnNewButton_2);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);
		
		JLabel lblNewLabel = new JLabel("Editor de Scripts SQL");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		toolBar.add(lblNewLabel);
		setLayout(groupLayout);

	}

	private String getCommands() {
		return this.editorPane.getText();
	}
	
	private String spawnFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Script SQL", "sql");
		fileChooser.setFileFilter(filter);
	    int result = fileChooser.showOpenDialog(parent.getFrame());
	    if (result != JFileChooser.APPROVE_OPTION) {
	    	return null;
	    }
	    return fileChooser.getSelectedFile().getAbsolutePath();
	}
	
	private void readFile(String filePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            editorPane.setText(content.toString());
        } catch (IOException e) {
        	this.parent
        		.getResultReader()
        		.loadResult(ResultFactory.fromException(e));
        }
	}

	private void writeFile(String filePath) {
		try (FileWriter writer = new FileWriter(filePath)) {
            String content = editorPane.getText();
            writer.write(content);
            JOptionPane.showMessageDialog(this.parent.getFrame(), "Archivo guardado exitosamente");
        } catch (IOException e) {
        	this.parent
        		.getResultReader()
        		.loadResult(ResultFactory.fromException(e));
        }	
	}
	
}
