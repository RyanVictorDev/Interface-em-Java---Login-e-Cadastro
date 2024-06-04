import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BibliotecaPessoal extends JFrame {
    private JPanel BibliotecaPanel;
    private JLabel Titulo;
    private JLabel opcoes;
    private JLabel TituloBiblio;
    private JLabel image;
    private JTable tabelaLivros;
    private DefaultTableModel tabelaLivrosModel;

    public BibliotecaPessoal(String nomeUsuario) {
        // Inicializar o painel principal
        BibliotecaPanel = new JPanel();
        BibliotecaPanel.setLayout(new BoxLayout(BibliotecaPanel, BoxLayout.Y_AXIS));
        setContentPane(BibliotecaPanel);
        setSize(600, 400); // Ajuste o tamanho conforme necessário
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Inicializar os componentes
        Titulo = new JLabel("Saraijavas");
        opcoes = new JLabel("Menu");
        TituloBiblio = new JLabel("Livros Alugados");
        image = new JLabel(new ImageIcon("caminho/para/sua/imagem.jpg")); // Ajuste o caminho da imagem

        // Inicializar e configurar a tabela e seu modelo
        tabelaLivrosModel = new DefaultTableModel(new Object[]{"Título", "Autor", "Preço", "Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaLivros = new JTable(tabelaLivrosModel);
        JScrollPane scrollPane = new JScrollPane(tabelaLivros);

        // Adicionar componentes ao painel principal
        BibliotecaPanel.add(Titulo);
        BibliotecaPanel.add(opcoes);
        BibliotecaPanel.add(TituloBiblio);
        BibliotecaPanel.add(image);
        BibliotecaPanel.add(scrollPane);

        // Carregar livros alugados pelo usuário ao criar a biblioteca pessoal
        carregarLivrosAlugados(nomeUsuario);

        // Criar um JPopupMenu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem2 = new JMenuItem("Biblioteca Pessoal");
        JMenuItem menuItem3 = new JMenuItem("Sair");

        // Adicionar os itens de menu ao JPopupMenu
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.add(menuItem3);

        // Adicionar ActionListeners aos itens de menu
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

        // Adicionar um ouvinte de mouse à JLabel "opcoes" para exibir o menu pop-up quando o botão direito do mouse for clicado
        opcoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(opcoes, e.getX(), e.getY());
                }
            }
        });
    }

    private void carregarLivrosAlugados(String nomeUsuario) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            int usuarioId = obterIdUsuario(conexao, nomeUsuario);
            if (usuarioId == -1) {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
                return;
            }

            String sql = "SELECT titulo, autor, preco, categoria FROM livros WHERE id_usuario_alugado = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();

            tabelaLivrosModel.setRowCount(0); // Limpar a tabela antes de adicionar novos dados

            while (resultSet.next()) {
                String titulo = resultSet.getString("titulo");
                String autor = resultSet.getString("autor");
                double preco = resultSet.getDouble("preco");
                String categoria = resultSet.getString("categoria");
                tabelaLivrosModel.addRow(new Object[]{titulo, autor, preco, categoria});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar livros: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    private int obterIdUsuario(Connection conexao, String nomeUsuario) throws SQLException {
        String sql = "SELECT id FROM usuario WHERE nome = ?";
        PreparedStatement statement = conexao.prepareStatement(sql);
        statement.setString(1, nomeUsuario);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("id");
        } else {
            return -1; // Usuário não encontrado
        }
    }
}
