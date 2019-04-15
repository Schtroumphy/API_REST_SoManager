package fr.eseo.ld.dao;

import java.util.List;
import fr.eseo.ld.beans.Utilisateur;

public interface UtilisateurDAO {
	List<Utilisateur> lister();

	List<Utilisateur> trouver(Utilisateur utilisateur);
	
}
