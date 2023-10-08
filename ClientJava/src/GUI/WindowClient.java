package GUI;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

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

    public WindowClient() {

        setTitle("Le Maraicher en ligne");
        setIconImage(new ImageIcon(this.getClass().getResource("/Images/icon.png")).getImage()); // pour ajouter une icone a l'app
        setContentPane(JPanelMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // pour implementer les fonctions de fenetre de bas (exit, agrandir, ...)
        pack();



    }
}
