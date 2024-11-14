package com.dipartimento.demowebapplications.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Piatto {

    private String nome;
    private String ingredienti;
    private List<Ristorante> ristoranti;

    public Piatto(String nome, String ingredienti) {
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.ristoranti = new ArrayList<>();
    }

}
