import Objs.Conexao;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDeposito extends JFrame {
    private JLabel Titulo;
    private JLabel opcoes;
    private JPanel section;
    private JTextField textField1;
    private JButton confirmarButton;
    private String nomeUsuario;
    private final JMenuItem menuItem3;

    public ClienteDeposito(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
        setContentPane(section);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem2 = new JMenuItem("Biblioteca Pessoal");
        menuItem3 = new JMenuItem("Saldo: R$ " + obterSaldoUsuario(nomeUsuario)); // Exibe o saldo atual
        JMenuItem menuItem4 = new JMenuItem("Sair");

        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.add(menuItem3);
        popupMenu.add(menuItem4);

        menuItem1.addActionListener(e -> {
            new ClienteHome(nomeUsuario,false);
            dispose();
        });

        menuItem2.addActionListener(e -> {
            new BibliotecaBase(nomeUsuario,false);
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

        // Adicionar um ouvinte de mouse à JLabel "opcoes" para exibir o menu pop-up quando o botão direito do mouse for clicado
        opcoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(opcoes, e.getX(), e.getY());
                }
            }
        });

        confirmarButton.addActionListener(e -> {
            double valorDeposito = Double.parseDouble(textField1.getText());
            if (valorDeposito > 0) {
                adicionarSaldoUsuario(nomeUsuario, valorDeposito);
                menuItem3.setText("Saldo: R$ " + obterSaldoUsuario(nomeUsuario)); // Atualiza o saldo no menu
                JOptionPane.showMessageDialog(this, "Depósito realizado com sucesso!");
                new ClienteHome(nomeUsuario,false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, insira um valor válido.");
            }
        });
    }

    private void adicionarSaldoUsuario(String nomeUsuario, double valorDeposito) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            // Primeiro, obter o saldo atual do usuário
            String sqlObterSaldo = "SELECT saldo FROM usuario WHERE nome = ?";
            PreparedStatement statementObterSaldo = conexao.prepareStatement(sqlObterSaldo);
            statementObterSaldo.setString(1, nomeUsuario);
            ResultSet resultSet = statementObterSaldo.executeQuery();

            if (resultSet.next()) {
                double saldoAtual = resultSet.getDouble("saldo");

                // Atualizar o saldo do usuário
                double novoSaldo = saldoAtual + valorDeposito;
                String sqlAtualizarSaldo = "UPDATE usuario SET saldo = ? WHERE nome = ?";
                PreparedStatement statementAtualizarSaldo = conexao.prepareStatement(sqlAtualizarSaldo);
                statementAtualizarSaldo.setDouble(1, novoSaldo);
                statementAtualizarSaldo.setString(2, nomeUsuario);

                statementAtualizarSaldo.executeUpdate();
            } else {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar saldo: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
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
