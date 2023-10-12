package GUI;

import Tcp.TcpConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowClient extends JFrame {
    private JTextField tfNom;
    private JTextField tfMdp;
    private JButton btnLogin;
    private JButton btnLogout;
    private JCheckBox cbNewClient;
    private JPanel JPanePublicite;
    private JTextField tfTotal;
    private JButton btnDelete;
    private JButton btnConfirm;
    private JButton btnVider;
    private JTable tablePanier;
    private JLabel tfImage;
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

    private int articleEnCours;
    private int idClient;

    public WindowClient(){

        articleEnCours = 1;

        DefaultTableModel model = new DefaultTableModel();

        // Définir les noms des colonnes
        model.setColumnIdentifiers(new String[]{"Article", "Prix à l'unité", "Quantité"});
        model.addRow(new Object[]{"Orange", "1,12", "12"});

        // Appliquer le modèle de tableau à la JTable
        tablePanier.setModel(model);

        // changer les proprietes de la JTable
        tablePanier.setDefaultEditor(Object.class, null);
        tablePanier.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePanier.setRowSelectionAllowed(true);
        tablePanier.setColumnSelectionAllowed(false);


        setTitle("Le Maraicher en ligne");
        setIconImage(new ImageIcon(this.getClass().getResource("/Images/icon.png")).getImage()); // pour ajouter une icone a l'app
        setContentPane(JPanelMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // pour implementer les fonctions de fenetre de bas (exit, agrandir, ...)
        pack();

        serverConnection = new TcpConnection("192.168.230.128", 50000);

        //********** Fermer la fenetre ***************
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                OVESP_Logout();
                serverConnection.close();
                dispose(); // Fermez la fenêtre
            }
        });
        //********* Clique sur bouton *****************
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (OVESP_Login())
                    OVESP_Consult(articleEnCours);
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                OVESP_Logout();
            }
        });
        btnPrevious.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                OVESP_Consult(articleEnCours - 1);
            }
        });

        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                OVESP_Consult(articleEnCours + 1);
            }
        });
        btnBuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (tfQuantite.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Entrez une quantité", "Erreur d'achat", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    if (OVESP_Achat()){
                        OVESP_Caddie();
                        OVESP_Consult(articleEnCours);
                    }
                }
            }
        });
    }

    //********** Fonction d'interaction avec GUI **********************************
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
        // les champs login
        tfNom.setText("");
        tfMdp.setText("");

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

        // les champs du magasin
        tfStock.setText("");
        tfArticle.setText("");
        tfPrice.setText("");
        tfQuantite.setText("");
        tfImage.setIcon(new ImageIcon("src/Images/missing.jpg"));

        articleEnCours = 1;
    }

    /**
     * Permet d'afficher un article sur le GUI
     * @param nom Nom de l'article
     * @param stock Quantité de l'article
     * @param prix Prix de l'article
     * @param image Nom du fichier de l'article ("src/Images/" sera ajouté dans la fonction)
     */
    public void setArticle(String nom, String stock, String prix, String image){
        tfArticle.setText(nom);
        tfStock.setText(stock);
        tfPrice.setText(prix);
        tfImage.setIcon(new ImageIcon("src/Images/" + image));
    }

    //********** Fonction de protocol OVESP **********************************
    /**
     * Connecte le client à son compte Vérification de l’existence et du mot de passe du client / Création d’un nouveau client dans la table clients
     */
    public boolean OVESP_Login(){
        String data;
        String response;

        if (tfNom.getText().isEmpty() || tfMdp.getText().isEmpty()){// si un des deux champs est vide
            JOptionPane.showMessageDialog(null, "Remplisez les champs !", "Erreur de connection", JOptionPane.ERROR_MESSAGE);
            return false;
        }

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
            idClient = Integer.parseInt(champs[3]);
            LoginOk();
            return true;
        }
        else {
            JOptionPane.showMessageDialog(null, champs[2], "Erreur de connection", JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }

    /**
     * Déconnecte le client. Si Caddie en cours, vide le caddie et met à jour la BD
     */
    public void OVESP_Logout(){
        // Envoie de la requete
        serverConnection.send("LOGOUT");

        // Reception de la reponse
        String response = serverConnection.receive();

        LogoutOk();
    }

    /**
     * Consultation d’un article en BD → si article non trouvé, retour -1 au client
     * @param article id de l'article que l'on veut consulter
     */
    public void OVESP_Consult(int article){
        String data;
        String response;

        // Envoie de la requete
        data = "CONSULT#" + article;
        serverConnection.send(data);

        // Reception et parsing de la reponse
        response = serverConnection.receive();
        String[] champs = response.split("#");

        if (champs[1].equals("ok"))
        {
            articleEnCours = Integer.parseInt(champs[2]);
            setArticle(champs[3],champs[4],champs[5],champs[6]);
        }
    }

    /**
     * Permet de d'acheter un article. Si article non trouvé, retour -1. Si trouvé mais que stock insuffisant, retour d’une quantité 0 → Si ok, le stock est mis à jour en BD et le contenu du caddie est mémorisé au niveau du serveur → actuellement aucune action sur tables factures et ventes
     */
    public boolean OVESP_Achat(){
        String data;
        String response;

        // Envoie de la requete
        data = "ACHAT#" + articleEnCours + "#" + tfQuantite.getText();
        serverConnection.send(data);

        // Reception et parsing de la reponse
        response = serverConnection.receive();
        String[] champs = response.split("#");

        if (champs[1].equals("ok")){
            JOptionPane.showMessageDialog(null, champs[2] + " articles["+ champs[3] + "] ont été ajoutés à votre panier", "Achat réussie", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        else{
            switch (Integer.parseInt(champs[2])){
                case 0: JOptionPane.showMessageDialog(null, "Stock insufisant","Erreur d'achat", JOptionPane.ERROR_MESSAGE);
                    break;
                case -1: JOptionPane.showMessageDialog(null, "Article non trouvé","Erreur d'achat", JOptionPane.ERROR_MESSAGE);
                    break;
                case -2: JOptionPane.showMessageDialog(null, "Votre panier est plein !","Erreur d'achat", JOptionPane.ERROR_MESSAGE);
                    break;
            }

            return false;
        }
    }

    /**
     * Retourne l’entièreté du contenu du caddie au client
     */
    public void OVESP_Caddie(){
        String data;
        String response;
        int nbArticle;
        float prixTotal = 0;

        // Envoie de la requete
        data = "CADDIE" ;
        serverConnection.send(data);

        // Reception et parsing de la reponse
        response = serverConnection.receive();
        String[] champs = response.split("#");

        nbArticle = Integer.parseInt(champs[1]);


        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Article", "Prix à l'unité", "Quantité"});

        for (int i = 0; i<nbArticle; i++){
            // Définir les noms des colonnes
            model.addRow(new Object[]{champs[(i*5)+3], champs[(i*5)+5], champs[(i*5)+4]});
            prixTotal += Float.parseFloat(champs[(i*5)+5]) * Integer.parseInt(champs[(i*5)+4]);
        }

        tfTotal.setText(String.valueOf(prixTotal));
        tablePanier.setModel(model);

    }
}
