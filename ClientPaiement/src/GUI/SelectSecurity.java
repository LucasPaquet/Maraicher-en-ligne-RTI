package GUI;

import javax.swing.*;

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
        btnOk.addActionListener(e -> {
            MainWindow gui = new MainWindow(cbTls.isSelected());
            dispose();
            gui.setVisible(true);
        });
    }
}
