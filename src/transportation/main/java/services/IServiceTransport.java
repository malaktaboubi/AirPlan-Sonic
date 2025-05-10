package services;

import entities.Transportation;

import java.util.List;

public interface IServiceTransport <t> {
    public void ajouter(t t);
    public void modifier(t t);
    public void supprimer(int id);

    public static List<Transportation> afficher() {
        return null;
    }
}
