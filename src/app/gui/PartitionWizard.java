package app.gui;

import java.awt.BorderLayout;
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
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class PartitionWizard extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblNewLabel;
	private String tableName;

	/**
	 * Create the dialog.
	 */
	public PartitionWizard(String tableName) {
		this.tableName = tableName;
		setBounds(100, 100, 507, 469);
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
						PartitionWizard.this.dispose();
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
			GroupLayout gl_panel = new GroupLayout(panel);
			gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup().addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
									.addComponent(comboBox, 0, 471, Short.MAX_VALUE).addComponent(lblNewLabel_1)
									.addComponent(lblNewLabel_2))
							.addContainerGap()));
			gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup().addContainerGap().addComponent(lblNewLabel_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblNewLabel_2)
							.addContainerGap(287, Short.MAX_VALUE)));
			panel.setLayout(gl_panel);
		}

	}

	private enum FragmentationType {
		HORIZONTAL, VERTICAL, MIXTA
	}
}
