public class UsuarioAdmin extends Usuario {

    public UsuarioAdmin(String nome, String password) {
        super(nome, password);
    }

    public void deletarLivro(int idLivro) {
        String sql = "DELETE FROM livros WHERE id = " + idLivro;
        Conexao.executarUpdate(sql);
        System.out.println("Livro deletado do sistema.");
    }

    // Método para deletar um UsuarioCliente do sistema
    public void deletarUsuarioCliente(int idUsuarioCliente) {
        String sql = "DELETE FROM usuarios WHERE id = " + idUsuarioCliente;
        Conexao.executarUpdate(sql);
        System.out.println("Usuário Cliente deletado do sistema.");
    }
}
