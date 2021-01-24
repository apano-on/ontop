package it.unibz.inf.ontop.protege.panels;

/*
 * #%L
 * ontop-protege4
 * %%
 * Copyright (C) 2009 - 2013 KRDB Research Centre. Free University of Bozen Bolzano.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import it.unibz.inf.ontop.protege.gui.IconLoader;
import it.unibz.inf.ontop.protege.utils.OptionPaneUtils;
import it.unibz.inf.ontop.protege.core.querymanager.QueryController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveQueryPanel extends JPanel {

	private static final long serialVersionUID = -7101725310389493765L;
	
	private final static String NEWGROUP = "New group...";
	private final static String NOGROUP = "No group...";

	private final JDialog parent;
	private final String query;
	
	private final QueryController queryController;

	/**
	 * Default constructor
	 */
	public SaveQueryPanel(String query, JDialog parent, QueryController queryController) {
		this.query = query;
		this.parent = parent;
		this.queryController = queryController;
		
		initComponents();

        cmbQueryGroup.removeAllItems();
        cmbQueryGroup.insertItemAt(NOGROUP, cmbQueryGroup.getItemCount());

        for (QueryController.Group group : queryController.getGroups()) {
            if (!group.isDegenerate())
                cmbQueryGroup.insertItemAt(group.getID(), cmbQueryGroup.getItemCount());
        }
        cmbQueryGroup.insertItemAt(NEWGROUP, cmbQueryGroup.getItemCount());
        cmbQueryGroup.addItemListener(e -> {
            String name = (String) e.getItem();
            txtGroupName.setEnabled(name.equals(NEWGROUP));
        });
        cmbQueryGroup.setSelectedItem(NOGROUP);
    }

    /**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        pnlSaveForm = new JPanel();
        lblDescription = new JLabel();
        lblID = new JLabel();
        lblGroup = new JLabel();
        lblGroupName = new JLabel();
        txtQueryID = new JTextField();
        cmbQueryGroup = new JComboBox();
        txtGroupName = new JTextField();
        pnlCommandButton = new JPanel();
        cmdCreateNew = new JButton();
        cmdCancel = new JButton();

        setMaximumSize(new Dimension(320, 155));
        setMinimumSize(new Dimension(320, 155));
        setPreferredSize(new Dimension(320, 155));
        setLayout(new BorderLayout());

        pnlSaveForm.setMaximumSize(new Dimension(340, 120));
        pnlSaveForm.setMinimumSize(new Dimension(340, 120));
        pnlSaveForm.setPreferredSize(new Dimension(340, 120));
        pnlSaveForm.setLayout(new GridBagLayout());

        lblDescription.setFont(new Font("Arial", 0, 11));
        lblDescription.setText("<html>Insert an ID and group  for this query.</html>");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 0, 10, 0);
        pnlSaveForm.add(lblDescription, gridBagConstraints);

        lblID.setFont(new Font("Arial", 0, 11));
        lblID.setText("Query ID:");
        lblID.setPreferredSize(new Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(0, 0, 4, 0);
        pnlSaveForm.add(lblID, gridBagConstraints);

        lblGroup.setFont(new Font("Arial", 0, 11));
        lblGroup.setText("Query Group:");
        lblGroup.setPreferredSize(new Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(0, 0, 4, 0);
        pnlSaveForm.add(lblGroup, gridBagConstraints);

        lblGroupName.setFont(new Font("Arial", 0, 11));
        lblGroupName.setText("Group Name:");
        lblGroupName.setPreferredSize(new Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(0, 0, 4, 0);
        pnlSaveForm.add(lblGroupName, gridBagConstraints);

        txtQueryID.setMinimumSize(new Dimension(150, 22));
        txtQueryID.setPreferredSize(new Dimension(170, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 4, 0);
        pnlSaveForm.add(txtQueryID, gridBagConstraints);

        cmbQueryGroup.setModel(new DefaultComboBoxModel(new String[] { "No group..." }));
        cmbQueryGroup.setBorder(BorderFactory.createEtchedBorder());
        cmbQueryGroup.setPreferredSize(new Dimension(140, 27));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        pnlSaveForm.add(cmbQueryGroup, gridBagConstraints);

        txtGroupName.setEnabled(false);
        txtGroupName.setMinimumSize(new Dimension(150, 22));
        txtGroupName.setPreferredSize(new Dimension(170, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(0, 0, 4, 0);
        pnlSaveForm.add(txtGroupName, gridBagConstraints);

        add(pnlSaveForm, BorderLayout.CENTER);

        pnlCommandButton.setMinimumSize(new Dimension(340, 35));
        pnlCommandButton.setPreferredSize(new Dimension(340, 35));

        cmdCreateNew.setIcon(IconLoader.getImageIcon("images/accept.png"));
        cmdCreateNew.setText("Accept");
        cmdCreateNew.setBorder(BorderFactory.createEtchedBorder());
        cmdCreateNew.setContentAreaFilled(false);
        cmdCreateNew.setIconTextGap(5);
        cmdCreateNew.setMaximumSize(new Dimension(95, 25));
        cmdCreateNew.setMinimumSize(new Dimension(95, 25));
        cmdCreateNew.setPreferredSize(new Dimension(95, 25));
        cmdCreateNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cmdCreateNewActionPerformed(evt);
            }
        });
        pnlCommandButton.add(cmdCreateNew);

        cmdCancel.setIcon(IconLoader.getImageIcon("images/cancel.png"));
        cmdCancel.setText("Cancel");
        cmdCancel.setBorder(BorderFactory.createEtchedBorder());
        cmdCancel.setContentAreaFilled(false);
        cmdCancel.setIconTextGap(5);
        cmdCancel.setMaximumSize(new Dimension(95, 25));
        cmdCancel.setMinimumSize(new Dimension(95, 25));
        cmdCancel.setPreferredSize(new Dimension(95, 25));
        cmdCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cmdCancelActionPerformed(evt);
            }
        });
        pnlCommandButton.add(cmdCancel);

        add(pnlCommandButton, BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

	private void cmdCreateNewActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonAcceptActionPerformed
		try {
            String id = txtQueryID.getText();
            String groupId = (String) cmbQueryGroup.getSelectedItem();
            if (groupId.equals(NOGROUP)) {
                queryController.addQuery(id, query);
            }
            else {
                QueryController.Group group;
                if (groupId.equals(NEWGROUP)) {
                    String newGroupId = txtGroupName.getText().trim();
                    group = queryController.addGroup(newGroupId);
                }
                else {
                    group = queryController.getGroup(groupId);
                }
                queryController.addQuery(group, id, query);
            }
        }
		catch (IllegalArgumentException e) {
            OptionPaneUtils.showPrettyMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
		parent.dispose();
	}// GEN-LAST:event_buttonAcceptActionPerformed

	private void cmdCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonCancelActionPerformed
		parent.dispose();
	}// GEN-LAST:event_buttonCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox cmbQueryGroup;
    private JButton cmdCancel;
    private JButton cmdCreateNew;
    private JLabel lblDescription;
    private JLabel lblGroup;
    private JLabel lblGroupName;
    private JLabel lblID;
    private JPanel pnlCommandButton;
    private JPanel pnlSaveForm;
    private JTextField txtGroupName;
    private JTextField txtQueryID;
    // End of variables declaration//GEN-END:variables
}
