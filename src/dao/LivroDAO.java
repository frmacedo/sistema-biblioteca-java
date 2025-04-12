package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    // ✅ Adiciona novo livro
    public static boolean adicionarLivro(Livro livro) {
        String sql = "INSERT INTO livros (titulo, autor, ano, emprestado) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setInt(3, livro.getAno());
            stmt.setBoolean(4, livro.isEmprestado());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    livro.setId(idGerado); // Atualiza o ID no objeto
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ Busca todos os livros
    public static List<Livro> buscarTodos() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT * FROM livros";

        try (Connection conn = ConexaoBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Livro livro = new Livro(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getInt("ano")
                );
                livro.setEmprestado(rs.getBoolean("emprestado"));
                livros.add(livro);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return livros;
    }

    // ✅ Atualiza status de empréstimo
    public static boolean atualizarEmprestimo(Livro livro) {
        String sql = "UPDATE livros SET emprestado = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, livro.isEmprestado());
            stmt.setInt(2, livro.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ Exclui livro (somente se não estiver emprestado)
    public static boolean excluirLivro(int idLivro) {
        // Verifica se está emprestado
        Livro livro = getLivroPorId(idLivro);
        if (livro != null && livro.isEmprestado()) {
            return false; // Não permite excluir livro emprestado
        }

        // Remove os vínculos de empréstimo antes de excluir
        EmprestimoDAO.excluirEmprestimosPorLivro(idLivro);

        String sql = "DELETE FROM livros WHERE id = ?";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLivro);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ Buscar livro por ID
    public static Livro getLivroPorId(int idLivro) {
        String sql = "SELECT * FROM livros WHERE id = ?";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLivro);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Livro livro = new Livro(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getInt("ano")
                );
                livro.setEmprestado(rs.getBoolean("emprestado"));
                return livro;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ✅ Método para "limpar" livros órfãos (sem usuário)
    public static void liberarLivrosOrfaos() {
        String sql = """
            UPDATE livros SET emprestado = 0
            WHERE id NOT IN (
                SELECT id_livro FROM emprestimos
            )
        """;

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}





