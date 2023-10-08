package GUI;

import javax.swing.*;

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
        setContentPane(JPanelMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // pour implementer les fonctions de fenetre de bas (exit, agrandir, ...)
        pack();

    }
}
