package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectSecurity extends JFrame{
    private JPanel JPanelMain;
    private JCheckBox cbTls;
    private JButton btnOk;

    public SelectSecurity(){
        setTitle("Le Maraîcher en ligne");
        setContentPane(JPanelMain);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow gui = new MainWindow(cbTls.isSelected());
                dispose();
                gui.setVisible(true);
            }
        });
    }
}
