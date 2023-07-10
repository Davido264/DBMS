package app.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.util.function.Supplier;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import app.lib.connector.*;
import app.lib.queryBuilders.DefaultQuerys;
import app.lib.result.ResultFactory;
import app.lib.result.Status;
import javax.swing.table.*;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.UUID;

public class PartitionWizard extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblNewLabel;
	JComboBox comboBox_1;
	private String schemaName;
	private String tableName;
	private String database;
	private JTable table;
	private ConnectionStringBuilder constr;
	private Main parent;
	private Object[] databases;
	private Object[] columns;
	private Object[] columnsWithInfo;
	private Supplier<Boolean> execute;

	/**
	 * Create the dialog.
	 */
	public PartitionWizard(String schemaName, String tableName, String database, Main parent,
			ConnectionStringBuilder constr) {
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.constr = constr;
		this.parent = parent;
		this.database = database;
		setBounds(100, 100, 507, 446);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		{
			lblNewLabel = new JLabel("Fragmentar Tabla");
		}

		JLabel tableNameLabel = new JLabel(String.format("[%s]", tableName));
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		contentPanel.add(lblNewLabel);
		contentPanel.add(tableNameLabel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (PartitionWizard.this.execute != null) {
							PartitionWizard.this.execute.get();
							PartitionWizard.this.dispose();
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						PartitionWizard.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{

			JPanel panel = new JPanel();

			getContentPane().add(panel, BorderLayout.CENTER);
			JLabel lblNewLabel_1 = new JLabel("Tipo de Fragmentación");
			JComboBox comboBox = new JComboBox();
			comboBox.setModel(new DefaultComboBoxModel(FragmentationType.values()));

			JLabel lblNewLabel_2 = new JLabel("Parametros de Fragmentación");

			JScrollPane scrollPane = new JScrollPane();

			JLabel lblNewLabel_3 = new JLabel("Bases de Datos");

			this.comboBox_1 = new JComboBox();

			JButton btnNewButton = new JButton("Nuevo");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((DefaultTableModel) table.getModel()).addRow(new Object[] {});
				}
			});
			GroupLayout gl_panel = new GroupLayout(panel);
			gl_panel.setHorizontalGroup(
					gl_panel.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_panel.createSequentialGroup().addContainerGap()
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
											.addComponent(comboBox, 0, 471, Short.MAX_VALUE).addComponent(lblNewLabel_1)
											.addGroup(Alignment.TRAILING,
													gl_panel.createSequentialGroup().addComponent(lblNewLabel_2)
															.addPreferredGap(ComponentPlacement.RELATED, 264,
																	Short.MAX_VALUE)
															.addComponent(btnNewButton))
											.addComponent(comboBox_1, 0, 471, Short.MAX_VALUE)
											.addComponent(lblNewLabel_3)
											.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
									.addContainerGap()));
			gl_panel.setVerticalGroup(
					gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(
									gl_panel.createSequentialGroup().addContainerGap().addComponent(lblNewLabel_1)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(comboBox, GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
													.addComponent(btnNewButton).addComponent(lblNewLabel_2))
											.addGap(12)
											.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 163,
													GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblNewLabel_3)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(33)));

			table = new JTable();
			table.getTableHeader().setReorderingAllowed(false);
			scrollPane.setViewportView(table);
			panel.setLayout(gl_panel);

			fetchInformation();
			comboBox_1.setModel(new DefaultComboBoxModel(this.databases == null ? new Object[] {} : this.databases));
			setTableModel(FragmentationType.HORIZONTAL);
		}
	}

	private void setTableModel(FragmentationType f) {
		if (f.equals(FragmentationType.HORIZONTAL)) {
			setHorizontalModel();
		} else if (f.equals(FragmentationType.VERTICAL)) {

		} else {

		}
	}

	private void setHorizontalModel() {
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		model.addColumn("Columna");
		model.addColumn("Condición");

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

		JTextField textField = (JTextField) cellEditor.getTableCellEditorComponent(table, null, true, 0, 1);

		((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				// restringir ; y palabras clave de SQL
				if (text != null && text.matches("[^;]")) {
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});

		JComboBox comboBox = new JComboBox(this.columns == null ? new Object[] {} : this.columns);
		comboBox.setEditor(editor);
		TableColumn column2 = table.getColumnModel().getColumn(0);
		column2.setCellEditor(new DefaultCellEditor(comboBox));

		this.execute = () -> {
			try (SQLOperation sqlOp = new SQLOperation(this.constr.build())) {
				this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				DefaultTableModel tmodel = (DefaultTableModel) this.table.getModel();
				int rows = tmodel.getRowCount();

				if (rows == 0) {
					return true;
				}

				StringBuilder sb = new StringBuilder();
				String[] newTables = new String[tmodel.getRowCount()];

				String altDB = this.comboBox_1.getSelectedItem().toString();

				if (!altDB.equals(this.database)) {
					sb.append(String.format("USE [%s];\n", altDB));
				}
				for (int i = 0; i < rows; i++) {
					String newTableName = String.format("%s_%s", this.tableName,
							UUID.randomUUID().toString().replace('-', '_'));
					sb.append(String.format("SELECT * INTO [%s].[%s] FROM [%s].[%s].[%s]\n", this.schemaName,
							newTableName, this.database, this.schemaName, this.tableName));
					sb.append(String.format("WHERE %s %s;\n", tmodel.getValueAt(i, 0), tmodel.getValueAt(i, 1)));
					newTables[i] = newTableName;
				}

				String query = sb.toString();
				if (this.parent.getSettings().imprimirComandos) {
					System.out.println(query);
				}

				var result = sqlOp.executeRaw(query);

				if (result.getStatus().equals(Status.FAILURE)) {
					this.parent.getResultReader().loadResult(result);
					return true;
				}

				sb.setLength(0);

				if (!altDB.equals(this.database)) {
					sb.append(String.format("USE [%s];\n", this.database));

					query = sb.toString();
					if (this.parent.getSettings().imprimirComandos) {
						System.out.println(query);
					}
					
					result = sqlOp.executeRaw(query);

					if (result.getStatus().equals(Status.FAILURE)) {
						this.parent.getResultReader().loadResult(result);
						return true;
					}
					sb.setLength(0);
				}

				sb.append(String.format("CREATE VIEW [%s].[%s_view_frag] AS\n", this.schemaName,
						this.tableName));
				
				String[] cols = new String[this.columns.length];
				for (int i = 0; i < cols.length; i++) {
					cols[i] = String.format("[%s]", this.columns[i]);
				}
				String allColumns = String.join(",\n\t",cols);
				
				for (int i = 0; i < newTables.length; i++) {
					sb.append(String.format("SELECT \n\t%s \nFROM [%s].[%s].[%s]", allColumns, altDB, this.schemaName, newTables[i]));
					if (i == newTables.length - 1) {
						continue;
					}
					sb.append("\nUNION ALL\n");
				}

				sb.append(";\n");
				//sb.append(String.format("REVOKE INSERT, UPDATE, DELETE ON [%s].[%s_view_frag] TO PUBLIC;\n\n",
				//		this.schemaName, this.tableName));

				query = sb.toString();
				if (this.parent.getSettings().imprimirComandos) {
					System.out.println(query);
				}

				result = sqlOp.executeRaw(query);

				if (result.getStatus().equals(Status.FAILURE)) {
					this.parent.getResultReader().loadResult(result);
					return true;
				}
				sb.setLength(0);

				sb.append(String.format("CREATE OR ALTER TRIGGER [%s].[%s_trg_frag_insert]\n", this.schemaName,
						this.tableName));
				sb.append(String.format("ON [%s]\n", this.tableName));
				sb.append("AFTER INSERT\n");
				sb.append("AS BEGIN\n");
				for (int i = 0; i < rows; i++) {
					String newTableName = newTables[i];
					sb.append(String.format("INSERT INTO [%s].[%s].[%s]\n", altDB, this.schemaName, newTableName));
					sb.append("SELECT * FROM INSERTED\n");
					sb.append(String.format("WHERE %s %s;\n\n", tmodel.getValueAt(i, 0), tmodel.getValueAt(i, 1)));
				}
				sb.append("END;\n\n");

				query = sb.toString();
				if (this.parent.getSettings().imprimirComandos) {
					System.out.println(query);
				}

				result = sqlOp.executeRaw(query);

				if (result.getStatus().equals(Status.FAILURE)) {
					this.parent.getResultReader().loadResult(result);
					return true;
				}
				sb.setLength(0);

				sb.append(String.format("CREATE OR ALTER TRIGGER [%s].[%s_trg_frag_delete]\n", this.schemaName,
						this.tableName));
				sb.append(String.format("ON [%s]\n", this.tableName));
				sb.append("AFTER DELETE\n");
				sb.append("AS BEGIN\n");

				String[] params = new String[this.columns.length];

				for (int i = 0; i < params.length; i++) {
					params[i] = String.format("[s].[%s] = [i].[%s]", this.columns[i], this.columns[i]);
				}
				String predicate = String.join("\nAND ", params);

				for (String newTable : newTables) {
					sb.append(String.format("DELETE FROM [%s].[%s].[%s] s INNER JOIN DELETED i \nON %s;\n\n", altDB,
							this.schemaName, newTable, predicate));
				}
				sb.append("END;\n\n");

				query = sb.toString();
				if (this.parent.getSettings().imprimirComandos) {
					System.out.println(query);
				}

				sb.setLength(0);

				if (result.getStatus().equals(Status.FAILURE)) {
					this.parent.getResultReader().loadResult(result);
					return true;
				}
				sb = new StringBuilder();

				sb.append(String.format("CREATE OR ALTER TRIGGER [%s].[%s_trg_frag_update]\n", this.schemaName,
						this.tableName));
				sb.append(String.format("ON [%s]\n", this.tableName));
				sb.append("AFTER UPDATE\n");
				sb.append("AS BEGIN\n");

				for (int i = 0; i < params.length; i++) {
					params[i] = String.format("[s].[%s] = [i].[%s]", this.columns[i], this.columns[i]);
				}
				String changes = String.join(",\n", params);

				for (String newTable : newTables) {
					sb.append(String.format("UPDATE s SET %s \nFROM [%s].[%s].[%s] s\nINNER JOIN INSERTED i \nON %s;\n\n",
							changes, altDB, this.schemaName, newTable, predicate));
				}
				sb.append("END;\n\n");

				query = sb.toString();
				if (this.parent.getSettings().imprimirComandos) {
					System.out.println(query);
				}

				result = sqlOp.executeRaw(query);

				if (result.getStatus().equals(Status.FAILURE)) {
					this.parent.getResultReader().loadResult(result);
					return true;
				}
				return true;

			} catch (Exception e) {
				this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
				return false;
			} finally {
				this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		};

	}

	private void fetchInformation() {
		try (SQLOperation sqlOperation = new SQLOperation(constr.build())) {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			if (this.parent.getSettings().imprimirComandos) {
				System.out.println(String.format(DefaultQuerys.getColumnNamesQuery, this.schemaName, this.tableName));
			}

			var result = sqlOperation
					.executeRaw(String.format(DefaultQuerys.getColumnNamesQuery, this.schemaName, this.tableName));

			if (result.getStatus().equals(Status.FAILURE)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}

			this.columns = result.getTable().get("name").toArray();

			if (this.parent.getSettings().imprimirComandos) {
				System.out.println(String.format(DefaultQuerys.getColumnsQuery, this.schemaName, this.tableName));
			}

			result = sqlOperation
					.executeRaw(String.format(DefaultQuerys.getColumnsQuery, this.schemaName, this.tableName));

			if (result.getStatus().equals(Status.FAILURE)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}

			this.columnsWithInfo = result.getTable().get("name").toArray();

			if (this.parent.getSettings().imprimirComandos) {
				System.out.println(DefaultQuerys.getDatabasesQuery);
			}

			result = sqlOperation.executeRaw(DefaultQuerys.getDatabasesQuery);

			if (result.getStatus().equals(Status.FAILURE)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}

			this.databases = result.getTable().get("name").toArray();

		} catch (Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private enum FragmentationType {
		HORIZONTAL, VERTICAL, MIXTA
	}
}
