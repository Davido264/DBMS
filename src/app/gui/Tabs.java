package app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import app.lib.connector.ConnectionStringBuilder;

import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class Tabs extends JPanel {
	private JTabbedPane tabbedPane;
	private Main parent;
	private int editorTabsCount;
	private int tableTabsCount;

	/**
	 * Create the panel.
	 */
	public Tabs(Main parent) {
		this.parent = parent;
		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP) {
		    @Override
		    public void addTab(String title, final Component component) {
		        JPanel tabPanel = new JPanel(new BorderLayout());
		        tabPanel.setOpaque(false);

		        JLabel titleLabel = new JLabel(title);
		        JButton closeButton = new JButton("x");
		        closeButton.setMargin(new Insets(0, 0, 0, 0));
		        closeButton.setFocusable(false);
		        closeButton.setBorderPainted(false);
		        closeButton.setContentAreaFilled(false);
		        closeButton.setOpaque(true);
		        closeButton.setBackground(Color.WHITE);

		        closeButton.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		                int tabIndex = indexOfComponent(component);
		                if (tabIndex == -1) {
		                	return;
		                }

		                Component component = tabbedPane.getComponentAt(tabIndex);
		                if (component instanceof Editor) {
		                	System.out.println("foo");
		                	editorTabsCount--;
		                }
        	
		                if (component instanceof TableProperties) {
		                	System.out.println("bar");
		                	tableTabsCount--;
		                }
		                	
		                removeTabAt(tabIndex);
		            }
		        });

		        JPanel tabTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		        tabTitlePanel.add(titleLabel);
		        tabTitlePanel.add(closeButton);

		        tabPanel.add(tabTitlePanel, BorderLayout.CENTER);

		        super.addTab(null, component);
		        setTabComponentAt(getTabCount() - 1, tabPanel);
		    }
		};
		this.tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
		);
		setLayout(groupLayout);
	}

	
	
	public void createNewEditorTab(ConnectionStringBuilder c) {
		Editor editor = new Editor(this.parent,c);
		String title = "Editor SQL" + (this.editorTabsCount == 0 ? "" : String.format("(%d)", this.editorTabsCount));
		this.tabbedPane.addTab(title, new JScrollPane(editor));
		this.editorTabsCount++;
	}

	public void createNewEditorTab(String initialText,ConnectionStringBuilder c) {
		Editor editor = new Editor(this.parent,initialText,c);
		String title = "Editor SQL" + (this.editorTabsCount == 0 ? "" : String.format("(%d)", this.editorTabsCount));
		this.tabbedPane.addTab(title, new JScrollPane(editor));
		this.editorTabsCount++;
	}
	
	
	public void createNewTablePropertiesTab(boolean edit,ConnectionStringBuilder c) {
		TableProperties tableProperties = new TableProperties(this.parent,edit,c);
		String title = "Editor de Tablas" + (this.tableTabsCount == 0 ? "" : String.format("(%d)", this.tableTabsCount));
		tabbedPane.addTab(title, new JScrollPane(tableProperties));
		this.tableTabsCount++;
	}
	
}
