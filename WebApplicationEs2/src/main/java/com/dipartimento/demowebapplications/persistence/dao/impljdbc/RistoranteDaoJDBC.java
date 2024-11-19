package com.dipartimento.demowebapplications.persistence.dao.impljdbc;

import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.model.Ristorante;
import com.dipartimento.demowebapplications.persistence.DBManager;
import com.dipartimento.demowebapplications.persistence.dao.PiattoDao;
import com.dipartimento.demowebapplications.persistence.dao.RistoranteDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RistoranteDaoJDBC implements RistoranteDao {
    Connection connection = null;
    public RistoranteDaoJDBC(Connection conn){ this.connection = conn; }

    @Override
    public List<Ristorante> findAll() {
        List<Ristorante> ristoranti = new ArrayList<Ristorante>();
        String query = "select * from ristorante";

        System.out.println("going to execute:"+query);

        Statement st = null;
        try {
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                Ristorante rist = new RistoranteProxy();
                rist.setNome(rs.getString("nome"));
                rist.setDescrizione(rs.getString("descrizione"));
                rist.setUbicazione(rs.getString("ubicazione"));
                ristoranti.add(rist);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ristoranti;
    }

    @Override
    public Ristorante findByID(String nome) {
        String query = "SELECT nome, descrizione, ubicazione FROM ristorante WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nome);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String descrizione = resultSet.getString("descrizione");
                String ubicazione = resultSet.getString("ubicazione");
                RistoranteProxy rist = new RistoranteProxy();
                rist.setNome(nome);
                rist.setDescrizione(descrizione);
                rist.setUbicazione(ubicazione);
                return rist;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void create(Ristorante ristorante) {
        String query = "INSERT INTO ristorante (nome, descrizione, ubicazione) VALUES (?, ?, ?) " +
                "ON CONFLICT (nome) DO UPDATE SET " +
                "   descrizione = EXCLUDED.descrizione , "+
                "   ubicazione = EXCLUDED.ubicazione ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristorante.getNome());
            statement.setString(2, ristorante.getDescrizione());
            statement.setString(3, ristorante.getUbicazione());
            statement.executeUpdate();

            manageRistorantePiattiRelation(ristorante);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Ristorante ristorante) {
        create(ristorante);
    }

    @Override
    public void delete(Ristorante ristorante) {
        String query = "DELETE FROM ristorante WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristorante.getNome());
            statement.executeUpdate();

            removeAllRistorantePiattiRelations(ristorante.getNome());
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting restaurant", e);
        }
    }

    private void manageRistorantePiattiRelation(Ristorante ristorante) throws SQLException {
        List<Piatto> piatti = ristorante.getPiatti();
        if (piatti == null || piatti.isEmpty()) {
            return;
        }
        removeAllRistorantePiattiRelations(ristorante.getNome());

        PiattoDao piattoDao = DBManager.getInstance().getPiattoDao();

        for (Piatto piatto : piatti) {
            piattoDao.create(piatto);
            insertRistorantePiattoRelation(ristorante.getNome(), piatto.getNome());
        }
    }

    private void removeAllRistorantePiattiRelations(String ristoranteNome) throws SQLException {
        String query = "DELETE FROM ristorante_piatto WHERE ristorante_nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristoranteNome);
            statement.executeUpdate();
        }
    }

    private void insertRistorantePiattoRelation(String ristoranteNome, String piattoNome) throws SQLException {
        String query = "INSERT INTO ristorante_piatto (ristorante_nome, piatto_nome) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristoranteNome);
            statement.setString(2, piattoNome);
            statement.executeUpdate();
        }
    }

    public static void main(String[] args) {
        RistoranteDao ristoDao = DBManager.getInstance().getRistoranteDao();
        List<Ristorante> ristoranti = ristoDao.findAll();
        for (Ristorante ristorante : ristoranti) {
            System.out.println(ristorante.getNome());
            System.out.println(ristorante.getDescrizione());
            System.out.println(ristorante.getUbicazione());
        }
    }
}


