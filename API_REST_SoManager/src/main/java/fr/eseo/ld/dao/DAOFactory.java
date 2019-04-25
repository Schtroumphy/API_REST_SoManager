package fr.eseo.ld.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import fr.eseo.ld.config.DAOConfigurationException;

public class DAOFactory {

    //private static final String FICHIER_PROPERTIES = "dao.properties";
    private static String PROPERTY_URL = "jdbc:mysql://localhost/somanager?useLegacyDatetimeCode=false&serverTimezone=Europe/Paris";
    private static String PROPERTY_DRIVER = "com.mysql.jdbc.Driver";
    private static String PROPERTY_NOM_UTILISATEUR = "root";
    private static String PROPERTY_MOT_DE_PASSE = "";

    private String url;
    private String username;
    private String password;

    DAOFactory( String url, String username, String password ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /*
     * Méthode chargée de récupérer les informations de connexion à la base de
     * données, charger le driver JDBC et retourner une instance de la Factory
     */
    public static DAOFactory getInstance() throws DAOConfigurationException {
        Properties properties = new Properties();
        String url;
        String driver;
        String nomUtilisateur;
        String motDePasse;

       /* ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream fichierProperties = classLoader.getResourceAsStream( FICHIER_PROPERTIES );

        if ( fichierProperties == null ) {
            throw new DAOConfigurationException( "Le fichier properties " + FICHIER_PROPERTIES + " est introuvable." );
        }*/

        //try {
        /*
           properties.load( fichierProperties );
            url = properties.getProperty( PROPERTY_URL );
            driver = properties.getProperty( PROPERTY_DRIVER );
            nomUtilisateur = properties.getProperty( PROPERTY_NOM_UTILISATEUR );
            motDePasse = properties.getProperty( PROPERTY_MOT_DE_PASSE );
         */
            url = PROPERTY_URL;
            driver = PROPERTY_DRIVER;
            nomUtilisateur = PROPERTY_NOM_UTILISATEUR;
            motDePasse = PROPERTY_MOT_DE_PASSE;
        /*} catch ( IOException e ) {
            throw new DAOConfigurationException( "Impossible de charger le fichier properties " + FICHIER_PROPERTIES, e );
        }*/

        try {
            Class.forName( driver );
        } catch ( ClassNotFoundException e ) {
            throw new DAOConfigurationException( "Le driver est introuvable dans le classpath.", e );
        }

        DAOFactory instance = new DAOFactory( url, nomUtilisateur, motDePasse );
        return instance;
    }    

    /* Méthode chargée de fournir une connexion à la base de données */
     /* package */ public Connection getConnection() throws SQLException {
        return DriverManager.getConnection( url, username, password );
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
     * Méthodes de récupération de l'implémentation des différents DAO 
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */ 
     
    public UtilisateurDAO getUtilisateurDao() {
        return new UtilisateurDAOImpl(this);
    }
    
    public SujetDAO getSujetDao() {
        return new SujetDAOImpl(this);
    }
    
    public JuryDAO getJuryDao() {
        return new JuryDAOImpl(this);
    }
}
