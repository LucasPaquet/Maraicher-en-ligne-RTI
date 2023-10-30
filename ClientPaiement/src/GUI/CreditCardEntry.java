package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreditCardEntry extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfNom;
    private JTextField tfNumVisa;
    private boolean isOk;
    public CreditCardEntry(Frame parent) {
        super(parent, "Saisie de carte de crédit", true);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();
        setLocation(500,400);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        isOk = true;
        dispose();
    }

    private void onCancel() {
        isOk = false;
        dispose();
    }

    public String getNom(){
        return tfNom.getText();
    }

    public String getNumVisa(){
        return tfNumVisa.getText();
    }

    public boolean isOk() {
        return isOk;
    }
}
