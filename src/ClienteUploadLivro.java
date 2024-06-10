import Objs.Conexao;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteUploadLivro extends JFrame {

    private JPanel UploadPanel;
    private JLabel Titulo;
    private JLabel opcoes;
    private JLabel TituloUpload;
    private JTextField tituloInput;
    private JLabel preco;
    private JLabel categoria;
    private JLabel autor;
    private JLabel tituloLivro;
    private JTextField autorInput;
    private JTextField categoriaInput;
    private JTextField precoInput;
    private JButton uploadButton;

    public ClienteUploadLivro(String nomeUsuario) {
        setContentPane(UploadPanel);
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

        opcoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(opcoes, e.getX(), e.getY());
                }
            }
        });

        // Adicionando funcionalidade ao botão de upload
        uploadButton.addActionListener(e -> {
            String titulo = tituloInput.getText();
            String autor = autorInput.getText();
            String categoria = categoriaInput.getText();
            double preco = Double.parseDouble(precoInput.getText());

            // Validar entrada
            if (titulo.isEmpty() || autor.isEmpty() || categoria.isEmpty() || precoInput.getText().isEmpty()) {
                JOptionPane.showMessageDialog(ClienteUploadLivro.this, "Todos os campos devem ser preenchidos!");
                return;
            }

            // Inserir no banco de dados
            Connection conexao = null;
            try {
                conexao = Conexao.conectar();
                cadastrarLivro(conexao, titulo, autor, categoria, preco, nomeUsuario);
                JOptionPane.showMessageDialog(ClienteUploadLivro.this, "Livro cadastrado  com sucesso!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(ClienteUploadLivro.this, "Erro ao cadastrar livro: " + ex.getMessage());
            } finally {
                Conexao.fecharConexao(conexao);
            }
        });
    }

    private void cadastrarLivro(Connection conexao, String titulo, String autor, String categoria, double preco, String nomeUsuario) throws SQLException {
        String sql = "INSERT INTO livros (titulo, autor, categoria, preco, id_usuario) VALUES (?, ?, ?, ?, (SELECT id FROM usuario WHERE nome = ?))";
        PreparedStatement statement = conexao.prepareStatement(sql);
        statement.setString(1, titulo);
        statement.setString(2, autor);
        statement.setString(3, categoria);
        statement.setDouble(4, preco);
        statement.setString(5, nomeUsuario);
        statement.executeUpdate();
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
