package app.gui;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.JCheckBox;

public class UserEditor extends JPanel {
	private JTextField textField;
	private JPasswordField passwordField;

	/**
	 * Create the panel.
	 */
	public UserEditor() {

		textField = new JTextField();
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Nombre de Usuario");

		JLabel lblNewLabel_1 = new JLabel("Contraseña");

		passwordField = new JPasswordField();

		JTable table = new JTable();

		JScrollPane scrollPane = new JScrollPane(table);

		JButton btnNewButton = new JButton("Guardar");

		JButton btnNewButton_1 = new JButton("Modificar");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addContainerGap()
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
												.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(lblNewLabel).addComponent(lblNewLabel_1))
												.addPreferredGap(ComponentPlacement.RELATED, 245, Short.MAX_VALUE))
										.addGroup(groupLayout.createSequentialGroup()
												.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
														.addComponent(textField, Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
														.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 237,
																Short.MAX_VALUE))
												.addGap(109)))
								.addGap(5)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(btnNewButton_1, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE).addGap(3)))
				.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(16).addComponent(lblNewLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNewButton))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel_1)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(passwordField,
												GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE))
								.addComponent(btnNewButton_1))
						.addPreferredGap(ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		setLayout(groupLayout);

		Object[] columnNames = new Object[] { "Tabla", "SELECT", "INSERT", "DELETE", "UPDATE" };

		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return String.class; // Columna 1: String
				} else {
					return Boolean.class; // JCheckBox
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}
		};

		table.setModel(model);

		for (int i = 1; i <= 4; i++) {
			TableColumn column4 = table.getColumnModel().getColumn(i);
			column4.setCellRenderer(table.getDefaultRenderer(Boolean.class));
			column4.setCellEditor(table.getDefaultEditor(Boolean.class));
		}

		table.getTableHeader().setReorderingAllowed(false);

	}
}
