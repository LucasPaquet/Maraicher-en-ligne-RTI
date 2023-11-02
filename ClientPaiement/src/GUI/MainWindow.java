package GUI;

import VESPAP.ClientVESPAP;
import VESPAP.Facture;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class MainWindow extends JFrame{
    private JTextField tfNom;
    private JTextField tfMdp;
    private JButton btnLogin;
    private JButton btnLogout;
    private JPanel JPaneLogin;
    private JPanel JPaneFacture;
    private JTable tableFacture;
    private JButton btnBuy;
    private JButton btnSearch;
    private JPanel JPaneMain;
    private JScrollPane JPaneScroolFacture;
    private JTextField tfClient;
    private JPanel JpaneSearchClient;
    private ClientVESPAP cl;
    private String ip;
    private int port;

    public MainWindow() {

        initConfig();

        // Connexion serveur
        cl = new ClientVESPAP(ip, port);

        tableFacture.setDefaultEditor(Object.class, null);
        tableFacture.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableFacture.setRowSelectionAllowed(true);
        tableFacture.setColumnSelectionAllowed(false);

        setTitle("Le Maraicher en ligne");
        setContentPane(JPaneMain);
        pack();
        setLocation(400,200);

        //********** Fermer la fenetre ***************
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(); // Fermez la fenêtre
            }
        });

        //********* Clique sur bouton *****************
        btnLogin.addActionListener(actionEvent -> VESPAPLogin());

        btnLogout.addActionListener(actionEvent -> {
            cl.VESPAP_Logout();
            LogoutOK();
        });
        btnSearch.addActionListener(actionEvent -> VESPAPGetFactures());
        btnBuy.addActionListener(actionEvent -> {
            VESPAP_Payer();
            VESPAPGetFactures();
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
        tfClient.setEnabled(true);
    }

    private void LogoutOK(){
        // Partie Login
        tfMdp.setEnabled(true);
        tfNom.setEnabled(true);
        btnLogin.setEnabled(true);
        btnLogout.setEnabled(false);
        tfNom.setText("");
        tfMdp.setText("");

        // Partie Facture
        btnBuy.setEnabled(false);
        btnSearch.setEnabled(false);
        tfClient.setEnabled(false);
        tfClient.setText("");
        viderTableFactures();

    }
    private void viderTableFactures(){
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID facture", "ID client", "Prix", "Date", "Payé"});
        tableFacture.setModel(model);
    }

    //********** Fonction de protocol VESPAP **********************************
    private void VESPAPLogin(){

        if (cl.IsOosNull()){
            JOptionPane.showMessageDialog(null, "Vous n'êtes pas connecté au serveur", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            cl = new ClientVESPAP(ip, port); // on retente de se connecte au serveur
        }

        if(tfNom.getText().isEmpty() || tfMdp.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Remplisez les champs !", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cl.VESPAP_Login(tfNom.getText(), tfMdp.getText())){
            JOptionPane.showMessageDialog(null, "Vous êtes bien connecté", "Connection réussie !", JOptionPane.INFORMATION_MESSAGE);
            LoginOK();
        }
        else
            JOptionPane.showMessageDialog(null, "Les identifiants sont incorrects", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);

    }

    private void VESPAPGetFactures(){
        List<Facture> factures;

        try { // si on arrive pas à convertir le chalos quantite en int

            int idClient = Integer.parseInt(tfClient.getText());

            if (idClient > 0){ // pour empecher les nombres negatif
                factures = cl.VESPAP_GetFactures(idClient);

                if (factures.isEmpty()){ // si il n'y a pas de factures pour le client
                    JOptionPane.showMessageDialog(null, "Il n'y a pas de facture pour ce client", "Facture non trouvé", JOptionPane.INFORMATION_MESSAGE);
                }

                System.out.println(factures);
                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(new String[]{"ID facture", "ID client", "Prix", "Date", "Payé"});

                for (Facture facture : factures) {
                    // Définir les noms des colonnes
                    if (facture.isPaye()) // si la factures a ete paye on ne l'affiche pas
                        continue;
                    model.addRow(new Object[]{facture.getIdFacture(),
                            facture.getIdClient(),
                            facture.getPrix(),
                            facture.getDate(),
                            facture.isPaye()});
                }

                tableFacture.setModel(model);
            }

            else {
                JOptionPane.showMessageDialog(null, "Entrez un nombre positif", "Erreur d'achat", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, "Entrez un nombre valide", "Erreur d'achat", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void VESPAP_Payer(){
        if (tableFacture.getSelectedRow() == -1){
            JOptionPane.showMessageDialog(null, "Sélectionnez la facture à régler", "Erreur de Payement", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // On cree la boite de diaglogue et on l'affiche
        CreditCardEntry dialog = new CreditCardEntry(MainWindow.this);
        dialog.setModal(true);
        dialog.setVisible(true);

        if(!dialog.isOk()){ // si on appuie sur annuler
            JOptionPane.showMessageDialog(null, "La saisie de la carte à été annulé", "Erreur de Payement", JOptionPane.ERROR_MESSAGE);
            return;
        }



        if (cl.VESPAP_PayFactures(
                Integer.parseInt(tableFacture.getValueAt(tableFacture.getSelectedRow(), 0).toString()),
                dialog.getNom(),
                dialog.getNumVisa()))
        {
            JOptionPane.showMessageDialog(null, "La facture a bien été payé", "Payement réussi", JOptionPane.INFORMATION_MESSAGE);
        }
        else
            JOptionPane.showMessageDialog(null, "La facture n'a pas été payé", "Erreur de Payement", JOptionPane.ERROR_MESSAGE);
    }

    // *********************** LOGIQUE APPLICATION *****************************

    public void initConfig() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/config.properties")) {
            properties.load(fis);

            port = Integer.parseInt(properties.getProperty("PORT_PAIEMENT"));
            ip = properties.getProperty("IP_PAIEMENT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
