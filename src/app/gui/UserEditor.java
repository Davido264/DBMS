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
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

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
	JCheckBox windowsAuth;
	private Object[] databases;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("serial")
	public UserEditor(Main parent, boolean modifyUser, String username, String password,
			ConnectionStringBuilder conStrGenerator) {
		String[] arr = username == null ? new String[] { "" } : username.split("\\\\");
		this.user = arr[arr.length - 1];
		this.password = password == null ? "" : password;
		this.conStrGenerator = conStrGenerator;
		this.parent = parent;
		this.modifyUser = modifyUser;

		this.loginName = new JTextField();
		this.passwordField = new JPasswordField();
		JLabel lblNewLabel = new JLabel("Nombre de Usuario");
		JLabel lblNewLabel_1 = new JLabel("Contraseña");

		if (this.user != null && !this.user.equals("")) {
			this.loginName.setText(this.user);
		}

		if (this.password != null && !this.password.equals("")) {
			this.passwordField.setText(this.password);
		}

		this.loginName.setEditable(!modifyUser);
		this.passwordField.setEditable(!modifyUser);

		this.loginName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.out.println("Hello");
					UserEditor.this.loginName.setText(UserEditor.this.user);
					UserEditor.this.editUsername = false;
					return;
				}
				UserEditor.this.editUsername = true;
			}
		});

		this.loginName.setColumns(10);
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
				if ((modifyUser && editUsername && !loginName.getText().equals("")) || !modifyUser) {
					UserEditor.this.user = loginName.getText();
				}
				if ((modifyUser && editPassword && !new String(passwordField.getPassword()).strip().equals(""))
						|| !modifyUser) {
					UserEditor.this.password = new String(passwordField.getPassword()).strip();
				}

				if (modifyUser) {
					executeAlterUser();
					return;
				}

				executeCreateUser();
			}
		});

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		this.windowsAuth = new JCheckBox("Usar Autenticación de Windows");
		this.windowsAuth.setEnabled(!modifyUser);

		windowsAuth.setVerticalAlignment(SwingConstants.TOP);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(
										groupLayout.createSequentialGroup()
												.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 459,
														Short.MAX_VALUE)
												.addContainerGap())
								.addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
												.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
														.addComponent(lblNewLabel_1)
														.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 144,
																Short.MAX_VALUE)
														.addComponent(loginName))
												.addGap(6).addComponent(windowsAuth)
												.addPreferredGap(ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
												.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 101,
														GroupLayout.PREFERRED_SIZE)
												.addGap(18))
										.addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel)
												.addPreferredGap(ComponentPlacement.RELATED, 360, Short.MAX_VALUE)))
										.addGap(0)))));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addGap(22).addComponent(lblNewLabel)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(loginName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addGap(11).addComponent(lblNewLabel_1).addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(windowsAuth).addComponent(btnNewButton))
				.addGap(18).addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

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
		this.fillTableRole();
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

	}

	private void loadTable(JTabbedPane tabbedPane) {
		try (var operation = new SQLOperation(this.conStrGenerator.build())) {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			var result = operation.executeRaw(DefaultQuerys.getDatabasesQuery);
			if (result.getStatus().equals(Status.FAILURE) || result.getType().equals(ResultType.STRING)) {
				this.parent.getResultReader().loadResult(result);
				return;
			}

			this.databases = result.getTable().get("name").toArray();

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

			dbRolesTable.getTableHeader().setReorderingAllowed(false);
			dbRolesTable.setModel(model);
			for (int i = 1; i < dbRolesTable.getColumnCount(); i++) {
				TableColumn column0 = dbRolesTable.getColumnModel().getColumn(i);
				column0.setCellRenderer(dbRolesTable.getDefaultRenderer(Boolean.class));
				column0.setCellEditor(dbRolesTable.getDefaultEditor(Boolean.class));
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
			StringBuilder sb = new StringBuilder();
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			String loginUser;

			if (this.windowsAuth.isSelected()) {
				loginUser = String.format("%s\\%s", System.getenv("USERDOMAIN"), this.loginName.getText());
			} else {
				loginUser = this.loginName.getText();
			}

			String command = new Login(loginUser, new String(this.passwordField.getPassword()).strip(),
					this.windowsAuth.isSelected()).generateQuery();

			sb.append(command);
			sb.append("\n");
			sb.append("\n");

			DefaultTableModel model = (DefaultTableModel) serverRolesTable.getModel();
			int rowCount = model.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				boolean assigned = (Boolean) model.getValueAt(i, 0);
				String roleName = (String) model.getValueAt(i, 1);
				if (assigned) {
					command = new AlterServerRole(roleName, this.loginName.getText()).generateQuery();
					sb.append(command);
					sb.append("\n");
				}
			}

			sb.append("\n");

			for (Object database : this.databases) {
				sb.append(String.format("USE [%s];\n", database));
				command = new User(this.loginName.getText(), loginUser).generateQuery();
				sb.append(command);
				sb.append("\n");
			}

			sb.append("\n");

			model = (DefaultTableModel) dbRolesTable.getModel();
			rowCount = model.getRowCount();
			int columnCount = model.getColumnCount();

			for (int i = 0; i < rowCount; i++) {
				String database = (String) model.getValueAt(i, 0);
				sb.append(String.format("USE [%s];\n", database));

				for (int j = 1; j < columnCount; j++) {
					boolean assigned = (Boolean) model.getValueAt(i, j);
					String roleName = (String) model.getColumnName(j);
					if (assigned) {
						sb.append(String.format("EXEC sp_addrolemember '%s', '%s';\n", roleName, this.user));
					}
				}
			}

			String script = sb.toString();
			if (this.parent.getSettings().imprimirComandos) {
				System.out.println(script);
			}
			var result = operation.executeRaw(script);
			System.out.println(result.getStatus());

		} catch (Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		this.parent.getTreeView().loadDatabaseObjects();
	}

	private void executeAlterUser() {
		try (var operation = new SQLOperation(this.conStrGenerator.build())) {
			StringBuilder sb = new StringBuilder();
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (this.editUsername) {
				AlterUser generator = new AlterUser(this.loginName.getText(), AlterUser.Fields.NAME,
						this.loginName.getText());
				String command = generator.generateQuery();
				sb.append(command);
				sb.append("\n");
			}

			if (this.editPassword) {
				AlterUser generator = new AlterUser(this.loginName.getText(), AlterUser.Fields.PASSWORD,
						this.loginName.getText());
				String command = generator.generateQuery();
				sb.append(command);
				sb.append("\n");

			}

			sb.append("\n");

			DefaultTableModel model = (DefaultTableModel) dbRolesTable.getModel();
			int rowCount = model.getRowCount();
			int columnCount = model.getColumnCount();

			for (int i = 0; i < rowCount; i++) {
				String database = (String) model.getValueAt(i, 0);
				sb.append(String.format("USE [%s];\n", database));

				for (int j = 1; j < columnCount; j++) {
					boolean assigned = (Boolean) model.getValueAt(i, j);
					String roleName = (String) model.getColumnName(j);
					if (!assigned) {
						sb.append(String.format("EXEC sp_droprolemember '%s', '%s';\n", roleName, this.user));
					} else {
						sb.append(String.format("EXEC sp_addrolemember '%s', '%s';\n", roleName, this.user));
					}
				}

			}

			String script = sb.toString();
			if (this.parent.getSettings().imprimirComandos) {
				System.out.println(script);
			}
			var result = operation.executeRaw(script);
			System.out.println(result.getStatus());

		} catch (Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		this.parent.getTreeView().loadDatabaseObjects();
	}

	private void fillTableRole() {
		try {
			try (var operation = new SQLOperation(this.conStrGenerator.build())) {
				parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				DefaultTableModel model = (DefaultTableModel) this.serverRolesTable.getModel();
				var result = operation.executeRaw(DefaultQuerys.getRolesQuery);

				if (this.parent.getSettings().imprimirComandos) {
					System.out.println(DefaultQuerys.getRolesQuery);
				}

				if (result.getStatus().equals(Status.FAILURE)) {
					this.parent.getResultReader().loadResult(result);
					return;
				}

				var names = result.getTable().get("name");

				for (int i = 0; i < names.size(); i++) {
					model.addRow(new Object[] { false, names.get(i) });
				}

				model = (DefaultTableModel) this.dbRolesTable.getModel();

				for (Object database : this.databases) {
					Object[] row = new Object[model.getColumnCount()];
					row[0] = database;
					for (int i = 1; i < row.length; i++) {
						row[i] = false;
					}
					model.addRow(row);
				}

				this.dbRolesTable.getTableHeader().setReorderingAllowed(false);
			}

			DefaultTableModel model = (DefaultTableModel) this.dbRolesTable.getModel();
			if (this.modifyUser) {
				for (int i = 0; i < this.databases.length; i++) {
					ConnectionStringBuilder constr = this.conStrGenerator.copy().withDbName((String) databases[i]);
					try (SQLOperation op = new SQLOperation(constr.build())) {
						var result = op.executeRaw(String.format(DefaultQuerys.getUserDBRolesQuery, this.user));
						if (result.getStatus().equals(Status.FAILURE)) {
							this.parent.getResultReader().loadResult(result);
							return;
						}

						var names = result.getTable().get("name");
						for (int j = 0; j < names.size(); j++) {
							for (int k = 1; k < model.getColumnCount(); k++) {
								if (model.getColumnName(k).equals((String) names.get(j))) {
									model.setValueAt(true, i, k);
								}
							}
						}
						this.dbRolesTable.revalidate();
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
