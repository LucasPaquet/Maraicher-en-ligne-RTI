package GUI;

import Tcp.TcpConnection;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowClient extends JFrame {
    private JTextField tfNom;
    private JTextField tfMdp;
    private JButton btnLogin;
    private JButton btnLogout;
    private JCheckBox cbNewClient;
    private JPanel JPanePublicite;
    private JTextField textField7;
    private JButton btnDelete;
    private JButton btnConfirm;
    private JButton btnVider;
    private JTable tablePanier;
    private JLabel PlaceHolderImage;
    private JButton btnNext;
    private JButton btnPrevious;
    private JButton btnBuy;
    private JTextField tfArticle;
    private JTextField tfPrice;
    private JTextField tfStock;
    private JTextField tfQuantite;
    private JPanel JPanePanier;
    private JPanel JPaneMagasin;
    private JPanel JPanelMain;
    private TcpConnection serverConnection;

    public WindowClient() {

        setTitle("Le Maraicher en ligne");
        setIconImage(new ImageIcon(this.getClass().getResource("/Images/icon.png")).getImage()); // pour ajouter une icone a l'app
        setContentPane(JPanelMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // pour implementer les fonctions de fenetre de bas (exit, agrandir, ...)
        pack();

        serverConnection = new TcpConnection("192.168.28.128", 50000);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                OVESP_Login();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                OVESP_Logout();
            }
        });
    }

    /**
     * Permet de mettre le GUI dans l'état login (quand l'utilisateur est connecté)
     */
    public void LoginOk(){
        // login
        tfNom.setEnabled(false);
        tfMdp.setEnabled(false);
        btnLogin.setEnabled(false);
        cbNewClient.setEnabled(false);
        btnLogout.setEnabled(true);

        // Magasin
        btnNext.setEnabled(true);
        btnPrevious.setEnabled(true);
        tfQuantite.setEnabled(true);
        btnBuy.setEnabled(true);

        // Panier
        btnDelete.setEnabled(true);
        btnVider.setEnabled(true);
        btnConfirm.setEnabled(true);
    }

    /**
     * Permet de mettre le GUI dans l'état logout (quand l'utilisateur n'est pas connecté)
     */
    public void LogoutOk(){
        // login
        tfNom.setEnabled(true);
        tfMdp.setEnabled(true);
        btnLogin.setEnabled(true);
        cbNewClient.setEnabled(true);
        btnLogout.setEnabled(false);

        // Magasin
        btnNext.setEnabled(false);
        btnPrevious.setEnabled(false);
        tfQuantite.setEnabled(false);
        btnBuy.setEnabled(false);

        // Panier
        btnDelete.setEnabled(false);
        btnVider.setEnabled(false);
        btnConfirm.setEnabled(false);
    }

    public void OVESP_Login(){
        String data;
        String response;

        // Envoie de la requete
        int value = cbNewClient.isSelected() ? 1 : 0; // 1 = true = coche // 0 = false = pas coche
        data = "LOGIN#" + tfNom.getText() + "#" + tfMdp.getText() + "#" + value;
        serverConnection.send(data);

        // Reception et parsing de la reponse
        response = serverConnection.receive();
        String[] champs = response.split("#");

        System.out.println("DEBUG : " + champs[1]);

        if (champs[1].equals("ok")){
            JOptionPane.showMessageDialog(null, champs[2], "Connection réussie !", JOptionPane.INFORMATION_MESSAGE);
            LoginOk();
        }
        else {
            JOptionPane.showMessageDialog(null, champs[2], "Erreur de connection", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void OVESP_Logout(){
        // Envoie de la requete
        serverConnection.send("LOGOUT");

        // Reception de la reponse
        String response = serverConnection.receive();

        LogoutOk();
        serverConnection.close(); // DEBUG, pour l'instant
    }
}
