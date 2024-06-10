import Objs.Conexao;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cadastro extends JFrame {

    private JPanel Cadastro;
    private JTextField adicioneSeuNomeTextField;
    private JPasswordField senhaPasswordField;
    private JPasswordField passwordField1;
    private JButton CADASTRARButton;

    public Cadastro() {
        setContentPane(Cadastro);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        CADASTRARButton.addActionListener(e -> {
            String nome = adicioneSeuNomeTextField.getText();
            String senha = String.valueOf(senhaPasswordField.getPassword());
            String confirmacaoSenha = String.valueOf(passwordField1.getPassword());

            if (!senha.equals(confirmacaoSenha)) {
                JOptionPane.showMessageDialog(Cadastro.this, "As senhas não coincidem!");
                registrarLogEvento(null, nome, "cadastro", "falha", "127.0.0.1"); // Exemplo de IP estático
                return;
            }

            Connection conexao = null;
            try {
                conexao = Conexao.conectar();
                if (verificarUsuarioExistente(conexao, nome)) {
                    JOptionPane.showMessageDialog(Cadastro.this, "Usuário já existe!");
                    registrarLogEvento(conexao, nome, "cadastro", "falha", "127.0.0.1"); // Exemplo de IP estático
                    return;
                }

                cadastrarNovoUsuario(conexao, nome, senha);
                JOptionPane.showMessageDialog(Cadastro.this, "Usuário cadastrado com sucesso!");
                registrarLogEvento(conexao, nome, "cadastro", "sucesso", "127.0.0.1"); // Exemplo de IP estático
                new ClienteHome(nome, false);
                dispose();
            } catch (SQLException ex) {
                registrarLogEvento(conexao, nome, "cadastro", "falha", "127.0.0.1"); // Exemplo de IP estático
                throw new RuntimeException("Erro ao cadastrar novo usuário: " + ex.getMessage());
            } finally {
                Conexao.fecharConexao(conexao);
            }
        });
    }

    private boolean verificarUsuarioExistente(Connection conexao, String nome) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE nome = ?";
        PreparedStatement statement = conexao.prepareStatement(sql);
        statement.setString(1, nome);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    private void cadastrarNovoUsuario(Connection conexao, String nome, String senha) throws SQLException {
        String sql = "INSERT INTO usuario (nome, password) VALUES (?, ?)";
        PreparedStatement statement = conexao.prepareStatement(sql);
        statement.setString(1, nome);
        statement.setString(2, senha);
        statement.executeUpdate();
    }

    private void registrarLogEvento(Connection conexao, String nomeUsuario, String evento, String resultado, String ipUsuario) {
        try {
            String sql = "INSERT INTO log_eventos (id_usuario, evento, resultado, data_evento, ip_usuario) " +
                    "VALUES ((SELECT id FROM usuario WHERE nome = ?), ?, ?, CURRENT_TIMESTAMP, ?)";
            PreparedStatement statement = (conexao != null) ? conexao.prepareStatement(sql) : Conexao.conectar().prepareStatement(sql);
            statement.setString(1, nomeUsuario);
            statement.setString(2, evento);
            statement.setString(3, resultado);
            statement.setString(4, ipUsuario);
            statement.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar log de evento: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Cadastro::new);
    }
}
