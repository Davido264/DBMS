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
	private JTextField loginName;
	private JPasswordField passwordField;
	private Main parent;
	private ConnectionStringBuilder conStrGenerator;
	private String user;
	private String password;
	private boolean editUsername;
	private boolean editPassword;
	private boolean modifyUser;
	private JTable serverRolesTable;
	private JScrollPane panel_1;
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
		this.loginName = new JTextField();
		this.modifyUser = modifyUser;

		JLabel lblNewLabel = new JLabel("Nombre de Usuario");
		JLabel lblNewLabel_1 = new JLabel("Contraseña");

		this.loginName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					UserEditor.this.loginName.setText(UserEditor.this.user);
					UserEditor.this.editUsername = false;
					return;
				}
				UserEditor.this.editUsername = true;
			}
		});

		this.loginName.setColumns(10);

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

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
				groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout
						.createParallelGroup(Alignment.TRAILING)
						.addComponent(tabbedPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
								.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(lblNewLabel_1)
										.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
										.addComponent(loginName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE))
								.addComponent(lblNewLabel))
								.addPreferredGap(ComponentPlacement.RELATED, 162, Short.MAX_VALUE).addComponent(
										btnNewButton, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(22).addComponent(lblNewLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(loginName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(11).addComponent(lblNewLabel_1).addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNewButton))
						.addGap(18).addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addContainerGap(62, Short.MAX_VALUE)));

		JScrollPane panel = new JScrollPane();
		tabbedPane.addTab("Roles de Servidor", null, panel, null);

		serverRolesTable = new JTable();
		Object[] columnNames = new Object[] { "Activo", "Rol" };

		DefaultTableModel model = new DefaultTableModel() {
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

		for (Object o : columnNames) {
			model.addColumn(o);
		}

		serverRolesTable.setModel(model);
		TableColumn column0 = serverRolesTable.getColumnModel().getColumn(0);
		column0.setCellRenderer(serverRolesTable.getDefaultRenderer(Boolean.class));
		column0.setCellEditor(serverRolesTable.getDefaultEditor(Boolean.class));
		panel.setViewportView(serverRolesTable);

		this.loadTable(tabbedPane);
		setLayout(groupLayout);

		((AbstractDocument) loginName.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				// restringir ' ; \ y /
				if (text != null && text.matches("[a-zA-Z0-9_]")) {
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});

		if (username != null && !username.equals("")) {
			System.out.println(username);
			this.loginName.setText("username");
		}

		if (password != null && !password.equals("")) {
			this.passwordField.setText(password);
		}

	}

	private void loadTable(JTabbedPane tabbedPane) {
		try (var operation = new SQLOperation(this.conStrGenerator.build())) {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			var result = operation.executeRaw(DefaultQuerys.getDatabasesQuery);
			if (result.getStatus().equals(Status.FAILURE) || result.getType().equals(ResultType.STRING)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}

			Object[] databases = result.getTable().get("name").toArray();

			if (databases == null || databases.length == 0) {
				return;
			}

			panel_1 = new JScrollPane();
			tabbedPane.addTab("Roles de la Base de datos", null, panel_1, null);

			dbRolesTable = new JTable();

			result = operation.executeRaw(DefaultQuerys.getDBRolesQuery);
			if (result.getStatus().equals(Status.FAILURE)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}

			var names = result.getTable().get("name");

			if (names == null || names.size() == 0) {
				return;
			}

			names.add(0, "Base de Datos");

			DefaultTableModel model = new DefaultTableModel() {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 0) {
						return String.class; // Columna 1: String
					} else {
						return Boolean.class; // Columna 2: CheckBox
					}
				}

				@Override
				public boolean isCellEditable(int row, int column) {
					return column != 0;
				}

			};

			for (Object name : names) {
				model.addColumn(name);
			}

			dbRolesTable.setModel(model);
			for (int i = 1; i < dbRolesTable.getColumnCount(); i++) {
				TableColumn column0 = dbRolesTable.getColumnModel().getColumn(i);
				column0.setCellRenderer(dbRolesTable.getDefaultRenderer(Boolean.class));
				column0.setCellEditor(dbRolesTable.getDefaultEditor(Boolean.class));
			}

			for (Object database : databases) {
			}

			panel_1.setViewportView(dbRolesTable);

		} catch (Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void executeCreateUser() {
		try (var operation = new SQLOperation(this.conStrGenerator.build())) {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			String command = new Login(this.loginName.getText(), new String(this.passwordField.getPassword()).strip())
					.generateQuery();
			var result = operation.executeRaw(command);
			this.parent.getResultReader().loadResult(result);

			command = new User(this.loginName.getText(), this.loginName.getText()).generateQuery();
			result = operation.executeRaw(command);
			this.parent.getResultReader().loadResult(result);

			DefaultTableModel model = (DefaultTableModel) serverRolesTable.getModel();
			int rowCount = model.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				boolean assigned = (Boolean) model.getValueAt(i, 0);
				String roleName = (String) model.getValueAt(i, 1);
				if (assigned) {
					command = new AlterServerRole(roleName, this.loginName.getText()).generateQuery();
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
							String.format("EXEC sp_addrolemember '%s', '%s';", roleName, this.loginName.getText()));
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
						this.loginName.getText());
				String command = generator.generateQuery();
				var result = operation.executeRaw(command);
				this.parent.getResultReader().loadResult(result);
			}

			if (this.editPassword) {
				AlterUser generator = new AlterUser(this.loginName.getText(), AlterUser.Fields.PASSWORD,
						this.loginName.getText());
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
