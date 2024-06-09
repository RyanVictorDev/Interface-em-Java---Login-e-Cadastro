package Objs;

import java.util.Date;

public class Livro {
    private int id;
    private String titulo;
    private String autor;
    private int paginas;
    private String categoria;
    private double valor;
    private Date data;
    private boolean isDeleted = false;
    private boolean isAlugado = false;

    public int getId() {
        return id;
    }

    public void setId(int novoId) {
        this.id = novoId;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String novoTitulo) {
        this.titulo = novoTitulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String novoAutor) {
        this.autor = novoAutor;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int novoNumPaginas) {
        this.paginas = novoNumPaginas;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String novaCategoria) {
        this.categoria = novaCategoria;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double novoValor) {
        this.valor = novoValor;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date novaData) {
        this.data = novaData;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean newIsDeleted) {
        this.isDeleted = newIsDeleted;
    }

    public boolean getIsAlugado() {
        return isAlugado;
    }

    public void setIsAlugado(boolean novoIsAlugado) {
        this.isAlugado = novoIsAlugado;
    }
}
