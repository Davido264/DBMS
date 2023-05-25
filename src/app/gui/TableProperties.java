package app.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.ComboBoxEditor;
import app.lib.queryBuilders.SQLServerTypes;
import app.lib.queryBuilders.Create;
import app.lib.queryBuilders.InlineConstraints;
import app.lib.queryBuilders.ColumnEntry;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class TableProperties extends JPanel {
	private JTable table;
	private boolean editable;
	private JTextField textField;
	private final Main parent;

	/**
	 * Create the panel.
	 */
	public TableProperties(Main parent, boolean editable) {
		this.parent = parent;
		this.editable = editable;
		table = new JTable();
		JScrollPane scroll = new JScrollPane(table);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setEditable(editable);
		
		JButton btnNewButton = new JButton("+");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.addRow(new Object[]{"Column", SQLServerTypes.INT, 0, false});
				table.revalidate();
			}
		});
		
		JButton btnNewButton_1 = new JButton("Guardar");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeCreateTable();
			}
		});
		
		JButton btnNewButton_2 = new JButton("-");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Obtener el índice de la fila seleccionada
                int selectedRow = table.getSelectedRow();

                // Verificar si se seleccionó una fila
                if (selectedRow != -1) {
                	DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.removeRow(selectedRow);
                    table.revalidate();
                }
			}
		});
		
		JLabel lblNewLabel = new JLabel("Nombre de la Tabla");
		
		JLabel lblNewLabel_1 = new JLabel("Columnas");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGap(360)
							.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 87, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(scroll, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel))
							.addPreferredGap(ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel_1)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnNewButton)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnNewButton_2)))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton_2)
						.addComponent(btnNewButton)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scroll, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_1)
					.addContainerGap())
		);
		setLayout(groupLayout);

		Object[] columnNames = new Object[] { "Nombre", "Tipo de dato", "Longitud", "Not null" };
		
			
        DefaultTableModel model = new DefaultTableModel(columnNames,0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return String.class; // Columna 1: String
                } else if (columnIndex == 1) {
                    return String.class; // Columna 2: JComboBox
                } else if (columnIndex == 2) {
                    return Integer.class; // Columna 3: JSlider 
                } else {
                    return Boolean.class; // Columna 4: JCheckBox
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };	

        this.table.setModel(model);
        
        SQLServerTypes[] options = SQLServerTypes.values();
        TableColumn column2 = table.getColumnModel().getColumn(1);
      
        // Crear un ComboBoxEditor personalizado
        ComboBoxEditor editor = new ComboBoxEditor() {
            private JTextField editorComponent;

            @Override
            public Component getEditorComponent() {
                if (editorComponent == null) {
                    editorComponent = new JTextField();
                    editorComponent.setEditable(false);
                }
                return editorComponent;
            }

            @Override
            public void setItem(Object item) {
                if (editorComponent != null) {
                    editorComponent.setText(item.toString());
                }
            }

            @Override
            public Object getItem() {
                if (editorComponent != null) {
                    return editorComponent.getText();
                }
                return null;
            }

            @Override
            public void selectAll() {
                if (editorComponent != null) {
                    editorComponent.selectAll();
                }
            }

            @Override
            public void addActionListener(ActionListener l) {
                if (editorComponent != null) {
                    editorComponent.addActionListener(l);
                }
            }

            @Override
            public void removeActionListener(ActionListener l) {
                if (editorComponent != null) {
                    editorComponent.removeActionListener(l);
                }
            }
        };
       
        TableCellEditor cellEditor = table.getDefaultEditor(Object.class);

        JTextField textField = (JTextField) cellEditor.getTableCellEditorComponent(table, null, true, 0, 0);

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                // restringir ' ; \ y / 
                if (text != null && text.matches("[^';\\\\\\/]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        }); 
        
        JComboBox comboBox = new JComboBox(options);
        comboBox.setEditor(editor);
        
        column2.setCellEditor(new DefaultCellEditor(comboBox));

        TableColumn column3 = table.getColumnModel().getColumn(2);
        column3.setCellEditor(new SpinnerCellEditor());
         
        TableColumn column4 = table.getColumnModel().getColumn(3);
        column4.setCellRenderer(table.getDefaultRenderer(Boolean.class));
        column4.setCellEditor(table.getDefaultEditor(Boolean.class)); 
       
        this.table.getTableHeader().setReorderingAllowed(false);
	}
	

	ColumnEntry[] getColumnsFromEditor() {
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		int rowCount = model.getRowCount();
		ColumnEntry[] entries = new ColumnEntry[rowCount];

		for (int i = 0; i < rowCount; i++) {
			if ((Boolean) model.getValueAt(i, 3)) {
				entries[i] = new ColumnEntry(
						model.getValueAt(i, 0).toString(),
						((SQLServerTypes) model.getValueAt(i, 1)).withLengt((int)model.getValueAt(i, 2)),
						InlineConstraints.NOT_NULL
				);
				
				continue;
			}

			entries[i] = new ColumnEntry(
					model.getValueAt(i, 0).toString(),
					((SQLServerTypes) model.getValueAt(i, 1)).withLengt((int)model.getValueAt(i, 2))
			);
		}
		
		return entries;
	}
	
	
	public void executeCreateTable() {
		Create generator = new Create(this.textField.getText(),this.getColumnsFromEditor());
		String command = generator.generateQuery();
		this.parent.loadResults(command);
		this.parent.loadDatabseObjects();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);
		this.textField.setText("");
		this.textField.revalidate();
		this.table.revalidate();
	}
	
	
	
	
	
	// TableCellEditor personalizado con JSlider para números
	class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {
	    private JSpinner spinner;

	    public SpinnerCellEditor() {
	        spinner = new JSpinner();
	        spinner.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
	    }

	    @Override
	    public Object getCellEditorValue() {
	        return spinner.getValue();
	    }

	    @Override
	    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
	                                                          boolean isSelected, int row, int column) {
	        spinner.setValue((int) value);
	        return spinner;
	    }
	}
}
