package app.gui;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import app.lib.result.*;

public class ResultReader extends JPanel {
	private JTable table;
	private JTextArea textArea;
	
	
	public void loadResult(Result result) {
		this.removeAll();	
		if (result.getStatus().equals(Status.FAILURE)) {
			textArea = new JTextArea();
			textArea.setText(result.getReason());
			this.add(new JScrollPane(textArea));
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
        	this.add(new JScrollPane(table));
        	return;
        }

		textArea = new JTextArea();
        textArea.setText(result.getText());
		this.add(new JScrollPane(textArea));
		this.revalidate();
	}
	
}
