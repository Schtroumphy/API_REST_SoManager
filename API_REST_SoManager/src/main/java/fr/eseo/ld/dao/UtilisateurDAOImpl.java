package fr.eseo.ld.dao;

import static fr.eseo.ld.dao.DAOUtilitaire.fermetures;
import static fr.eseo.ld.dao.DAOUtilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.eseo.ld.beans.Utilisateur;

/**
 * Classe de classe implémentées de DAO - UtilisateurDAOImpl
 * 
 * <p>Utilisation du modèle DAO.</p>
 * 
 * @version 1.0
 * @author Thessalène JEAN-LOUIS
 *
 * @see fr.eseo.ld.dao.UtilisateurDAOImpl
 * @see org.junit
 */

public class UtilisateurDAOImpl implements UtilisateurDAO {
	/* Attributs de l'entité Utilisateur */
	private static final String ATTRIBUT_NOM = "nom";
	private static final String ATTRIBUT_PRENOM = "prenom";
	private static final String ATTRIBUT_IDENTIFIANT = "identifiant";
	private static final String ATTRIBUT_HASH = "hash";
	private static final String ATTRIBUT_EMAIL = "email";
	private static final String ATTRIBUT_VALIDE = "valide";
	private static final String ATTRIBUT_ID_UTILISATEUR = "idUtilisateur";
	private static final String[] ATTRIBUTS_NOMS = { ATTRIBUT_ID_UTILISATEUR, ATTRIBUT_NOM, ATTRIBUT_PRENOM,
			ATTRIBUT_IDENTIFIANT, ATTRIBUT_HASH, ATTRIBUT_EMAIL, ATTRIBUT_VALIDE };
	/* Noms des entités */ 
	private static final String NOM_ENTITE = "Utilisateur";
	
	/* Requetes SQL pour Utilisateur */
	private static final String SQL_SELECT_UTILISATEUR = "SELECT idUtilisateur, nom, prenom, identifiant, hash, email, valide FROM Utilisateur ORDER BY idUtilisateur";

	/* Logger */
	private static Logger logger = Logger.getLogger(UtilisateurDAOImpl.class.getName());
	
	/* Initialisation du DAO */
	private DAOFactory daoFactory;

	UtilisateurDAOImpl( DAOFactory daoFactory ) {
	        this.daoFactory = daoFactory;
	    }

	/**
	 * Liste tous les Utilisateurs présents dans la BDD.
	 * 
	 * @return utilisateurs la liste des Utilisateurs présents dans la BDD.
	 */
	@Override
	public List<Utilisateur> lister() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Utilisateur> utilisateurs = new ArrayList<>();
		try {
			// création d'une connexion grâce à la DAOFactory placée en attribut de la
			// classe
			connection = this.creerConnexion();
			preparedStatement = connection.prepareStatement(SQL_SELECT_UTILISATEUR);
			resultSet = preparedStatement.executeQuery();
			// récupération des valeurs des attributs de la BDD pour les mettre dans une
			// liste
			while (resultSet.next()) {
				utilisateurs.add(recupererUtilisateur(resultSet));
			}
		} catch (SQLException e) {
			logger.log(Level.FATAL, "Échec du listage des objets.", e);
		} finally {
			// fermeture des ressources utilisées
			fermetures(resultSet, preparedStatement, connection);
		}
		return utilisateurs;
	}
	
	/**
	 * Liste tous les Utilisateurs ayant pour attributs les mêmes que ceux
	 * spécifiés dans un bean Utilisateur.
	 * 
	 * @param utilisateur l'Utilisateur que l'on souhaite trouver dans la BDD.
	 * @return utilisateurs la liste des Utilisateurs trouvés dans la BDD.
	 */ 
	@Override
	public List<Utilisateur> trouver(Utilisateur utilisateur) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Utilisateur> utilisateurs = new ArrayList<>();
		// tableau de Strings regroupant tous les noms des attributs d'un objet ainsi que les valeurs correspondantes
		String[][] attributs = { ATTRIBUTS_NOMS,
				{ String.valueOf(utilisateur.getIdUtilisateur()), utilisateur.getNom(), utilisateur.getPrenom(),
						utilisateur.getIdentifiant(), utilisateur.getHash(), utilisateur.getEmail(),
						String.valueOf(utilisateur.getValide()) } };
		try {
			// création d'une connexion grâce à la DAOFactory placée en attribut de la classe
			connection = this.creerConnexion();
			// mise en forme de la requête SELECT en fonction des attributs de l'objet utilisateur
			preparedStatement = connection.prepareStatement(initialisationRequetePreparee("SELECT * FROM Utilisateur WHERE identifiant=?", utilisateur.getIdentifiant()), Statement.NO_GENERATED_KEYS);
			resultSet = preparedStatement.executeQuery();
			// récupération des valeurs des attributs de la BDD pour les mettre dans une liste
			while (resultSet.next()) {
				utilisateurs.add(recupererUtilisateur(resultSet));
			}
		} catch (SQLException e) {
			logger.log(Level.WARN, "Échec de la recherche de l'objet.", e);
		} finally {
			// fermeture des ressources utilisées
			fermetures(resultSet, preparedStatement, connection);
		}
		return utilisateurs;
	}
	
	// #################################################
	// #               Méthodes privées                #
	// #################################################
	
	/**
	 * Crée une connexion à la BDD.
	 * 
	 * @return connection la connexion à la BDD.
	 * @throws SQLException
	 */
	protected Connection creerConnexion() throws SQLException {
		return this.daoFactory.getConnection();
	}

	/**
	 * Fait la correspondance (le mapping) entre une ligne issue de la table
	 * Utilisateur (un ResultSet) et un bean Utilisateur.
	 * 
	 * @param resultSet la ligne issue de la table Utilisateur.
	 * @return utilisateur le bean dont on souhaite faire la correspondance.
	 * @throws SQLException
	 */
	public static Utilisateur recupererUtilisateur(ResultSet resultSet) throws SQLException {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setIdUtilisateur(resultSet.getLong(ATTRIBUT_ID_UTILISATEUR));
		utilisateur.setNom(resultSet.getString(ATTRIBUT_NOM));
		utilisateur.setPrenom(resultSet.getString(ATTRIBUT_PRENOM));
		utilisateur.setIdentifiant(resultSet.getString(ATTRIBUT_IDENTIFIANT));
		utilisateur.setHash(resultSet.getString(ATTRIBUT_HASH));
		utilisateur.setEmail(resultSet.getString(ATTRIBUT_EMAIL));
		utilisateur.setValide(resultSet.getString(ATTRIBUT_VALIDE));
		return utilisateur;
	}
}
