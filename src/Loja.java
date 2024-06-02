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

public class Loja extends JFrame {
    private JPanel section;
    private JLabel Titulo;
    private JLabel opcoes;
    private JTextField campoPesquisa;
    private JTable tabelaLivros;
    private DefaultTableModel tabelaLivrosModel;

    public Loja(String nomeUsuario) {
        // Configurar JFrame
        setContentPane(section);
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Inicializar a tabela e seu modelo
        tabelaLivrosModel = new DefaultTableModel(new Object[]{"Título", "Autor", "Preço", "Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Tornar todas as células não editáveis
                return false;
            }
        };
        tabelaLivros.setModel(tabelaLivrosModel);


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

        // Adicionar ActionListener para o campo de pesquisa
        campoPesquisa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String termoPesquisa = campoPesquisa.getText();
                pesquisarLivros(termoPesquisa);
            }
        });


        carregarLivrosDisponiveis();
    }

    private void carregarLivrosDisponiveis() {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT titulo, autor, preco, categoria FROM livros";
            PreparedStatement statement = conexao.prepareStatement(sql);
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
    private void pesquisarLivros(String termoPesquisa) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT titulo, autor, preco, categoria FROM livros WHERE titulo LIKE ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, "%" + termoPesquisa + "%");
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
            JOptionPane.showMessageDialog(this, "Erro ao pesquisar livros: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
}}
