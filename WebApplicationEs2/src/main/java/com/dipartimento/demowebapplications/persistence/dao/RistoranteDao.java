package com.dipartimento.demowebapplications.persistence.dao;

import com.dipartimento.demowebapplications.model.Ristorante;

import java.util.List;

public interface RistoranteDao {
    public List<Ristorante> findAll();
    public Ristorante findByPrimaryKey(String nome);
    public void save(Ristorante ristorante);
    public void delete(Ristorante ristorante);// Metodi per la relazione molti-a-molti
    void addPiattoToRistorante(String ristoranteNome, String piattoNome);
    void removePiattoFromRistorante(String ristoranteNome, String piattoNome);
}
