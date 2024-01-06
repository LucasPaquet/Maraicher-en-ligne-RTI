import GUI.MainWindow;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        MainWindow gui = new MainWindow();

        gui.setVisible(true);
    }
}