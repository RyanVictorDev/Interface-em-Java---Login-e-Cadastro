import Objs.Conexao;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.*;

public class ClienteLoja extends JFrame {
    private JPanel section;
    private JLabel Titulo;
    private JLabel opcoes;
    private JTextField campoPesquisa;
    private JTable tabelaLivros;
    private JButton ButaoAlugar;
    private DefaultTableModel tabelaLivrosModel;

    private String nomeUsuario;
    private int idLivroSelecionado = -1; // Variável para armazenar o ID do livro selecionado

    public ClienteLoja(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;

        // Configurar JFrame
        setContentPane(section);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Home");
        JMenuItem menuItem2 = new JMenuItem("Biblioteca Pessoal");
        JMenuItem menuItem3 = new JMenuItem("Saldo: R$");
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

        // Adicionar um ouvinte de mouse à JLabel "opcoes" para exibir o menu pop-up quando o botão direito do mouse for clicado
        opcoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(opcoes, e.getX(), e.getY());
                }
            }
        });

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

        // Adicionar ActionListener para o campo de pesquisa
        campoPesquisa.addActionListener(e -> {
            String termoPesquisa = campoPesquisa.getText();
            pesquisarLivros(termoPesquisa);
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
        ButaoAlugar.addActionListener(e -> {
            if (idLivroSelecionado != -1) {
                alugarLivro(idLivroSelecionado, nomeUsuario);
            } else {
                JOptionPane.showMessageDialog(ClienteLoja.this, "Por favor, selecione um livro para alugar.");
            }
        });

        carregarLivrosDisponiveis();
    }

    private void carregarLivrosDisponiveis() {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT id, titulo, autor, preco, categoria FROM livros WHERE id_usuario_alugado IS NULL";
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
            int idUsuario = obterIdUsuario(nomeUsuario);

            String sql = "UPDATE livros SET id_usuario_alugado = ? WHERE id = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setInt(1, idUsuario);
            statement.setInt(2, idLivro);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Objs.Livro alugado com sucesso!");
                carregarLivrosDisponiveis(); // Atualizar a lista de livros
                gerarNotaFiscal(idLivro, nomeUsuario); // Gerar nota fiscal
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

    private void gerarNotaFiscal(int idLivro, String nomeUsuario) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sqlLivro = "SELECT titulo, autor, preco FROM livros WHERE id = ?";
            PreparedStatement statementLivro = conexao.prepareStatement(sqlLivro);
            statementLivro.setInt(1, idLivro);
            ResultSet resultSetLivro = statementLivro.executeQuery();

            if (resultSetLivro.next()) {
                String titulo = resultSetLivro.getString("titulo");
                String autor = resultSetLivro.getString("autor");
                double preco = resultSetLivro.getDouble("preco");

                // Inserir a transação e obter o ID da transação
                String sqlTransacao = "INSERT INTO transacoes (id_livro, nome_usuario, preco) VALUES (?, ?, ?)";
                PreparedStatement statementTransacao = conexao.prepareStatement(sqlTransacao, Statement.RETURN_GENERATED_KEYS);
                statementTransacao.setInt(1, idLivro);
                statementTransacao.setString(2, nomeUsuario);
                statementTransacao.setDouble(3, preco);
                statementTransacao.executeUpdate();
                ResultSet resultSetTransacao = statementTransacao.getGeneratedKeys();
                int idTransacao = 0;
                if (resultSetTransacao.next()) {
                    idTransacao = resultSetTransacao.getInt(1);
                }

                // Gerar o nome do arquivo com o ID da transação
                String nomeArquivo = "NotaFiscal" + idTransacao + ".pdf";

                Document document = new Document();
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
                    document.open();
                    document.add(new Paragraph("Nota Fiscal"));
                    document.add(new Paragraph("Nome do Usuário: " + nomeUsuario));
                    document.add(new Paragraph("ID do Objs.Livro: " + idLivro));
                    document.add(new Paragraph("Título: " + titulo));
                    document.add(new Paragraph("Autor: " + autor));
                    document.add(new Paragraph("Preço: " + preco));
                    document.add(new Paragraph("ID da Transação: " + idTransacao));
                    document.close();
                    JOptionPane.showMessageDialog(this, "Nota fiscal gerada com sucesso!");
                } catch (FileNotFoundException | DocumentException e) {
                    JOptionPane.showMessageDialog(this, "Erro ao gerar nota fiscal: " + e.getMessage());
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar nota fiscal: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

}
