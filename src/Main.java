import view.ExpenseGUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseGUI gui = new ExpenseGUI();
            gui.setVisible(true);
        });
    }
}
