package app.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

import app.lib.connector.ConnectionStringBuilder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.DefaultComboBoxModel;

public class Connector extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final HashSet<String> hosts = new HashSet<String>();
	private JComboBox txtServer;
	private JTextField txtUserName;
	private JTextField txtPort;
	private JPasswordField txtPassword;
	private JCheckBox chckboxEncrypt;
	private JCheckBox chckboxPort;
	private JCheckBox chckboxIS;
	private boolean configured;

	/**
	 * Create the dialog.
	 */
	public Connector(JFrame parent) {
		super(parent, "Conexión", true);
		this.configured = false;
		setAlwaysOnTop(true);
		setResizable(false);

		JLabel lblUserName = new JLabel("Nombre de Usuario");
		JLabel lblPassword = new JLabel("Contraseña");
		lblUserName.setEnabled(false);
		lblPassword.setEnabled(false);

		setBounds(100, 100, 316, 365);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			txtPort = new JTextField();
			txtPort.setToolTipText("Puerto personalizado");
			txtPort.setText("1433");
			txtPort.setEnabled(false);
			txtPort.setColumns(10);
		}
		{
			txtUserName = new JTextField();
			txtUserName.setToolTipText("Nombre de Usuario de la base de datos");
			txtUserName.setColumns(10);
			txtUserName.setEnabled(false);
		}
		{
			this.loadKnownHosts();
			txtServer = new JComboBox();
			txtServer.setModel(new DefaultComboBoxModel(this.hosts.toArray()));
			txtServer.setEditable(true);
			txtServer.setToolTipText("Servidor");
		}
		{
			chckboxEncrypt = new JCheckBox("Conexión encriptada");
			chckboxEncrypt.setSelected(true);
		}
		{
			chckboxPort = new JCheckBox("Usar un puerto personalizado");
			chckboxPort.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					txtPort.setEnabled(!txtPort.isEnabled());
					txtPort.revalidate();
				}
			});
		}
		{
			txtPassword = new JPasswordField();
			txtPassword.setToolTipText("Contraseña");
			txtPassword.setEnabled(false);
		}
		{
			chckboxIS = new JCheckBox("Seguridad Integrada de Windows");
			chckboxIS.setSelected(true);
			chckboxIS.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					txtUserName.setEnabled(!txtUserName.isEnabled());
					txtPassword.setEnabled(!txtPassword.isEnabled());
					lblUserName.setEnabled(!lblUserName.isEnabled());
					lblPassword.setEnabled(!lblPassword.isEnabled());
					txtUserName.revalidate();
					lblUserName.revalidate();
					txtPassword.revalidate();
					lblPassword.revalidate();
				}
			});
		}

		JLabel lblNewLabel = new JLabel("Servidor");

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup().addGap(28).addGroup(gl_contentPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPanel.createSequentialGroup().addComponent(this.chckboxIS).addContainerGap())
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup().addComponent(lblUserName)
										.addContainerGap())
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPanel.createSequentialGroup()
												.addComponent(txtUserName, 238, 238, 238).addContainerGap())
										.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_contentPanel.createSequentialGroup()
														.addComponent(lblPassword).addContainerGap())
												.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
														.addGroup(gl_contentPanel.createSequentialGroup()
																.addComponent(txtPassword, 238, 238, 238)
																.addContainerGap())
														.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
																.addGroup(gl_contentPanel.createSequentialGroup()
																		.addComponent(chckboxPort)
																		.addPreferredGap(ComponentPlacement.UNRELATED)
																		.addComponent(txtPort, GroupLayout.DEFAULT_SIZE,
																				65, Short.MAX_VALUE)
																		.addGap(24))
																.addGroup(gl_contentPanel.createSequentialGroup()
																		.addComponent(chckboxEncrypt).addContainerGap())
																.addGroup(gl_contentPanel.createSequentialGroup()
																		.addGroup(gl_contentPanel
																				.createParallelGroup(Alignment.LEADING)
																				.addComponent(txtServer, 238, 238, 238)
																				.addComponent(lblNewLabel))
																		.addGap(24))))))))));
		gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPanel
				.createSequentialGroup().addGap(17).addComponent(lblNewLabel)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(txtServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.chckboxIS)
				.addPreferredGap(ComponentPlacement.RELATED, 16, Short.MAX_VALUE).addComponent(lblUserName)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(txtUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblPassword)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(
						txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE).addComponent(chckboxPort)
						.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(chckboxEncrypt).addGap(28)));
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveKnownHosts();
						configured = true;
						dispose();
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
						configured = false;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public ConnectionStringBuilder getConnectionStringBuilder() {
		String host = (this.txtServer.getSelectedItem() != null) ? this.txtServer.getSelectedItem().toString().strip()
				: "";

		return new ConnectionStringBuilder().withHost(host).withDbName("master")
				.withUserName(this.txtUserName.getText().strip())
				.withPassword(new String(this.txtPassword.getPassword()).strip())
				.withIntegratedSecurity(this.chckboxIS.isSelected())
				.withPort(Integer.parseInt(this.txtPort.getText().strip()))
				.withEncrypt(this.chckboxEncrypt.isSelected()).withTrustServerCertificates(true);
	}

	private void loadKnownHosts() {
		String localAppDataPath = System.getenv("LOCALAPPDATA");
		String filePath = Path.of(localAppDataPath, "Proyecto-DBMS", "known_hosts").toAbsolutePath().toString();
		String dirPath = Path.of(localAppDataPath, "Proyecto-DBMS").toAbsolutePath().toString();

		try {
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				this.hosts.add(line);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void saveKnownHosts() {
		if (this.txtServer.getSelectedItem() == null) {
			return;
		}

		this.hosts.add(this.txtServer.getSelectedItem().toString().strip());
		String localAppDataPath = System.getenv("LOCALAPPDATA");
		String filePath = Path.of(localAppDataPath, "Proyecto-DBMS", "known_hosts").toAbsolutePath().toString();

		try (FileWriter writer = new FileWriter(filePath)) {
			String content = String.join("\n", hosts);
			writer.write(content);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isConfigured() {
		return this.configured;
	}
}
