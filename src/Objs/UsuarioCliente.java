package Objs;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioCliente extends Usuario {

    private Livro[] livros;
    private double saldo;

    public UsuarioCliente(String nome, String password) {
        super(nome, password, false);
    }

    // Getter e Setter para livros
    public Livro[] getLivros() {
        return livros;
    }

    public void setLivros(Livro[] livros) {
        this.livros = livros;
    }

    // Getter e Setter para saldo
    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
        atualizarSaldoUsuario(this.getNome(), saldo);
    }

    // Getter e Setter para nome
    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) {
        atualizarNomeUsuario(this.nome, novoNome);
        this.nome = novoNome;
    }

    // Getter e Setter para senha
    public String getPassword() {
        return password;
    }

    public void setPassword(String novaSenha) {
        atualizarSenhaUsuario(this.nome, novaSenha);
        this.password = novaSenha;
    }

    // Getter para ID
    public int getId() {
        return obterIdUsuario(this.nome);
    }

    public void adicionarLivro(String titulo, String autor, String genero) {
        Conexao.adicionarLivro(this.getId(), titulo, autor, genero);
    }

    public ResultSet listarLivros(Connection conexao) throws SQLException {
        String sql = "SELECT titulo, autor, categoria FROM livros WHERE id_usuario_alugado = ?";
        PreparedStatement statement = conexao.prepareStatement(sql);
        statement.setInt(1, this.getId());
        return statement.executeQuery();
    }

    private double obterSaldoUsuario(String nomeUsuario) {
        Connection conexao = null;
        double saldo = 0;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT saldo FROM usuario WHERE nome = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, nomeUsuario);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                saldo = resultSet.getDouble("saldo");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao obter saldo: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
        return saldo;
    }

    private void atualizarSaldoUsuario(String nomeUsuario, double saldo) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "UPDATE usuario SET saldo = ? WHERE nome = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setDouble(1, saldo);
            statement.setString(2, nomeUsuario);
            statement.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar saldo: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    private void atualizarNomeUsuario(String nomeAtual, String novoNome) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "UPDATE usuario SET nome = ? WHERE nome = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, novoNome);
            statement.setString(2, nomeAtual);
            statement.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar nome: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    private void atualizarSenhaUsuario(String nomeUsuario, String novaSenha) {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "UPDATE usuario SET password = ? WHERE nome = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, novaSenha);
            statement.setString(2, nomeUsuario);
            statement.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar senha: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }

    private int obterIdUsuario(String nomeUsuario) {
        Connection conexao = null;
        int id = -1;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT id FROM usuario WHERE nome = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, nomeUsuario);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao obter ID do usuário: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
        return id;
    }
}
