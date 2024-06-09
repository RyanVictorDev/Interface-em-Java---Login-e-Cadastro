import Objs.Conexao;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteHome extends JFrame {
    private JPanel homePanel;
    private JLabel opcoes;
    private JToolBar navToolBar;
    private JLabel Titulo;
    private JLabel BoasVindas;
    private JButton publicarLivro;
    private JButton alugarLivro;
    private JButton depositarDinheiro;

    public ClienteHome(String nomeUsuario, boolean isAdmin) {
        setContentPane(homePanel);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem2 = new JMenuItem("Biblioteca Pessoal");
        JMenuItem menuItem3 = new JMenuItem("Saldo: R$ " + obterSaldoUsuario(nomeUsuario)); // Exibe o saldo atual
        JMenuItem menuItem4 = new JMenuItem("Sair");

        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.add(menuItem3);
        popupMenu.add(menuItem4);


        BoasVindas.setText("Olá, " + nomeUsuario);

        menuItem1.addActionListener(e -> {
            new ClienteHome(nomeUsuario, false);
            dispose();
        });

        menuItem2.addActionListener(e -> {
            new BibliotecaBase(nomeUsuario, false);
            dispose();
        });

        menuItem3.addActionListener(e -> {
            new ClienteDeposito(nomeUsuario);
            dispose();
        });

        menuItem4.addActionListener(e -> {
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

        publicarLivro.addActionListener(e -> {
            new ClienteUploadLivro(nomeUsuario);
            dispose();
        });
        alugarLivro.addActionListener(e -> {
            new ClienteLoja(nomeUsuario);
            dispose();
        });
        depositarDinheiro.addActionListener(e -> {
            new ClienteDeposito(nomeUsuario);
            dispose();
        });
    }

    private double obterSaldoUsuario(String nomeUsuario) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT saldo FROM usuario WHERE nome = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, nomeUsuario);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("saldo");
            } else {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
                return 0;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao obter saldo: " + ex.getMessage());
            return 0;
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }
}
