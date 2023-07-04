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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.JTable;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import app.lib.connector.SQLOperation;
import app.lib.connector.ConnectionStringBuilder;
import app.lib.queryBuilders.AlterUser;
import app.lib.queryBuilders.AlterServerRole;
import app.lib.queryBuilders.DefaultQuerys;
import app.lib.queryBuilders.Login;
import app.lib.queryBuilders.User;
import app.lib.result.ResultFactory;
import app.lib.result.ResultType;
import app.lib.result.Status;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTabbedPane;

public class UserEditor extends JPanel {
	private JTextField textField;
	private JPasswordField passwordField;
	private Main parent;
	private JLabel loginName;
	private ConnectionStringBuilder conStrGenerator;
	private String user;
	private String password;
	private JComboBox dbcBox;
	private boolean editUsername;
	private boolean editPassword;
	private boolean modifyUser;
	private JTable serverRolesTable;
	private JPanel panel_1;
	private JTable dbRolesTable;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("serial")
	public UserEditor(Main parent, boolean modifyUser, String username, String password,
			ConnectionStringBuilder conStrGenerator) {
		this.user = username == null ? "" : username;
		this.password = password == null ? "" : password;
		this.conStrGenerator = conStrGenerator;
		this.parent = parent;
		this.textField = new JTextField();
		this.loginName = new JLabel(conStrGenerator.getUserName());
		this.modifyUser = modifyUser;

		JLabel lblNewLabel = new JLabel("Nombre de Usuario");
		JLabel lblNewLabel_1 = new JLabel("Contraseña");

		this.textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					UserEditor.this.textField.setText(UserEditor.this.user);
					UserEditor.this.editUsername = false;
					return;
				}
				UserEditor.this.editUsername = true;
			}
		});

		this.textField.setColumns(10);

		this.passwordField = new JPasswordField();

		this.passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					UserEditor.this.passwordField.setText(UserEditor.this.password);
					UserEditor.this.editPassword = false;
					return;
				}
				UserEditor.this.editPassword = true;
			}
		});

		JButton btnNewButton = new JButton("Guardar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (modifyUser) {
					executeAlterUser();
					return;
				}

				executeCreateUser();
			}
		});

		JLabel lblNewLabel_3 = new JLabel("Login:");

		this.dbcBox = this.createComboBox();
		dbcBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String newDB = UserEditor.this.dbcBox.getSelectedItem().toString();
				UserEditor.this.conStrGenerator.withDbName(newDB);
			}
		});
		this.dbcBox.setEnabled(!this.modifyUser);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
				.createSequentialGroup().addContainerGap()
				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(tabbedPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
												.addComponent(dbcBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(lblNewLabel_1)
												.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 209,
														Short.MAX_VALUE)
												.addComponent(textField))
										.addComponent(lblNewLabel))
								.addPreferredGap(ComponentPlacement.RELATED, 158, Short.MAX_VALUE)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel_3)
												.addPreferredGap(ComponentPlacement.RELATED).addComponent(loginName)
												.addGap(1))
										.addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 97,
												GroupLayout.PREFERRED_SIZE))))
				.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addContainerGap()
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_3)
								.addComponent(loginName))
						.addComponent(dbcBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addGap(34).addComponent(lblNewLabel).addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addGap(11).addComponent(lblNewLabel_1).addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton))
				.addGap(18).addComponent(tabbedPane).addContainerGap()));

		JPanel panel = new JPanel();
		tabbedPane.addTab("Roles de Servidor", null, panel, null);

		serverRolesTable = new JTable();
		Object[] columnNames = new Object[] { "Activo", "Rol" };

		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return Boolean.class; // Columna 1: CheckBox
				} else {
					return String.class; // Columna 2: String
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}

		};

		serverRolesTable.setModel(model);
		TableColumn column0 = serverRolesTable.getColumnModel().getColumn(0);
		column0.setCellRenderer(serverRolesTable.getDefaultRenderer(Boolean.class));
		column0.setCellEditor(serverRolesTable.getDefaultEditor(Boolean.class));

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(serverRolesTable,
				Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(serverRolesTable,
				GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE));
		panel.setLayout(gl_panel);

		panel_1 = new JPanel();
		tabbedPane.addTab("Roles de Tabla", null, panel_1, null);

		dbRolesTable = new JTable();

		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return Boolean.class; // Columna 1: CheckBox
				} else {
					return String.class; // Columna 2: String
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}

		};

		dbRolesTable.setModel(model);
		column0 = dbRolesTable.getColumnModel().getColumn(0);
		column0.setCellRenderer(dbRolesTable.getDefaultRenderer(Boolean.class));
		column0.setCellEditor(dbRolesTable.getDefaultEditor(Boolean.class));

		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addComponent(dbRolesTable,
				GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addComponent(dbRolesTable,
				Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE));
		panel_1.setLayout(gl_panel_1);

		setLayout(groupLayout);

		this.fillTableRole();

		((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				// restringir ' ; \ y /
				if (text != null && text.matches("[a-zA-Z0-9_]")) {
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});

	}

	private JComboBox createComboBox() {
		Object[] databases = null;
		try (var operation = new SQLOperation(this.parent.getConnectionStringBuilder().build())) {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			var result = operation.executeRaw(DefaultQuerys.getDatabasesQuery);
			if (result.getStatus().equals(Status.FAILURE) || result.getType().equals(ResultType.STRING)) {
				this.parent.getResultReader().loadResult(result);
				return new JComboBox();
			}

			databases = result.getTable().get("name").toArray();
			if (databases.length != 0) {
				this.conStrGenerator.withDbName(databases[0].toString());
			}

		} catch (Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		return new JComboBox(databases);
	}

	private void executeCreateUser() {
		try (var operation = new SQLOperation(this.conStrGenerator.build())) {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			String command = new Login(this.textField.getText(), new String(this.passwordField.getPassword()).strip())
					.generateQuery();
			var result = operation.executeRaw(command);
			this.parent.getResultReader().loadResult(result);

			command = new User(this.textField.getText(), this.textField.getText()).generateQuery();
			result = operation.executeRaw(command);
			this.parent.getResultReader().loadResult(result);

			DefaultTableModel model = (DefaultTableModel) serverRolesTable.getModel();
			int rowCount = model.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				boolean assigned = (Boolean) model.getValueAt(i, 0);
				String roleName = (String) model.getValueAt(i, 1);
				if (assigned) {
					command = new AlterServerRole(roleName, this.textField.getText()).generateQuery();
					result = operation.executeRaw(command);
					this.parent.getResultReader().loadResult(result);
				}
			}

			model = (DefaultTableModel) dbRolesTable.getModel();
			rowCount = model.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				boolean assigned = (Boolean) model.getValueAt(i, 0);
				String roleName = (String) model.getValueAt(i, 1);
				if (assigned) {
					result = operation.executeRaw(
							String.format("EXEC sp_addrolemember '%s', '%s';", roleName, this.textField.getText()));
					this.parent.getResultReader().loadResult(result);
				}
			}

			this.parent.getTreeView().loadDatabaseObjects();
		} catch (Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void executeAlterUser() {
		try (var operation = new SQLOperation(this.conStrGenerator.build())) {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (this.editUsername) {
				AlterUser generator = new AlterUser(this.loginName.getText(), AlterUser.Fields.NAME,
						this.textField.getText());
				String command = generator.generateQuery();
				var result = operation.executeRaw(command);
				this.parent.getResultReader().loadResult(result);
			}

			if (this.editPassword) {
				AlterUser generator = new AlterUser(this.loginName.getText(), AlterUser.Fields.PASSWORD,
						this.textField.getText());
				String command = generator.generateQuery();
				var result = operation.executeRaw(command);
				this.parent.getResultReader().loadResult(result);

			}

			DefaultTableModel model = (DefaultTableModel) dbRolesTable.getModel();
			int rowCount = model.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				boolean assigned = (Boolean) model.getValueAt(i, 0);
				String roleName = (String) model.getValueAt(i, 1);
				if (!assigned) {
					var result = operation
							.executeRaw(String.format("EXEC sp_droprolemember '%s', '%s';", roleName, this.user));
					this.parent.getResultReader().loadResult(result);
				} else {
					var result = operation
							.executeRaw(String.format("EXEC sp_addrolemember '%s', '%s';", roleName, this.user));
					this.parent.getResultReader().loadResult(result);
				}
			}

			this.parent.getTreeView().loadDatabaseObjects();
		} catch (Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	private void fillTableRole() {
		try (var operation = new SQLOperation(this.conStrGenerator.build())) {
			parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTableModel model = (DefaultTableModel) this.serverRolesTable.getModel();
			var result = operation.executeRaw(DefaultQuerys.getRolesQuery);
			if (result.getStatus().equals(Status.FAILURE)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}

			var names = result.getTable().get("name");

			for (int i = 0; i < names.size(); i++) {
				model.addRow(new Object[] { false, names.get(i) });
			}

			model = (DefaultTableModel) this.dbRolesTable.getModel();
			result = operation.executeRaw(DefaultQuerys.getDBRolesQuery);
			if (result.getStatus().equals(Status.FAILURE)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}

			names = result.getTable().get("name");

			for (int i = 0; i < names.size(); i++) {
				model.addRow(new Object[] { false, names.get(i) });
			}

			if (this.modifyUser) {
				result = operation.executeRaw(String.format(DefaultQuerys.getUserDBRolesQuery, user));
				if (result.getStatus().equals(Status.FAILURE)) {
					this.parent.getResultReader().loadResult(result);
					return;
				}

				names = result.getTable().get("name");
				for (int i = 0; i < names.size(); i++) {
					for (int j = 0; j < model.getRowCount(); j++) {
						if (((String) (model.getValueAt(j, 1))).equals((String) names.get(i))) {
							model.setValueAt(true, j, 0);
						}
					}
				}
			}
		} catch (Exception e) {
			parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}
}
