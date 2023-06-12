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
import app.lib.queryBuilders.DefaultQuerys;
import app.lib.queryBuilders.Login;
import app.lib.queryBuilders.User;
import app.lib.result.ResultFactory;
import app.lib.result.ResultType;
import app.lib.result.Status;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class UserEditor extends JPanel {
	private JTextField textField;
	private JPasswordField passwordField;
	private Main parent;
	private JLabel loginName;
	private ConnectionStringBuilder conStrGenerator;
	private String user;
	private String password;
	private JComboBox dbcBox;
	private JCheckBox chckbxRead;
	private JCheckBox chckbxWrite;
	private JCheckBox chckbxAdmin;
	private boolean editUsername;
	private boolean editPassword;
	private boolean newUser;

	/**
	 * Create the panel.
	 */
	public UserEditor(Main parent, boolean newUser, String username, String password,
			ConnectionStringBuilder conStrGenerator) {
		this.user = username == null ? "" : username;
		this.password = password == null ? "" : password;
		this.conStrGenerator = conStrGenerator;
		this.parent = parent;
		this.textField = new JTextField();
		this.loginName = new JLabel(conStrGenerator.getUserName());

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
				if (!newUser && (UserEditor.this.editPassword || UserEditor.this.editUsername)) {
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
		this.dbcBox.setEnabled(newUser);

		this.chckbxRead = new JCheckBox("Lectura");
		this.chckbxWrite = new JCheckBox("Escritura");
		this.chckbxAdmin = new JCheckBox("Administrador");

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout
				.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
												.addComponent(dbcBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(lblNewLabel_1)
												.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 209,
														Short.MAX_VALUE)
												.addComponent(textField))
										.addComponent(lblNewLabel))
								.addPreferredGap(ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel_3)
												.addPreferredGap(ComponentPlacement.RELATED).addComponent(loginName)
												.addGap(1))
										.addGroup(groupLayout.createSequentialGroup().addGap(10)
												.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(chckbxRead).addComponent(chckbxWrite)
														.addComponent(chckbxAdmin)))
										.addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 97,
												GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addContainerGap()
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_3)
								.addComponent(loginName))
						.addComponent(dbcBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup().addComponent(chckbxWrite)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxRead)))
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addGap(11).addComponent(lblNewLabel_1))
						.addGroup(groupLayout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(chckbxAdmin)))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(passwordField,
						GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton))
				.addGap(235)));
		setLayout(groupLayout);

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

			if (this.chckbxWrite.isSelected()) {
				result = operation.executeRaw(
						String.format("EXEC sp_addrolemember 'db_datawriter', '%s';", this.textField.getText()));
				this.parent.getResultReader().loadResult(result);
			}

			if (this.chckbxRead.isSelected()) {
				result = operation.executeRaw(
						String.format("EXEC sp_addrolemember 'db_datareader', '%s';", this.textField.getText()));
				this.parent.getResultReader().loadResult(result);
			}

			if (this.chckbxAdmin.isSelected()) {
				result = operation.executeRaw(
						String.format("EXEC sp_addrolemember 'db_ddladmin', '%s';", this.textField.getText()));
				this.parent.getResultReader().loadResult(result);
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

			this.parent.getTreeView().loadDatabaseObjects();
		} catch (Exception e) {
			this.parent.getResultReader().loadResult(ResultFactory.fromException(e));
		} finally {
			this.parent.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}
}
