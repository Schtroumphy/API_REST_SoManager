package fr.eseo.ld.dao;

import static fr.eseo.ld.dao.DAOUtilitaire.fermetures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mysql.jdbc.Statement;

import fr.eseo.ld.beans.Sujet;

public class ProfesseurSujetDAOImpl implements ProfesseurSujetDAO {

	/* Requetes SQL */
	private static final String SQL_SELECT_SUJETS = "SELECT * FROM sujet WHERE sujet.idSujet IN (SELECT idSujet from professeursujet where idProfesseur = ? )";

	/* Logger */
	private static Logger logger = Logger.getLogger(ProfesseurSujetDAO.class.getName());

	/* Initialisation du DAO */
	private DAOFactory daoFactory;

	ProfesseurSujetDAOImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/**
	 * Liste tous les Sujets présents dans la BDD.
	 * 
	 * @return sujets la liste des Sujets présents dans la BDD.
	 */
	@Override
	public List<Sujet> listerSujets(int value) {
		System.out.println(value);
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Sujet> sujets = new ArrayList<>();
		try {
			// cr�ation d'une connexion grâce à la DAOFactory plac�e en attribut de la
			// classe
			connection = this.creerConnexion();
			preparedStatement = connection.prepareStatement(initialisationRequetePreparee(SQL_SELECT_SUJETS, value), Statement.RETURN_GENERATED_KEYS);
			resultSet = preparedStatement.executeQuery();
			// r�cup�ration des valeurs des attributs de la BDD pour les mettre dans une
			// liste
			resultSet.first();
			sujets.add(recupererSujets(resultSet));
			while (resultSet.next()) {
				System.out.println("while");
				sujets.add(recupererSujets(resultSet));
			}
			System.out.println("DAO : " + sujets);
		} catch (SQLException e) {
			logger.log(Level.WARN, "Échec du listage des objets.", e);
		} finally {
			fermetures(resultSet, preparedStatement, connection);
		}
		return sujets;
	}

	// #################################################
	// # M�thodes priv�es #
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
	 * Fait la correspondance (le mapping) entre une ligne issue de la table Sujet
	 * (un ResultSet) et un bean Sujet.
	 * 
	 * @param resultSet la ligne issue de la table Sujet.
	 * @return sujet le bean dont on souhaite faire la correspondance.
	 * @throws SQLException
	 */
	public static Sujet recupererSujets(ResultSet resultSet) throws SQLException {
		return SujetDAOImpl.recupererSujet(resultSet);
	}
	
	/**
	 * Initialise une requête préparée.
	 * 
	 * @param connection la connexion à la BDD.
	 * @param sql la requête SQL.
	 * @param returnGeneratedKeys le boolean qui permet de générer des ID ou pas.
	 * @param objets la liste d'objets à insérer dans la requête.
	 * @return preparedStatement la requête préparée initialisée.
	 * @throws SQLException
	 */
	protected static String initialisationRequetePreparee(String sql, Object... objets) {
		String[] listeSQL = (sql+" ").split("\\?");
		StringBuilder newSQL = new StringBuilder(listeSQL[0]);
		for(int i = 0; i<objets.length; i++) {
			newSQL.append("\"" + objets[i] + "\"" + listeSQL[i+1]);
		}
		return newSQL.toString().replaceAll("\"null\"", "null");
	}	

}
