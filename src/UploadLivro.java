import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UploadLivro extends JFrame {

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

    public UploadLivro(String nomeUsuario) {
        setContentPane(UploadPanel);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Criar um JPopupMenu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem2 = new JMenuItem("Biblioteca Pessoal");
        JMenuItem menuItem3 = new JMenuItem("Sair");

        // Adicionar os itens de menu ao JPopupMenu
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.add(menuItem3);

        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Home(nomeUsuario);
                dispose();
            }
        });

        menuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BibliotecaPessoal(nomeUsuario);
                dispose();
            }
        });

        menuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainInterface();
                dispose();
            }
        });

        opcoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(opcoes, e.getX(), e.getY());
                }
            }
        });

        // Adicionando funcionalidade ao botão de upload
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titulo = tituloInput.getText();
                String autor = autorInput.getText();
                String categoria = categoriaInput.getText();
                String preco = precoInput.getText();

                // Validar entrada
                if (titulo.isEmpty() || autor.isEmpty() || categoria.isEmpty() || preco.isEmpty()) {
                    JOptionPane.showMessageDialog(UploadLivro.this, "Todos os campos devem ser preenchidos!");
                    return;
                }

                // Inserir no banco de dados
                Connection conexao = null;
                try {
                    conexao = Conexao.conectar();
                    cadastrarLivro(conexao, titulo, autor, categoria, preco, nomeUsuario);
                    JOptionPane.showMessageDialog(UploadLivro.this, "Livro cadastrado com sucesso!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(UploadLivro.this, "Erro ao cadastrar livro: " + ex.getMessage());
                } finally {
                    Conexao.fecharConexao(conexao);
                }
            }
        });
    }

    private void cadastrarLivro(Connection conexao, String titulo, String autor, String categoria, String preco, String nomeUsuario) throws SQLException {
        String sql = "INSERT INTO livros (titulo, autor, categoria, preco, id_usuario) VALUES (?, ?, ?, ?, (SELECT id FROM usuario WHERE nome = ?))";
        PreparedStatement statement = conexao.prepareStatement(sql);
        statement.setString(1, titulo);
        statement.setString(2, autor);
        statement.setString(3, categoria);
        statement.setString(4, preco);
        statement.setString(5, nomeUsuario);
        statement.executeUpdate();
    }
}
