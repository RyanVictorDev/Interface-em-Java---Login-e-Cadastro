package Objs;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Usuario {
    protected String nome;
    protected String password;
    protected int id;
    protected boolean isAdmin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Usuario(String nome, String password, boolean isAdmin) {
        this.nome = nome;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Método para verificar se o usuário é administrador no banco de dados
    public void checkAdminStatus() {
        Connection conexao = null;
        try {
            conexao = Conexao.conectar();
            String sql = "SELECT is_admin FROM usuario WHERE nome = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, nome);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.isAdmin = resultSet.getBoolean("is_admin");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao verificar privilégios de administrador: " + ex.getMessage());
        } finally {
            Conexao.fecharConexao(conexao);
        }
    }
}
