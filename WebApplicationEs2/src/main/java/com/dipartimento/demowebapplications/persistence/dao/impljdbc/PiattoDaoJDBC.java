package com.dipartimento.demowebapplications.persistence.dao.impljdbc;

import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.model.Ristorante;
import com.dipartimento.demowebapplications.persistence.DBManager;
import com.dipartimento.demowebapplications.persistence.dao.PiattoDao;
import com.dipartimento.demowebapplications.persistence.dao.RistoranteDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PiattoDaoJDBC implements PiattoDao {
    Connection connection;
    public PiattoDaoJDBC(Connection conn){ this.connection = conn; }

    @Override
    public List<Piatto> findAll() {
        List<Piatto> piatti = new ArrayList<>();
        String query = "SELECT * FROM piatto";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Piatto piatto = new Piatto();
                piatto.setNome(rs.getString("nome"));
                piatto.setIngredienti(rs.getString("ingredienti"));
                piatti.add(piatto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching all dishes", e);
        }
        return piatti;
    }

    @Override
    public Piatto findByID(String nome) {
        String query = "SELECT nome, ingredienti FROM piatto WHERE nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Piatto(rs.getString("nome"), rs.getString("ingredienti"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching dish by name: " + nome, e);
        }
        return null;
    }

    @Override
    public void create(Piatto piatto) {
        String query = "INSERT INTO piatto (nome, ingredienti) VALUES (?, ?) " +
                "ON CONFLICT (nome) DO UPDATE SET ingredienti = EXCLUDED.ingredienti";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, piatto.getNome());
            stmt.setString(2, piatto.getIngredienti());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating or updating dish: " + piatto.getNome(), e);
        }
    }

    @Override
    public void update(Piatto piatto) {
        String query = "UPDATE piatto SET ingredienti = ? WHERE nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, piatto.getIngredienti());
            stmt.setString(2, piatto.getNome());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("No dish found with name: " + piatto.getNome());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating dish: " + piatto.getNome(), e);
        }
    }

    @Override
    public void delete(String nome) {
        String query = "DELETE FROM piatto WHERE nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nome);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new RuntimeException("No dish found to delete with name: " + nome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting dish with name: " + nome, e);
        }
    }

    @Override
    public List<Piatto> findAllByRistoranteName(String ristoranteNome) {
        List<Piatto> piatti = new ArrayList<>();
        String query = "SELECT p.nome, p.ingredienti FROM piatto p " +
                "JOIN ristorante_piatto rp ON p.nome = rp.piatto_nome " +
                "WHERE rp.ristorante_nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ristoranteNome);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Piatto piatto = new Piatto(rs.getString("nome"), rs.getString("ingredienti"));
                    piatti.add(piatto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching dishes for restaurant: " + ristoranteNome, e);
        }
        return piatti;
    }

    public static void main(String[] args) {
        PiattoDao piattoDao = DBManager.getInstance().getPiattoDao();
        List<Piatto> piatti = piattoDao.findAll();
        for (Piatto piatto : piatti) {
            System.out.println(piatto.getNome());
            System.out.println(piatto.getIngredienti());

        }
    }
}
