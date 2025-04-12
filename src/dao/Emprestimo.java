package dao;

import java.time.LocalDate;
import dao.Usuario;
import dao.Livro;

public class Emprestimo {
    private Livro livro;
    private Usuario usuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;

    // 🔹 Construtor padrão para novos empréstimos
    public Emprestimo(Livro livro, Usuario usuario) {
        this.livro = livro;
        this.usuario = usuario;
        this.dataEmprestimo = LocalDate.now();
    }

    // ✅ Construtor completo (para exibir histórico ou carregar do banco)
    public Emprestimo(Livro livroSelecionado, Usuario usuarioSelecionado, LocalDate dataEmprestimo, LocalDate dataDevolucao) {
        this.livro = livroSelecionado;
        this.usuario = usuarioSelecionado;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
    }

    // ✅ Getters e Setters
    public Livro getLivro() {
        return livro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    // ✅ Método para devolver livro
    public void devolver() {
        this.dataDevolucao = LocalDate.now();
        usuario.devolverLivro();
        livro.setEmprestado(false);
    }

    @Override
    public String toString() {
        return "Livro: " + livro.getTitulo() + " | Usuário: " + usuario.getNome() +
               " | Empréstimo: " + dataEmprestimo +
               (dataDevolucao != null ? " | Devolvido: " + dataDevolucao : " | Em andamento");
    }
}




