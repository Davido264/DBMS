package app.gui;

import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import app.lib.result.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout.Alignment;

public class ResultReader extends JPanel {
	private JTable table;
	private JTextArea textArea;
	private JScrollPane scrollPane;

	public ResultReader() {
		
		this.scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(this.scrollPane, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(this.scrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
		);
		setLayout(groupLayout);
	}
	
	public void loadResult(Result result) {
		if (result.getStatus().equals(Status.FAILURE)) {
			textArea = new JTextArea();
			textArea.setText(result.getReason());
			textArea.setEditable(false);
			this.scrollPane.setViewportView(textArea);
			this.scrollPane.revalidate();
			this.revalidate();
			return;
        }

        if (result.getType().equals(ResultType.TABLE)) {
        	var keys = result.getTable()
        			.keySet()
        			.toArray(new String[result.getTable().keySet().size()]);
        	
        	DefaultTableModel model = new DefaultTableModel(); 

        	for (String key : keys) {
        		model.addColumn(key);
        	}
     
        	var maxCount = result
        			.getTable()
        			.getOrDefault(keys[0], new ArrayList<Object>(0))
        			.size();
        	
        	for (int i = 0; i < maxCount; i++) {
        		var buffer = new Object[keys.length];
        		var curr = 0;
        		for (String key : keys) {
        			buffer[curr] = result.getTable()
        					.get(key)
        					.get(i);
        			curr++;
        		}
        		model.addRow(buffer);
        	}

			table = new JTable();
			table.setEnabled(false);	
			
        	table.setModel(model);
        	this.scrollPane.setViewportView(table);
			this.scrollPane.revalidate();
			this.revalidate();
        	return;
        }

		textArea = new JTextArea();
		textArea.setEditable(false);
        textArea.setText(result.getText());
		this.scrollPane.setViewportView(textArea);
		this.scrollPane.revalidate();
		this.revalidate();
	}
	
}
