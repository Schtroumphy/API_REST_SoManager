package com.server.somanager.service;

import java.util.ArrayList;
import fr.eseo.ld.jwt.JWTDecoder;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import fr.eseo.ld.beans.Jury;
import fr.eseo.ld.beans.Message;
import fr.eseo.ld.beans.Sujet;
import fr.eseo.ld.beans.Utilisateur;
import fr.eseo.ld.dao.DAOFactory;
import fr.eseo.ld.dao.JuryDAO;
import fr.eseo.ld.dao.ProfesseurSujetDAO;
import fr.eseo.ld.dao.SujetDAO;
import fr.eseo.ld.dao.UtilisateurDAO;

/**
 * Classe de requêtes API - Ccontroleur
 * 
 * <p>
 * Utilisation du modèle DAO.
 * </p>
 * 
 * @version 1.0
 * @author Thessalène JEAN-LOUIS
 * 
 */

@RestController
public class RestService {

	private static final Logger logger = LoggerFactory.getLogger(RestService.class);

	private List<Claim> claims;
	private String tokenUser;

	private UtilisateurDAO utilisateurDao;
	private SujetDAO sujetDao;
	private JuryDAO juryDao;
	private ProfesseurSujetDAO professeurSujetDAO;

	private DAOFactory daoFactory = DAOFactory.getInstance();

	@GetMapping(value = "/")
	public ResponseEntity<String> pong() {
		logger.info("Démarrage des services OK .....");
		return new ResponseEntity<String>("Réponse du serveur: " + HttpStatus.OK.name(), HttpStatus.OK);
	}

	// ###########################################################################################
	// # Méthodes GET
	// ###########################################################################################

	/*
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * UTILISATEURS * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * *
	 */

	/**
	 * @Description Renvoie la liste de tous les utilisateurs
	 * @Path /get
	 * @Params none
	 * @Result Liste<Utilisateur>
	 */

	@CrossOrigin
	@RequestMapping(value = "/get/users", method = RequestMethod.GET)
	@ResponseBody
	public List<Utilisateur> getUsers(@RequestParam(required = false, value = "value") String value) {

		List<Utilisateur> listeUtilisateur = new ArrayList<Utilisateur>();

		this.utilisateurDao = daoFactory.getUtilisateurDao();

		listeUtilisateur = utilisateurDao.lister();
		return listeUtilisateur;
	}

	/**
	 * @Description Connexion : Renvoie un message de succès ou d'échec
	 * @Path /get/user/connect?identifiant=test&password=test
	 * @Params identifiant, password identifiants de l'utilisateur qui cherche à se
	 *         connecter
	 * @Result (classe) Message : message de succès ou d'échec
	 */

