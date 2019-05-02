package fr.eseo.ld.dao;

import static fr.eseo.ld.dao.DAOUtilitaire.fermetures;
import static fr.eseo.ld.dao.DAOUtilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mysql.jdbc.Statement;

import fr.eseo.ld.beans.Epreuve;
import fr.eseo.ld.beans.Equipe;
import fr.eseo.ld.beans.Jury;

public class JuryDAOImpl implements JuryDAO {

	/* Attributs de l'entité Sujet */
	private static final String ATTRIBUT_ID_JURY = "idJury";
	private static final String ATTRIBUT_ID_EPREUVE = "idEpreuve";
	private static final String ATTRIBUT_ID_EQUIPE = "idEquipe";
	private static final String ATTRIBUT_DATE = "date";

	/* Requetes SQL */
	private static final String SQL_SELECT_TOUT = "SELECT * FROM Jury";

	/* Logger */
	private static Logger logger = Logger.getLogger(JuryDAO.class.getName());

	/* Initialisation du DAO */
	private DAOFactory daoFactory;

	JuryDAOImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/**
	 * Liste tous les Sujets présents dans la BDD.
	 * 
	 * @return sujets la liste des Sujets présents dans la BDD.
	 */
	@Override
	public List<Jury> lister() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Jury> jurys = new ArrayList<>();
		try {
			// cr�ation d'une connexion grâce à la DAOFactory plac�e en attribut de la
			// classe
			connection = this.creerConnexion();
			preparedStatement = connection.prepareStatement(SQL_SELECT_TOUT);
			resultSet = preparedStatement.executeQuery();
			// r�cup�ration des valeurs des attributs de la BDD pour les mettre dans une
			// liste
			resultSet.first();
			jurys.add(recupererJury(resultSet));
			while (resultSet.next()) {
				//System.out.println("while");
				jurys.add(recupererJury(resultSet));
			}
			//System.out.println("DAO : " + jurys);
		} catch (SQLException e) {
			logger.log(Level.WARN, "Échec du listage des objets.", e);
		} finally {
			fermetures(resultSet, preparedStatement, connection);
		}
		return jurys;
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
	public static Jury recupererJury(ResultSet resultSet) throws SQLException {
		Epreuve epreuve = new Epreuve();
		Equipe equipe = new Equipe();

		epreuve.setIdEpreuve(resultSet.getLong(ATTRIBUT_ID_EPREUVE));
		equipe.setIdEquipe(resultSet.getString(ATTRIBUT_ID_EQUIPE));

		Jury jury = new Jury();
		jury.setIdJury(resultSet.getLong(ATTRIBUT_ID_JURY));
//		jury.setIdEpreuve(resultSet.getLong(ATTRIBUT_ID_EPREUVE));
		jury.setEpreuve(epreuve);
//		jury.setIdEquipe(resultSet.getString(ATTRIBUT_ID_EQUIPE));
		jury.setEquipe(equipe);
		jury.setDate(resultSet.getDate(ATTRIBUT_DATE));
		return jury;
	}

}
