package com.example.hotels.tests;

import com.example.hotels.models.Hebergement;
import com.example.hotels.services.ServiceHebergement;

public class TestAjout {
    public static void main(String[] args) {
        Hebergement h = new Hebergement(
                "Dar El Medina",
                "Maison d’hôte",
                "Tunis",
                "Rue Sidi Ben Arous",
                "Tunisie",
                180.0,
                true,
                "photos/dar_medina.jpg",
                "album1",
                "Maison typique au cœur de la médina de Tunis",
                "WiFi, Climatisation, Petit-déjeuner",
                4,
                2,
                "attente"
        );

        ServiceHebergement service = new ServiceHebergement();
        service.ajouter(h);
    }
}
