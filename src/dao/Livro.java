package dao;

public class Livro {

    private int id;
    private String titulo;
    private String autor;
    private int ano;
    private boolean emprestado = false;

    // Construtor padrão (necessário pra quando você quiser instanciar sem passar parâmetros)
    public Livro() {}

    public Livro(int id, String titulo, String autor, int ano) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) { // Adiciona o setter para uso em cópias
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) { // Adiciona setter de título
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) { // Adiciona setter de autor
        this.autor = autor;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) { // Adiciona setter de ano
        this.ano = ano;
    }

    public void setEmprestado(boolean emprestado) {
        this.emprestado = emprestado;
    }

    public boolean isEmprestado() {
        return emprestado;
    }

   @Override
public String toString() {
    return titulo + (emprestado ? " [EMPRESTADO]" : "");
 }
}






