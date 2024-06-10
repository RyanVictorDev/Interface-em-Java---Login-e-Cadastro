import Objs.Conexao;
import Objs.Livro;
import Objs.UsuarioCliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BibliotecaBase extends JFrame {
    protected JPanel BibliotecaPanel;
    protected JLabel Titulo;
    protected JLabel opcoes;
    private JToolBar navToolBar;
    protected JLabel TituloBiblio;
    protected JLabel image;
    protected JTable tabelaLivros;
    protected DefaultTableModel tabelaLivrosModel;
    protected UsuarioCliente usuarioCliente;
    protected JButton btnAlterarStatus;
    protected JPanel tituloOpcoesPanel;

    public BibliotecaBase(String nomeUsuario, boolean isAdmin) {
        // Inicializar o painel principal
        BibliotecaPanel = new JPanel();
        BibliotecaPanel.setLayout(new BorderLayout());
        setContentPane(BibliotecaPanel);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Inicializar os componentes
        Titulo = new JLabel("Saraijavas");
        opcoes = new JLabel("Menu");
        TituloBiblio = new JLabel();
        tabelaLivrosModel = new DefaultTableModel(new Object[]{"ID", "Título", "Autor", "Preço", "Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaLivros = new JTable(tabelaLivrosModel);
        JScrollPane scrollPane = new JScrollPane(tabelaLivros);

        // Adicionar componentes ao painel
        tituloOpcoesPanel = new JPanel(new BorderLayout());
        tituloOpcoesPanel.add(Titulo, BorderLayout.WEST);
        tituloOpcoesPanel.add(opcoes, BorderLayout.EAST);
        BibliotecaPanel.add(tituloOpcoesPanel, BorderLayout.NORTH);
        BibliotecaPanel.add(scrollPane, BorderLayout.CENTER);

        // Criar instância de UsuarioCliente
        usuarioCliente = new UsuarioCliente(nomeUsuario, "senha"); // Ajuste conforme necessário para obter a senha

        // Criar e configurar o menu pop-up
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem4 = new JMenuItem("Sair");

        popupMenu.add(menuItem1);
        popupMenu.add(menuItem4);

        if(!isAdmin){
            menuItem1.addActionListener(e -> {
                new ClienteHome(nomeUsuario,false);
                dispose();
            });
        }
        else{
            menuItem1.addActionListener(e -> {
            new AdminHome(nomeUsuario);
            dispose();
        });
        }

        menuItem4.addActionListener(e -> {
            new MainInterface();
            dispose();
        });

// Adicionar as opções específicas do cliente (não administrador)
        if (!isAdmin) {
            JMenuItem menuItem2 = new JMenuItem("Biblioteca Pessoal");
            JMenuItem menuItem3 = new JMenuItem("Saldo: R$ " + usuarioCliente.getSaldo());

            popupMenu.add(menuItem2);
            popupMenu.add(menuItem3);

            menuItem2.addActionListener(e -> {
                new BibliotecaBase(nomeUsuario, isAdmin);
                dispose();
            });

            menuItem3.addActionListener(e -> {
                new ClienteDeposito(nomeUsuario);
                dispose();
            });
        }


        // Adicionar um ouvinte de mouse à JLabel "opcoes" para exibir o menu pop-up quando o botão direito do mouse for clicado
        opcoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(opcoes, e.getX(), e.getY());
                }
            }
        });

        if (isAdmin) {
            initComponents(); // Inicializar componentes do admin
            carregarTodosLivros();
        } else {
            initComponents(); // Inicializar componentes do admin
            carregarLivrosAlugados();
        }
    }

    protected void initComponents() {
        // Verifica se o BibliotecaPanel foi inicializado corretamente
        if (BibliotecaPanel == null) {
            BibliotecaPanel = new JPanel();
            BibliotecaPanel.setLayout(new BorderLayout());
            setContentPane(BibliotecaPanel);
        }

        btnAlterarStatus = new JButton("Alterar Status");
        btnAlterarStatus.addActionListener(e -> {
            int[] selectedRows = tabelaLivros.getSelectedRows();
            for (int row : selectedRows) {
                int livroId = (int) tabelaLivros.getValueAt(row, 0); // Supondo que o ID esteja na primeira coluna
                definirStatusLivro(livroId, true);
            }
        });

        // Verifica se o layout do BibliotecaPanel está definido corretamente
        if (BibliotecaPanel.getLayout() instanceof BorderLayout) {
            // Adiciona o botão btnAlterarStatus ao sul (SOUTH) do BibliotecaPanel
            BibliotecaPanel.add(btnAlterarStatus, BorderLayout.SOUTH);
        } else {
            // Caso o layout não seja BorderLayout, adiciona o botão ao final do BibliotecaPanel
            BibliotecaPanel.add(btnAlterarStatus);
        }
    }


    private void carregarTodosLivros() {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            ResultSet rs = listarTodosLivros(conexao);
            while (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAutor(rs.getString("autor"));
                livro.setCategoria(rs.getString("categoria"));
                livro.setValor(rs.getDouble("preco"));

                tabelaLivrosModel.addRow(new Object[]{livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getValor(), livro.getCategoria()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar livros: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    protected ResultSet listarTodosLivros(Connection conexao) throws SQLException {
        String sql = "SELECT * FROM livros";
        PreparedStatement statement = conexao.prepareStatement(sql);
        return statement.executeQuery();
    }

    protected void carregarLivrosAlugados() {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            ResultSet rs = usuarioCliente.listarLivros(conexao);
            while (rs.next()) {
                Livro livro = new Livro();
                livro.setTitulo(rs.getString("titulo"));
                livro.setAutor(rs.getString("autor"));
                livro.setCategoria(rs.getString("categoria"));

                // Verificar se o valor da coluna "preco" é null
                double preco = rs.getDouble("preco");
                if (!rs.wasNull()) {
                    livro.setValor(preco);
                } else {
                    // Definir um valor padrão ou lidar com a situação de valor null conforme necessário
                    livro.setValor(0.0); // Por exemplo, definindo o valor como 0.0
                }

                tabelaLivrosModel.addRow(new Object[]{livro.getTitulo(), livro.getAutor(), livro.getValor(), livro.getCategoria()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar livros: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }



    protected boolean definirStatusLivro(int livroId, boolean isDeleted) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "UPDATE livros SET is_deleted = ? WHERE id = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setBoolean(1, isDeleted);
            statement.setInt(2, livroId);
            int linhasAfetadas = statement.executeUpdate();

            // Se a atualização foi bem-sucedida e o livro foi marcado como excluído,
            // remova a linha correspondente da tabela
            if (linhasAfetadas > 0 && isDeleted) {
                // Encontra a linha na tabela que corresponde ao livroId
                for (int i = 0; i < tabelaLivros.getRowCount(); i++) {
                    if ((int) tabelaLivros.getValueAt(i, 0) == livroId) { // Assumindo que o ID do livro está na primeira coluna
                        // Remove a linha da tabela
                        tabelaLivrosModel.removeRow(i);
                        break; // Uma vez que a linha foi removida, não há necessidade de continuar procurando
                    }
                }
            }

            return linhasAfetadas > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar status do livro: " + ex.getMessage());
            return false;
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BibliotecaBase("Admin", false));
    }
}
