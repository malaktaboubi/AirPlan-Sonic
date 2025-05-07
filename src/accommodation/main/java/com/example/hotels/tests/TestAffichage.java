package com.example.hotels.tests;


import com.example.hotels.models.Hebergement;
import com.example.hotels.services.ServiceHebergement;

import java.util.List;

public class TestAffichage {
    public static void main(String[] args) {
        ServiceHebergement service = new ServiceHebergement();
        List<Hebergement> liste = service.afficher();

        for (Hebergement h : liste) {
            System.out.println(h);
        }
    }
}

