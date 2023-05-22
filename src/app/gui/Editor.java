package app.gui;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JEditorPane;

import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Editor extends JPanel {

	private JEditorPane editorPane;
	private Main parent;
	
	/**
	 * Create the panel.
	 */
	public Editor(Main parent) {
		this.parent = parent;
		this.editorPane = new JEditorPane();
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(editorPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(toolBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(editorPane, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JButton btnNewButton = new JButton("Ejecutar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.loadResults(getCommands());
			}
		});
		toolBar.add(btnNewButton);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);
		
		JLabel lblNewLabel = new JLabel("Editor de Scripts SQL");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		toolBar.add(lblNewLabel);
		setLayout(groupLayout);

	}

	private String[] getCommands() {
		return this.editorPane
				.getText()
				.strip()
				.split(";");
	}
	
}
