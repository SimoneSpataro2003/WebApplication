package com.dipartimento.demowebapplications.persistence.dao.impljdbc;

import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.persistence.DBManager;
import com.dipartimento.demowebapplications.persistence.dao.PiattoDao;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
class PiattoDaoJDBCComponent implements PiattoDao {
    private final Connection connection;

    public PiattoDaoJDBCComponent() {
        this.connection = DBManager.getInstance().getConnection();
    }

    @Override
    public List<Piatto> findAll() {
        List<Piatto> piatti = new ArrayList<>();
        String query = "SELECT * FROM piatto";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Piatto piatto = new Piatto();
                piatto.setNome(rs.getString("nome"));
                piatto.setIngredienti(rs.getString("ingredienti"));
                piatti.add(piatto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return piatti;
    }

    @Override
    public Piatto findByID(String nome) {
        String query = "SELECT nome, ingredienti FROM piatto WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nome);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Piatto(resultSet.getString("nome"), resultSet.getString("ingredienti"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void create(Piatto piatto) {
        String query = "INSERT INTO piatto (nome, ingredienti) VALUES (?, ?) " +
                "ON CONFLICT (nome) DO UPDATE SET ingredienti = EXCLUDED.ingredienti";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, piatto.getNome());
            statement.setString(2, piatto.getIngredienti());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Piatto piatto) {
        String query = "UPDATE piatto SET ingredienti = ? WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, piatto.getIngredienti());
            statement.setString(2, piatto.getNome());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Piatto not found for update: " + piatto.getNome());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String nome) {
        String query = "DELETE FROM piatto WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nome);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                throw new SQLException("Piatto not found for deletion: " + nome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Piatto> findAllByRistoranteName(String ristoranteNome) {
        List<Piatto> piatti = new ArrayList<>();
        String query = "SELECT p.nome, p.ingredienti FROM piatto p " +
                "JOIN ristorante_piatto rp ON p.nome = rp.piatto_nome " +
                "WHERE rp.ristorante_nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristoranteNome);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String ingredienti = resultSet.getString("ingredienti");
                piatti.add(new Piatto(nome, ingredienti));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
