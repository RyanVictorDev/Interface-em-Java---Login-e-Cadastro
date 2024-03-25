import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame{
    private JPanel Home;
    private JButton homeButton;
    private JButton livrosButton;
    private JButton bibliotecaPessoalButton;
    private JButton sairButton;

    public Home(){
        setContentPane(Home);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainInterface();
            }
        });
    }
}
