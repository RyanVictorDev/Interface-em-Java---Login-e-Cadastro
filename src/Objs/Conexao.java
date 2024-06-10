package Objs;

import java.sql.*;

public class Conexao {
    private static final String URL = "jdbc:postgresql://localhost/saraijavas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aluno";

    public static Connection conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
    }

    public static void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Erro ao fechar conexão com o banco de dados: " + ex.getMessage());
            }
        }
    }

    public static void executarUpdate(String sql) {
        Connection conexao = conectar();
        try {
            Statement statement = conexao.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao executar instrução SQL: " + ex.getMessage());
        } finally {
            fecharConexao(conexao);
        }
    }

    public static void adicionarLivro(int idUsuario, String titulo, String autor, String genero) {
        String sql = "INSERT INTO livros (id_usuario, titulo, autor, genero) VALUES (?, ?, ?, ?)";
        try (Connection conexao = conectar();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setInt(1, idUsuario);
            statement.setString(2, titulo);
            statement.setString(3, autor);
            statement.setString(4, genero);
            statement.executeUpdate();
            System.out.println("Objs.Livro adicionado com sucesso.");
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao adicionar livro: " + ex.getMessage());
        }
    }

    public static ResultSet executarConsulta(String sql) {
        Connection conexao = conectar();
        try {
            Statement statement = conexao.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao executar consulta SQL: " + ex.getMessage());
        }
    }

}
