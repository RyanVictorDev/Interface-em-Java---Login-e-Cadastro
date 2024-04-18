import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Conexao {
    public static void main(String[] args) {
        Connection conexao = null;
        try {
            Class.forName("org.postgresql.Driver");
            conexao = DriverManager.getConnection("jdbc:postgresql://localhost/saraijavas", "postgres","832450");
            ResultSet rsCliente = conexao.createStatement().executeQuery("SELECT * FROM USUARIOS");
            while (rsCliente.next()){
                System.out.println("Nome: " + rsCliente.getString("nome"));
            }

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Driver do banco de dados não localizado.");
        }
        catch (SQLException ex) {
            throw new RuntimeException("Ocorreu um erro ao acessar o banco de dados: " + ex.getMessage());
        } finally {
            if ( conexao != null){
                try {
                    conexao.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
