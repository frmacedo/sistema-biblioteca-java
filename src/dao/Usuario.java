package dao;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String telefone;
    private String senha;
    private String nivel; // admin ou user

    // Construtor completo
    public Usuario(int id, String nome, String email, String telefone, String senha, String nivel) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.nivel = nivel;
    }

    // ✅ Construtor auxiliar (usado para exibição em histórico de empréstimos)
    public Usuario(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.email = "";
        this.telefone = "";
        this.senha = "";
        this.nivel = "leitor"; // ou "user", se preferir
    }

    // Getters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
    this.id = id;
}


    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getSenha() {
        return senha;
    }

    public String getNivel() {
        return nivel;
    }

    // Setters (se precisar)
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    // Simula devolução de livro
    public void devolverLivro() {
        System.out.println("Livro devolvido pelo usuário.");
    }

    @Override
    public String toString() {
        String nivelFormatado;

        switch (nivel.toLowerCase()) {
            case "admin":
                nivelFormatado = "Administrador";
                break;
            case "user":
                nivelFormatado = "Interno";
                break;
            case "123":
            case "cliente":
            case "leitor":
                nivelFormatado = "Leitor";
                break;
            default:
                nivelFormatado = nivel; // caso tenha algo inesperado
        }

        return nome + " (" + telefone + ") - " + nivelFormatado;
    }
}











