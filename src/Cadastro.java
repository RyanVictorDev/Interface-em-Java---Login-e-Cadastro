import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Cadastro extends JFrame {

    private JPanel Cadastro;
    private JTextField adicioneSeuNomeTextField;
    private JPasswordField senhaPasswordField;
    private JPasswordField passwordField1;
    private JButton CADASTRARButton;

    public Cadastro(){
        setContentPane(Cadastro);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        CADASTRARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Home();
            }
        });
    }
}
