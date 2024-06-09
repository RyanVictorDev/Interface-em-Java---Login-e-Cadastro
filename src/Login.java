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
                if (usuario != null) {
                    JOptionPane.showMessageDialog(Login.this, "Login bem-sucedido!");
                    if (usuario.getIsAdmin()) {
                        new AdminHome(nome);
                    } else {
                        new ClienteHome(nome,false);
                    }
                    dispose(); // Fecha a janela atual
                } else {
                    JOptionPane.showMessageDialog(Login.this, "Credenciais inválidas. Tente novamente.");
                }
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

}
