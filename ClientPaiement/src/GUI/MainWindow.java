package GUI;

import VESPAP.ClientVESPAP;
import VESPAP.Facture;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainWindow extends JFrame{
    private JTextField tfNom;
    private JTextField tfMdp;
    private JButton btnLogin;
    private JButton btnLogout;
    private JPanel JPaneLogin;
    private JPanel JPaneFacture;
    private JTable table1;
    private JButton btnBuy;
    private JButton btnSearch;
    private JPanel JPaneMain;

    public MainWindow() {
        ClientVESPAP cl = new ClientVESPAP("127.0.0.1", 50000);

        setTitle("Le Maraicher en ligne");
        setContentPane(JPaneMain);
        pack();

        //********** Fermer la fenetre ***************
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(); // Fermez la fenêtre
            }
        });

        //********* Clique sur bouton *****************
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cl.VESPAP_Login(tfNom.getText(), tfMdp.getText()))
                    LoginOK();
            }
        });
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cl.VESPAP_Logout();
                LogoutOK();
            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                List<Facture> factures = null;

                factures = cl.VESPAP_GetFactures(1);

                System.out.println(factures.toString());
            }
        });
    }

    //********** Fonction d'interaction avec GUI **********************************
    private void LoginOK(){

        // Partie Login
        tfMdp.setEnabled(false);
        tfNom.setEnabled(false);
        btnLogin.setEnabled(false);
        btnLogout.setEnabled(true);

        // Partie Facture
        btnBuy.setEnabled(true);
        btnSearch.setEnabled(true);
    }

    private void LogoutOK(){
        // Partie Login
        tfMdp.setEnabled(true);
        tfNom.setEnabled(true);
        btnLogin.setEnabled(true);
        btnLogout.setEnabled(false);

        // Partie Facture
        btnBuy.setEnabled(false);
        btnSearch.setEnabled(false);
    }
}
