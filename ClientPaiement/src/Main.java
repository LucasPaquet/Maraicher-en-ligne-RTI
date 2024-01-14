import GUI.MainWindow;
import GUI.SelectSecurity;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SelectSecurity gui = new SelectSecurity();

        gui.setVisible(true);
    }
}