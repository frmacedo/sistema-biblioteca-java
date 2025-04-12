package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class EmprestimoDAO {

    public static boolean salvarEmprestimo(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimos (id_usuario, id_livro, data_emprestimo) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimo.getUsuario().getId());
            stmt.setInt(2, emprestimo.getLivro().getId());
            stmt.setDate(3, Date.valueOf(emprestimo.getDataEmprestimo()));

            int rowsAffected = stmt.executeUpdate();

            // Atualiza status do livro
            emprestimo.getLivro().setEmprestado(true);
            LivroDAO.atualizarEmprestimo(emprestimo.getLivro());

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int contarEmprestimosAtivos(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM emprestimos e " +
                     "JOIN livros l ON e.id_livro = l.id " +
                     "WHERE e.id_usuario = ? AND l.emprestado = 1";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Emprestimo> listarEmprestimosAtivos() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = """
            SELECT e.data_emprestimo,
                   u.id AS id_usuario, u.nome AS nome_usuario,
                   l.id AS id_livro, l.titulo AS titulo_livro, l.autor, l.ano
            FROM emprestimos e
            JOIN usuarios u ON e.id_usuario = u.id
            JOIN livros l ON e.id_livro = l.id
            WHERE l.emprestado = 1
        """;

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario(rs.getInt("id_usuario"), rs.getString("nome_usuario"));
                Livro livro = new Livro(
                    rs.getInt("id_livro"),
                    rs.getString("titulo_livro"),
                    rs.getString("autor"),
                    rs.getInt("ano")
                );
                livro.setEmprestado(true);

                Emprestimo emp = new Emprestimo(livro, usuario);
                emp.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());

                emprestimos.add(emp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emprestimos;
    }

    public static boolean usuarioPodeDevolverLivro(int idLivro, int idUsuarioSelecionado) {
        String sql = "SELECT COUNT(*) FROM emprestimos e " +
                     "JOIN livros l ON e.id_livro = l.id " +
                     "WHERE e.id_usuario = ? AND e.id_livro = ? AND l.emprestado = 1";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuarioSelecionado);
            stmt.setInt(2, idLivro);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean registrarDevolucao(int idLivro) {
        String updateLivroSql = "UPDATE livros SET emprestado = 0 WHERE id = ?";
        String deleteEmprestimoSql = "DELETE FROM emprestimos WHERE id_livro = ?";

        try (Connection conn = ConexaoBD.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateLivroSql);
                 PreparedStatement stmt2 = conn.prepareStatement(deleteEmprestimoSql)) {

                stmt1.setInt(1, idLivro);
                stmt1.executeUpdate();

                stmt2.setInt(1, idLivro);
                stmt2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void excluirEmprestimosPorUsuario(int idUsuario) {
        String buscarLivrosSql = "SELECT id_livro FROM emprestimos WHERE id_usuario = ?";
        String atualizarLivroSql = "UPDATE livros SET emprestado = 0 WHERE id = ?";
        String deletarEmprestimosSql = "DELETE FROM emprestimos WHERE id_usuario = ?";

        try (Connection conn = ConexaoBD.conectar()) {
            List<Integer> livrosEmprestados = new ArrayList<>();

            try (PreparedStatement buscarStmt = conn.prepareStatement(buscarLivrosSql)) {
                buscarStmt.setInt(1, idUsuario);
                ResultSet rs = buscarStmt.executeQuery();
                while (rs.next()) {
                    livrosEmprestados.add(rs.getInt("id_livro"));
                }
            }

            for (int idLivro : livrosEmprestados) {
                try (PreparedStatement atualizarStmt = conn.prepareStatement(atualizarLivroSql)) {
                    atualizarStmt.setInt(1, idLivro);
                    atualizarStmt.executeUpdate();
                }
            }

            try (PreparedStatement deletarStmt = conn.prepareStatement(deletarEmprestimosSql)) {
                deletarStmt.setInt(1, idUsuario);
                deletarStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void excluirEmprestimosPorLivro(int idLivro) {
        String sql = "DELETE FROM emprestimos WHERE id_livro = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void liberarLivrosPorUsuario(int idUsuario) {
        String sql = """
            UPDATE livros SET emprestado = 0
            WHERE id IN (
                SELECT id_livro FROM emprestimos WHERE id_usuario = ?
            )
        """;

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}







