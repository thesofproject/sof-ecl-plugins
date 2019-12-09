package org.sofproject.gst.json;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.eclipse.core.runtime.CoreException;
import org.sofproject.topo.ui.graph.ITopoGraph;


public class JsonCustomOptionPane extends JDialog
        implements ActionListener,
        PropertyChangeListener {

    private JOptionPane optionPane;
    private JTextField nameField;
    private JTextField versionField;
    private JTextField descriptionField;
    private JComboBox<String> typeBox;
    private String btnString1 = "OK";
    private String btnString2 = "Cancel";
    private ITopoGraph topoGraph;

    
    private static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }


    public JsonCustomOptionPane(Frame aFrame, ITopoGraph graph) {
        super(aFrame, true);

        topoGraph = graph;
        setTitle("Serialize Json");

		JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField("");
        
		JLabel versionLabel = new JLabel("Version:");
        versionField = new JTextField("");
        
		JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField("");
        
        JLabel typeLabel = new JLabel("Type:");
        String[] items = {"Gstreamer", "Ffmpeg"};
        typeBox = new JComboBox<>(items);
        
        Object[] array = {nameLabel, nameField, versionLabel, versionField, descriptionLabel, descriptionField, typeLabel, typeBox};

        Object[] options = {btnString1, btnString2};
        
        optionPane = new JOptionPane(array,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                options,
                options[0]);

        setContentPane(optionPane);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                nameField.requestFocusInWindow();
            }
        });

        nameField.addActionListener(this);
        versionField.addActionListener(this);

        optionPane.addPropertyChangeListener(this);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop)
                || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                return;
            }

            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                if (nameField.getText().isEmpty()) {
                	//text was invalid
                	nameField.selectAll();
                    JOptionPane.showMessageDialog(this,
                            "Name cannot be empty!",
                            "",
                            JOptionPane.ERROR_MESSAGE);
                    nameField.requestFocusInWindow();

                } 
                else if (!isInteger(versionField.getText())) {
                	versionField.selectAll();
                    JOptionPane.showMessageDialog(this,
                            "Version number should be an integer!",
                            "",
                            JOptionPane.ERROR_MESSAGE);
                    versionField.requestFocusInWindow();
                }
                else {
                	try {
	            		JsonProperty jsonProperty = new JsonProperty(nameField.getText(), descriptionField.getText(), versionField.getText(), typeBox.getSelectedItem().toString());
	            		topoGraph.serializeJson(jsonProperty);
					} catch (CoreException | IOException error) {
						error.printStackTrace(); //TODO:
					}
                    exit();
                }
            } else {
            	System.out.println("Json serialize cancelled");
                exit();
            }
        }
    }

    /**
     * This method clears the dialog and hides it.
     */
    public void exit() {
        dispose();
    }

}