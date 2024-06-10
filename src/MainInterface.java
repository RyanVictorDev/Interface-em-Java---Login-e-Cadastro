import javax.swing.*;

public class    MainInterface extends JFrame {

    private JPanel section1;
    private JButton cadastrarSeButton;
    private JButton entrarButton;

    public MainInterface(){
        setContentPane(section1);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        cadastrarSeButton.addActionListener(e -> {
            new Cadastro();
            dispose();
        });
        entrarButton.addActionListener(e -> {
            new Login();
            dispose();
        });
    }

    public static void main(String[] args) {
        new MainInterface();
    }
}
