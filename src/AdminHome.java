import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminHome extends JFrame {
    private JPanel homePanel;
    private JLabel opcoes;
    private JToolBar navToolBar;
    private JLabel Titulo;
    private JLabel BoasVindas;
    private JButton consultaUsuarios;
    private JButton consultaLivros;

    public AdminHome(String nomeUsuario) {

        setContentPane(homePanel);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem2 = new JMenuItem("Sair");

        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);

        BoasVindas.setText("Olá, " + nomeUsuario);

        menuItem1.addActionListener(e -> {
            new AdminHome(nomeUsuario);
            dispose();
        });

        menuItem2.addActionListener(e -> {
            new MainInterface();
            dispose();
        });

        opcoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(opcoes, e.getX(), e.getY());
                }
            }
        });

        consultaLivros.addActionListener(e -> {
            new BibliotecaBase(nomeUsuario, true);
            dispose();
        });

        consultaUsuarios.addActionListener(e -> {
            new AdminConsultaUsuario(nomeUsuario);
            dispose();
        });
    }

}
