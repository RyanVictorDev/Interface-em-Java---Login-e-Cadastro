package Objs;

public class UsuarioAdmin extends Usuario {

    public UsuarioAdmin(String nome, String password) {
        super(nome, password, true); // Definindo isAdmin como true para um admin
    }

    public void deletarLivro(int idLivro) {
        String sql = "DELETE FROM livros WHERE id = " + idLivro;
        Conexao.executarUpdate(sql);
        System.out.println("Livro deletado do sistema.");
    }

    public void deletarUsuarioCliente(int idUsuarioCliente) {
        String sql = "DELETE FROM usuarios WHERE id = " + idUsuarioCliente;
        Conexao.executarUpdate(sql);
        System.out.println("Usuário Cliente deletado do sistema.");
    }
}