	@CrossOrigin
	@RequestMapping(value = "/get/user/connect", method = RequestMethod.GET)
	@ResponseBody
	public Message connectUser(@RequestParam(required = true, value = "identifiant") String identifiant,
			@RequestParam(required = true, value = "password") String password) {
		Message message = new Message();
		List<Utilisateur> listeUtilisateur = new ArrayList<Utilisateur>();
		this.utilisateurDao = daoFactory.getUtilisateurDao();

		/* Création de l'utilisateur avec l'identifiant entré en paramètre */
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setIdentifiant(identifiant);

		/* Recherche des informations relatives à l'utilisateur via son identifiant */
		listeUtilisateur = utilisateurDao.trouver(utilisateur);

		if (listeUtilisateur.isEmpty()) {
			message.setSuccess(false);
			message.setMessage("Aucun utilisateur avec cet identifiant n'est présent dans la base de données");
		} else {
			if (BCrypt.checkpw(password, listeUtilisateur.get(0).getHash())) {
				message.setSuccess(true);

				/* On génère le token avec les informations de l'utilisateur se connectant */
				tokenUser = createToken(listeUtilisateur.get(0));
				/* Vérification du token */
				boolean resultat = verifyToken(tokenUser);

				claims = (List<Claim>) decodeToken(tokenUser);

				/* Initialisation du message à renvoyer */
				message.setMessage("Connexion réussie ! token : " + tokenUser + " Valide : " + resultat + " Décodage : "
						+ claims.get(1).asString());
			} else {
				message.setSuccess(false);
				message.setMessage("Echec de connexion : Mot de passe incorrect !");
			}
		}
		return message;
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * SUJETS * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * *
	 */

	/**
	 * @Description Renvoie la liste de tous les utilisateurs
	 * @Path /get/sujets
	 * @Params none
	 * @Result Liste<Sujet>
	 */

	@CrossOrigin
	@RequestMapping(value = "/get/sujets", method = RequestMethod.GET)
	@ResponseBody
	public Object getSujets(@RequestParam(required = false, value = "value") String value) {
		Message message = new Message();
		// Check the token
		System.out.println("Token : " + tokenUser);
		if (tokenUser == null) {
			message.setSuccess(false);
			message.setMessage("Vous devez vous identifier pour accéder à cette requête");
			return message;
		} else {
		List<Sujet> listeSujets = new ArrayList<Sujet>();

		this.sujetDao = daoFactory.getSujetDao();

		listeSujets = sujetDao.lister();
		return listeSujets;
		}
	}

	/**
	 * @Description : Renvoie les informations relatives à un sujet
	 * @Path : /get/sujetById?idSujet=
	 * @params id l'id du sujet recherché
	 * @return Liste<Sujet> liste contenant le sujet recherché
	 */

	@CrossOrigin
	@RequestMapping(value = "/get/sujetById", method = RequestMethod.GET)
	@ResponseBody
	public Sujet getSujetById(@RequestParam(required = true, value = "idSujet") Long value) {

		Sujet sujet = new Sujet();
		this.sujetDao = daoFactory.getSujetDao();
		sujet = sujetDao.trouver(value);
		return sujet;
	}

	/**
	 * @Description : Renvoie les sujets concernant un professeur
	 * @Path : /get/sujetsByIdProfesseur?idProfesseur=
	 * @params id l'id du prof voulu
	 * @return Liste<Sujet> liste contenant le sujet recherché
	 */

	@CrossOrigin
	@RequestMapping(value = "/get/sujetsByIdProfesseur", method = RequestMethod.GET)
	@ResponseBody
	public Object getSujetByIdProfesseur(@RequestParam(required = true, value = "idProfesseur") int value) {
		Message message = new Message();
		List<Sujet> sujets = new ArrayList<Sujet>();
		this.professeurSujetDAO = daoFactory.getProfesseurSujetDao();
		sujets = professeurSujetDAO.listerSujets(value);
		if (sujets.isEmpty()) {
			message.setSuccess(false);
			message.setMessage(
					"Aucun sujet correspondant à cet identifiant professeur n'est présent dans la base de données");
			return message;
		} else {
			return sujets;
		}
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * JURYS * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * *
	 */

	/**
	 * @Description Renvoie la liste de tous les jurys 
	 * @Path /get/jurys
	 * @Params none
	 * @Result Liste<Jurys>
	 */

	@CrossOrigin
	@RequestMapping(value = "/get/jurys", method = RequestMethod.GET)
	@ResponseBody
	public Object getJurys(/*@RequestParam(required = false, value = "value") String value*/) {
		Message message = new Message();
		// Check the token
		System.out.println("Token : " + tokenUser);
		if (tokenUser == null) {
			message.setSuccess(false);
			message.setMessage("Vous devez vous identifier pour accéder à cette requête");
			return message;
		} else {
			// Verifier si le token est valide
			if (!verifyToken(tokenUser)) {
				message.setSuccess(false);
				message.setMessage("Token invalide ! Veuillez vous reconnecter");
				return message;
			} else {
				JWTDecoder jwtDecod = new JWTDecoder(tokenUser);
				System.out.println("JWTDecoder : " + jwtDecod.getClaim("identifiant"));
				System.out.println("Expiration : "+ jwtDecod.getExpiresAt());

				List<Jury> listeJury = new ArrayList<Jury>();
				this.juryDao = daoFactory.getJuryDao();
				listeJury = juryDao.lister();
				if (listeJury.isEmpty()) {
					message.setSuccess(false);
					message.setMessage(
							"Aucun sujet correspondant à cet identifiant professeur n'est présent dans la base de données");
					return message;
				} else {
					return listeJury;
				}
			}

		}
	}

	// ###########################################################################################
	// # Autres
	// ###########################################################################################

	/**
	 * @Description Générer un token
	 * @param user l'utilisateur qui se connecte
	 * @return String token le token généré
	 */
	public String createToken(Utilisateur user) {
		String token = "";
		try {
			Algorithm algorithm = Algorithm.HMAC256(fr.eseo.ld.config.SecurityConstants.SECRET);
			token = JWT.create().withIssuer("auth0").withClaim("idUser", user.getIdUtilisateur())
					.withClaim("identifiant", user.getIdentifiant()).withClaim("nom", user.getNom())
					.withClaim("prenom", user.getPrenom()).sign(algorithm);

		} catch (JWTCreationException exception) {
			// Invalid Signing configuration / Couldn't convert Claims.
		}
		return token;
	}

	/**
	 * @Description Vérifier un token
	 * @param token le token à vérifier
	 * @return Liste<Claims> liste de claims contenus dans la token (payload)
	 */
	public boolean verifyToken(String token) {
		boolean result = false;
		try {
			Algorithm algorithm = Algorithm.HMAC256(fr.eseo.ld.config.SecurityConstants.SECRET);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); // Reusable verifier instance
			DecodedJWT jwt = verifier.verify(token);
			result = true;
		} catch (JWTVerificationException exception) {
			// Invalid signature/claims
			result = false;
		}
		return result;
	}

	/**
	 * @Description Décoder un token
	 * @param token le token à décoder
	 * @return Liste<Claims> liste de claims contenus dans la token (payload)
	 */
	public Object decodeToken(String token) {
		Message message = new Message();
		List<Claim> claims_array = new ArrayList<Claim>();
		try {
			DecodedJWT jwt = JWT.decode(token);
			Claim claim1 = jwt.getClaim("idUser");
			Claim claim2 = jwt.getClaim("identifiant");
			Claim claim3 = jwt.getClaim("nom");
			Claim claim4 = jwt.getClaim("prenom");
			System.out.println("Claim identifiant " + claim2.asString());
			claims_array.add(claim1);
			claims_array.add(claim2);
			claims_array.add(claim3);
			claims_array.add(claim4);
			return claims_array;
		} catch (JWTDecodeException exception) {
			// Invalid token
			message.setSuccess(false);
			message.setMessage("Echec décodage token");
			return message;
		}

	}

}
