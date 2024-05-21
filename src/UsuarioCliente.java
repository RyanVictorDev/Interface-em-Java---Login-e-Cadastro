import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioCliente extends Usuario {

    private Livro[] livros;

    public UsuarioCliente(String nome, String password) {
        super(nome,password);
        this.livros = new Livro[32];
    }

    public void adicionarLivro(String titulo, String autor, String genero) {
        Conexao.adicionarLivro(this.getId(), titulo, autor, genero);
    }

    public void listarLivros() {
        String sql = "SELECT * FROM livros WHERE id = " + this.getId();
        ResultSet rs = Conexao.executarConsulta(sql);
        try {
            while (rs.next()) {
                System.out.println("Título: " + rs.getString("titulo"));
                System.out.println("Autor: " + rs.getString("autor"));
                System.out.println("Gênero: " + rs.getString("genero"));
                System.out.println("-----------------------------");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar livros: " + ex.getMessage());
        } finally {
            try {
                Conexao.fecharConexao(rs.getStatement().getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
