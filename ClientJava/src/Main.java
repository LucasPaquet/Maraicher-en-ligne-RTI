import GUI.WindowClient;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup(); // pour mettre un look and feel plus moderne
        WindowClient gui = new WindowClient();



        gui.setVisible(true);
    }
}