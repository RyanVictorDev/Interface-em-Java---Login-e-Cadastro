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

public  class BibliotecaBase extends JFrame {
    protected JPanel BibliotecaPanel;
    protected JLabel Titulo;
    protected JLabel opcoes;
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
        BibliotecaPanel.setLayout(new BoxLayout(BibliotecaPanel, BoxLayout.Y_AXIS));
        setContentPane(BibliotecaPanel);
        setSize(600, 400); // Ajuste o tamanho conforme necessário
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Criar instância de UsuarioCliente
        usuarioCliente = new UsuarioCliente(nomeUsuario, "senha"); // Ajuste conforme necessário para obter a senha

        // Criar e configurar o menu pop-up
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem2 = new JMenuItem("Biblioteca Pessoal");
        JMenuItem menuItem3 = new JMenuItem("Saldo: R$ " + usuarioCliente.getSaldo()); // Exibe o saldo atual
        JMenuItem menuItem4 = new JMenuItem("Sair");

        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.add(menuItem3);
        popupMenu.add(menuItem4);

        menuItem1.addActionListener(e -> {
            new ClienteHome(nomeUsuario,isAdmin);
            dispose();
        });

        menuItem2.addActionListener(e -> {
            new BibliotecaBase(nomeUsuario, isAdmin);
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

        // Adicionar um ouvinte de mouse à JLabel "opcoes" para exibir o menu pop-up quando o botão direito do mouse for clicado
        opcoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(opcoes, e.getX(), e.getY());
                }
            }
        });

        // Criar um JPanel para Titulo e opcoes
        tituloOpcoesPanel = new JPanel(new BorderLayout());
        tituloOpcoesPanel.add(Titulo, BorderLayout.WEST);
        tituloOpcoesPanel.add(opcoes, BorderLayout.EAST);

        if (isAdmin) {
            initComponents(); // Inicializar componentes do admin

            // Carregar todos os livros do banco de dados
            carregarTodosLivros();
        } else {
            initComponentsCliente(); // Inicializar componentes do cliente
        }
    }

    protected void initComponents() {
        // Inicializar componentes do admin
        btnAlterarStatus = new JButton("Alterar Status");
        btnAlterarStatus.addActionListener(e -> {
            // Aqui você pode adicionar a lógica para alterar o status dos livros selecionados
            // Por exemplo:
            int[] selectedRows = tabelaLivros.getSelectedRows();
            for (int row : selectedRows) {
                // Obtenha o ID do livro na linha selecionada
                int livroId = (int) tabelaLivros.getValueAt(row, 0); // Supondo que o ID esteja na primeira coluna
                // Defina o status is_deleted como verdadeiro para o livro com o ID correspondente
                definirStatusLivro(livroId, true);
            }
        });

        // Adicionar o botão ao painel principal
        BibliotecaPanel.add(btnAlterarStatus, BorderLayout.SOUTH);
    }

    protected void initComponentsCliente() {
        // Inicializar componentes do cliente
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
        BibliotecaPanel.add(tituloOpcoesPanel);
        BibliotecaPanel.add(TituloBiblio);
        BibliotecaPanel.add(image);
        BibliotecaPanel.add(scrollPane);

        // Carregar livros alugados pelo usuário ao criar a biblioteca pessoal
        carregarLivrosAlugados();
    }

    private void carregarTodosLivros() {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            ResultSet rs = listarTodosLivros(conexao);
            while (rs.next()) {
                // Criar um objeto Livro com os dados do resultado da consulta
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAutor(rs.getString("autor"));
                livro.setCategoria(rs.getString("categoria"));
                livro.setValor(rs.getDouble("preco"));

                // Adicionar o livro à tabela
                tabelaLivrosModel.addRow(new Object[]{livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getValor(), livro.getCategoria()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar livros: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }
    protected ResultSet listarTodosLivros(Connection conexao) throws SQLException {
        // Defina a consulta SQL para listar todos os livros
        String sql = "SELECT * FROM livros";
        // Crie uma declaração preparada
        PreparedStatement statement = conexao.prepareStatement(sql);
        // Execute a consulta e retorne o conjunto de resultados
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
                double preco = rs.getDouble("preco");
                livro.setValor(preco);

                // Adicionar o livro à tabela
                tabelaLivrosModel.addRow(new Object[]{livro.getTitulo(), livro.getAutor(),
                        livro.getValor(), livro.getCategoria()});
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
            return linhasAfetadas > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar status do livro: " + ex.getMessage());
            return false;
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }
}
