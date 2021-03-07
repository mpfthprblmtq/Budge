package budge.service;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.Component;

public class DialogService {

    private final ImageIcon budgeIcon = new ImageIcon(this.getClass().getResource("/resources/img/b.png"));

    public DialogService() {

    }

    public void showErrorDialog(String title, String message, Component component) {
        JOptionPane.showMessageDialog(
                component,
                message,
                title,
                JOptionPane.ERROR_MESSAGE,
                budgeIcon);
    }

    public void showDialog(String title, String message, Component component) {
        JOptionPane.showMessageDialog(
                component,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE
        );
    }
}
