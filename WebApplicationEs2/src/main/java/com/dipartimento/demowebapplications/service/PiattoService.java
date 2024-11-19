package com.dipartimento.demowebapplications.service;


import com.dipartimento.demowebapplications.exception.PiattoNotValidException;
import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.persistence.dao.PiattoDao;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
class PiattoService implements IPiattoService {
    private final PiattoDao piattoDao;

    PiattoService(PiattoDao piattoDao) {
        this.piattoDao = piattoDao;
    }


    @Override
    public Collection<Piatto> findAll() {
        return piattoDao.findAll();
    }

    @Override
    public Piatto findById(String nome) {
        return piattoDao.findByID(nome);
    }

    @Override
    public Piatto createPiatto(Piatto piatto) throws Exception {

        System.out.println("Doing create piatto");
        checkPiattoIsValid(piatto);

        Piatto byPrimaryKey = piattoDao.findByID(piatto.getNome());
        if(byPrimaryKey!=null){
            throw new Exception("Already exists a Piatto whit the same name:"+piatto.getNome());
        }


        piattoDao.create(piatto);

        return piattoDao.findByID(piatto.getNome());
    }

    private void checkPiattoIsValid(Piatto piatto) throws PiattoNotValidException {
        if(piatto==null){
            throw new PiattoNotValidException("Il nome del piatto non può essere null.");
        }

        if(piatto.getNome()==null || piatto.getNome().isEmpty()){
            throw new PiattoNotValidException("Il piatto non può non avere un nome.");
        }

        if (piatto.getIngredienti() == null || piatto.getIngredienti().isEmpty()) {
            throw new PiattoNotValidException("Il piatto non può non avere ingredienti.");
        }

        if (!piatto.getNome().matches("[a-zA-Z\\s]+")) {
            throw new PiattoNotValidException("Il nome del piatto può contenere solo lettere e spazi.");
        }
    }

    @Override
    public Piatto updatePiatto(String nomePiatto, Piatto piatto) throws Exception {
        checkPiattoIsValid(piatto);

        Piatto existingPiatto = piattoDao.findByID(nomePiatto);
        if (existingPiatto == null) {
            throw new Exception("Nessun piatto trovato col nome " + nomePiatto);
        }
        piatto.setNome(nomePiatto);

        piattoDao.update(piatto);

        return piattoDao.findByID(nomePiatto);
    }

    @Override
    public void deletePiatto(String nome) throws Exception {
        Piatto existingPiatto = piattoDao.findByID(nome);
        if (existingPiatto == null) {
            throw new Exception("Nessun piatto trovato col nome " + nome);
        }
        piattoDao.delete(nome);
    }
}
