package services;

import entities.Transportation;

import java.util.List;

public interface IServiceTransport {
    public void ajouter(Transportation h);
    public void modifier(Transportation h);
    public void supprimer(int id);

    public static List<Transportation> afficher() {
        return null;
    }
}
