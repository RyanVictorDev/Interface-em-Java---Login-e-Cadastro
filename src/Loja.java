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
    private JButton ButaoAlugar;
    private DefaultTableModel tabelaLivrosModel;

    private String nomeUsuario;
    private int idLivroSelecionado = -1; // Variável para armazenar o ID do livro selecionado

    public Loja(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;

        // Configurar JFrame
        setContentPane(section);
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Inicializar a tabela e seu modelo
        tabelaLivrosModel = new DefaultTableModel(new Object[]{"ID", "Título", "Autor", "Preço", "Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Tornar todas as células não editáveis
                return false;
            }
        };
        tabelaLivros.setModel(tabelaLivrosModel);

        // Ocultar a coluna ID da tabela
        tabelaLivros.removeColumn(tabelaLivros.getColumnModel().getColumn(0));

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

        // Adicionar um ouvinte de mouse à tabela para capturar a seleção do livro
        tabelaLivros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabelaLivros.getSelectedRow();
                if (row != -1) {
                    idLivroSelecionado = (int) tabelaLivrosModel.getValueAt(row, 0); // Obter o ID do livro da coluna oculta
                }
            }
        });

        // Adicionar ActionListener ao botão "ButaoAlugar"
        ButaoAlugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idLivroSelecionado != -1) {
                    alugarLivro(idLivroSelecionado, nomeUsuario);
                } else {
                    JOptionPane.showMessageDialog(Loja.this, "Por favor, selecione um livro para alugar.");
                }
            }
        });

        carregarLivrosDisponiveis();
    }

    private void carregarLivrosDisponiveis() {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT id, titulo, autor, preco, categoria FROM livros";
            PreparedStatement statement = conexao.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            tabelaLivrosModel.setRowCount(0); // Limpar a tabela antes de adicionar novos dados

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String titulo = resultSet.getString("titulo");
                String autor = resultSet.getString("autor");
                double preco = resultSet.getDouble("preco");
                String categoria = resultSet.getString("categoria");
                tabelaLivrosModel.addRow(new Object[]{id, titulo, autor, preco, categoria});
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
            String sql = "SELECT id, titulo, autor, preco, categoria FROM livros WHERE titulo LIKE ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, "%" + termoPesquisa + "%");
            ResultSet resultSet = statement.executeQuery();

            tabelaLivrosModel.setRowCount(0); // Limpar a tabela antes de adicionar novos dados

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String titulo = resultSet.getString("titulo");
                String autor = resultSet.getString("autor");
                double preco = resultSet.getDouble("preco");
                String categoria = resultSet.getString("categoria");
                tabelaLivrosModel.addRow(new Object[]{id, titulo, autor, preco, categoria});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao pesquisar livros: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    private void alugarLivro(int idLivro, String nomeUsuario) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            // Supondo que você tenha uma forma de obter o ID do usuário pelo nome de usuário
            int idUsuario = obterIdUsuario(nomeUsuario);

            String sql = "UPDATE livros SET id_usuario_alugado = ? WHERE id = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setInt(1, idUsuario);
            statement.setInt(2, idLivro);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Livro alugado com sucesso!");
                carregarLivrosDisponiveis(); // Atualizar a lista de livros
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao alugar o livro.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alugar livro: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    private int obterIdUsuario(String nomeUsuario) {
        // Implementar a lógica para obter o ID do usuário a partir do nome de usuário
        // Esta função deve consultar o banco de dados e retornar o ID correspondente
        // Exemplo:
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT id FROM usuario WHERE nome = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, nomeUsuario);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("Usuário não encontrado: " + nomeUsuario);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao obter ID do usuário: " + ex.getMessage());
            return -1;
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }
}
