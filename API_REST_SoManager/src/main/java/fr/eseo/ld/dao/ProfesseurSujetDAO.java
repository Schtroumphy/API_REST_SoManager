package fr.eseo.ld.dao;

import java.util.List;

import fr.eseo.ld.beans.Sujet;

public interface ProfesseurSujetDAO {
	List<Sujet> listerSujets(int value);
}
