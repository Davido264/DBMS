package app.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

import app.lib.connector.ConnectionStringBuilder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class Connector extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtServer;
	private JTextField txtUserName;
	private JTextField txtPort;
	private JPasswordField txtPassword;
	private JCheckBox chckboxEncrypt;
	private JCheckBox chckboxPort;
	private JTextField txtDbName;
	private boolean configured;

	/**
	 * Create the dialog.
	 */
	public Connector(JFrame parent) {
		super(parent,"Conexión",true);
		this.configured = false;
		setAlwaysOnTop(true);
		setResizable(false);
		
		setBounds(100, 100, 316, 400);
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
		}
		{
			txtServer = new JTextField();
			txtServer.setToolTipText("Servidor");
			txtServer.setColumns(10);
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
		}
		
		JLabel lblNewLabel = new JLabel("Servidor");
		
		JLabel lblNewLabel_1 = new JLabel("Nombre de Usuario");
		
		JLabel lblNewLabel_2 = new JLabel("Contraseña");
		
		txtDbName = new JTextField();
		txtDbName.setToolTipText("Nombre de la base de datos");
		txtDbName.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Nombre de la Base de Datos");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(28)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_3)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
							.addComponent(lblNewLabel_1)
							.addComponent(txtUserName)
							.addComponent(lblNewLabel_2)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(chckboxPort)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(txtPort, GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
							.addComponent(chckboxEncrypt)
							.addComponent(txtServer)
							.addComponent(lblNewLabel)
							.addComponent(txtPassword)
							.addComponent(txtDbName)))
					.addGap(24))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(17)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_3)
					.addGap(5)
					.addComponent(txtDbName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckboxPort)
						.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckboxEncrypt)
					.addContainerGap(51, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
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
	
	public String getConnectionString() {
		return new ConnectionStringBuilder()
				.withHost(this.txtServer.getText().strip())
				.withDbName(this.txtDbName.getText().strip())
				.withUserName(this.txtUserName.getText().strip())
				.withPassword(new String(this.txtPassword.getPassword()).strip())
				.withPort(Integer.parseInt(this.txtPort.getText().strip()))
				.withEncrypt(this.chckboxEncrypt.isEnabled())
				.withTrustServerCertificates(true)
				.build();
	}
	
	public boolean isConfigured() {
		return this.configured;
	}
}
