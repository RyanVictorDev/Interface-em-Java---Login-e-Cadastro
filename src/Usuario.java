public class Usuario {
    protected String nome;
    protected String password;
    protected int id;

    public int getId(){
        return id;
    }

    public void setId(int id){
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

    public  Usuario(String nome, String password ){
        this.nome = nome;
        this.password = password;
    }

}
