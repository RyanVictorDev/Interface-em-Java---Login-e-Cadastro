import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        ENTRARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = textField1.getText();
                String senha = String.valueOf(passwordField1.getPassword());

                Connection conexao = null;
                try {
                    conexao = Conexao.conectar();
                    if (verificarCredenciais(conexao, nome, senha)) {
                        JOptionPane.showMessageDialog(Login.this, "Login bem-sucedido!");
                        new Home(nome);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(Login.this, "Credenciais inválidas. Tente novamente.");
                    }
                } finally {
                    Conexao.fecharConexao(conexao);
                }
            }
        });
    }

    private boolean verificarCredenciais(Connection conexao, String nome, String senha) {
        try {
            String sql = "SELECT * FROM usuario WHERE nome = ? AND password = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, nome);
            statement.setString(2, senha);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao verificar credenciais: " + ex.getMessage());
        }
    }
}
