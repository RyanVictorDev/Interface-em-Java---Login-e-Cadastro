import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainInterface extends JFrame {

    private JPanel section1;
    private JButton cadastrarSeButton;
    private JButton entrarButton;

    public MainInterface(){
        setContentPane(section1);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        cadastrarSeButton.addActionListener(e -> new Cadastro());
        entrarButton.addActionListener(e -> new Login());
    }
    public static void main(String[] args) {
        new MainInterface();
    }
}
