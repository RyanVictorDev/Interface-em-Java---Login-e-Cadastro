import Objs.Conexao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class AdminConsultaUsuario extends JFrame {
    private JPanel section;
    private JLabel Titulo;
    private JLabel opcoes;
    private JTextField campoPesquisa;
    private JTable tabelaUsuarios;
    private JButton botaoApagar;
    private DefaultTableModel tabelaUsuariosModel;

    private String nomeUsuario;
    private int idUsuarioSelecionado = -1; // Variável para armazenar o ID do usuário selecionado

    public AdminConsultaUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;

        // Inicializa section, que deve ser criado no arquivo .form correspondente
        initComponents();

        // Configurar JFrame
        setContentPane(section);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem2 = new JMenuItem("Sair");

        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);

        menuItem1.addActionListener(e -> {
            new AdminHome(nomeUsuario);
            dispose();
        });

        menuItem2.addActionListener(e -> {
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

        tabelaUsuariosModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaUsuarios.setModel(tabelaUsuariosModel);

        // Esconder a coluna ID
        tabelaUsuarios.removeColumn(tabelaUsuarios.getColumnModel().getColumn(0));

        campoPesquisa.addActionListener(e -> {
            String termoPesquisa = campoPesquisa.getText();
            pesquisarUsuarios(termoPesquisa);
        });

        tabelaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabelaUsuarios.getSelectedRow();
                if (row != -1) {
                    idUsuarioSelecionado = (int) tabelaUsuariosModel.getValueAt(row, 0);
                }
            }
        });

        botaoApagar.addActionListener(e -> {
            if (idUsuarioSelecionado != -1) {
                apagarUsuario(idUsuarioSelecionado);
            } else {
                JOptionPane.showMessageDialog(AdminConsultaUsuario.this, "Por favor, selecione um usuário para apagar.");
            }
        });

        carregarUsuarios();
    }

    private void initComponents() {
        // Método gerado automaticamente pelo GUI designer para inicializar componentes
        // e garantir que section é inicializado
    }

    private void carregarUsuarios() {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT id, nome FROM usuario";
            PreparedStatement statement = conexao.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            tabelaUsuariosModel.setRowCount(0);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                tabelaUsuariosModel.addRow(new Object[]{id, nome});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    private void pesquisarUsuarios(String termoPesquisa) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT id, nome FROM usuario WHERE nome LIKE ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, "%" + termoPesquisa + "%");
            ResultSet resultSet = statement.executeQuery();

            tabelaUsuariosModel.setRowCount(0);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                tabelaUsuariosModel.addRow(new Object[]{id, nome});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao pesquisar usuários: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    private void apagarUsuario(int idUsuario) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();

            // Atualizar os livros alugados pelo usuário para definir id_usuario_alugado como NULL
            String sqlAtualizarLivros = "UPDATE livros SET id_usuario_alugado = NULL WHERE id_usuario_alugado = ?";
            PreparedStatement statementAtualizarLivros = conexao.prepareStatement(sqlAtualizarLivros);
            statementAtualizarLivros.setInt(1, idUsuario);
            statementAtualizarLivros.executeUpdate();

            // Atualizar os livros para definir id_usuario como NULL
            String sqlAtualizarLivros1 = "UPDATE livros SET id_usuario = NULL WHERE id_usuario = ?";
            PreparedStatement statementAtualizarLivros1 = conexao.prepareStatement(sqlAtualizarLivros1);
            statementAtualizarLivros1.setInt(1, idUsuario);
            statementAtualizarLivros1.executeUpdate();

            // Apagar o usuário
            String sqlApagarUsuario = "DELETE FROM usuario WHERE id = ?";
            PreparedStatement statementApagarUsuario = conexao.prepareStatement(sqlApagarUsuario);
            statementApagarUsuario.setInt(1, idUsuario);

            int rowsDeleted = statementApagarUsuario.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Usuário apagado com sucesso!");
                carregarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao apagar o usuário.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao apagar usuário: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }
}
