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
                return;
            }

            Connection conexao = null;
            try {
                conexao = Conexao.conectar();
                if (verificarUsuarioExistente(conexao, nome)) {
                    JOptionPane.showMessageDialog(Cadastro.this, "Usuário já existe!");
                    return;
                }

                cadastrarNovoUsuario(conexao, nome, senha);
                JOptionPane.showMessageDialog(Cadastro.this, "Usuário cadastrado com sucesso!");
                new ClienteHome(nome, false);
                dispose();
            } catch (SQLException ex) {
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
}
