import Objs.Conexao;
import Objs.Usuario;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {

    private JPanel Login;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton ENTRARButton;

    public Login() {
        setContentPane(Login);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        ENTRARButton.addActionListener(e -> {
            String nome = textField1.getText();
            String senha = String.valueOf(passwordField1.getPassword());

            Connection conexao = null;
            try {
                conexao = Conexao.conectar();
                Usuario usuario = verificarCredenciais(conexao, nome, senha);
                String resultado;
                if (usuario != null) {
                    resultado = "sucesso";
                    JOptionPane.showMessageDialog(Login.this, "Login bem-sucedido!");
                    if (usuario.getIsAdmin()) {
                        new AdminHome(nome);
                    } else {
                        new ClienteHome(nome, false);
                    }
                    dispose(); // Fecha a janela atual
                } else {
                    resultado = "falha";
                    JOptionPane.showMessageDialog(Login.this, "Credenciais inválidas. Tente novamente.");
                }
                registrarLogEvento(conexao, nome, "login", resultado, "127.0.0.1"); // Exemplo de IP estático
            } finally {
                Conexao.fecharConexao(conexao);
            }
        });
    }

    private Usuario verificarCredenciais(Connection conexao, String nome, String senha) {
        try {
            String sql = "SELECT * FROM usuario WHERE nome = ? AND password = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, nome);
            statement.setString(2, senha);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Usuario usuario = new Usuario(
                        resultSet.getString("nome"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("is_admin") // Passando o valor de isAdmin
                );
                usuario.setId(resultSet.getInt("id"));
                return usuario;
            }
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao verificar credenciais: " + ex.getMessage());
        }
    }

    private void registrarLogEvento(Connection conexao, String nomeUsuario, String evento, String resultado, String ipUsuario) {
        try {
            String sql = "INSERT INTO log_eventos (id_usuario, evento, resultado, data_evento, ip_usuario) " +
                    "VALUES ((SELECT id FROM usuario WHERE nome = ?), ?, ?, CURRENT_TIMESTAMP, ?)";
            PreparedStatement statement = conexao.prepareStatement(sql);
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
        SwingUtilities.invokeLater(Login::new);
    }
}
