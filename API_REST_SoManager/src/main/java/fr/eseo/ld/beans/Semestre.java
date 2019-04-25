package fr.eseo.ld.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe du bean Semestre.
 * 
 * <p>Définition des attributs de l'objet Semestre ainsi que des accesseurs et des mutateurs.</p>
 * 
 * @author Lilian BRAUD
 */
public class Semestre {
	
	public static final String SEMESTRE_GL = "S8";
	public static final String SEMESTRE_PFE = "S9";

	private Long idSemestre;
	private Date dateDebut;	
	private Date dateFin;
	private String nom;
	
	private static final String FORMAT_DATE = "yyyy-MM-dd";
	
	
	/**
	 * Constructeur par défaut.
	 */
	public Semestre() {
		super();
	}
	
	public Long getIdSemestre() {
		return idSemestre;
	}
	
	public void setIdSemestre(Long idSemestre) {
		this.idSemestre = idSemestre;
	}
	
	public Date getDateDebut() {
		return dateDebut;
	}

	public String getDateDebutString() {
		String dateDebutString = null;
		if(dateDebut != null) {
			SimpleDateFormat formatDate = new SimpleDateFormat(FORMAT_DATE);
			dateDebutString = formatDate.format(dateDebut);
		}
		return dateDebutString;
	}
	
	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}
	
	public String getDateFinString() {
		String dateFinString = null;
		if(dateFin != null) {
			SimpleDateFormat formatDate = new SimpleDateFormat(FORMAT_DATE);
			dateFinString = formatDate.format(dateFin);
		}
		return dateFinString;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}	
	
	/**
	 * Redéfinition de la méthode toString().
	 */
	@Override
	public String toString() {
		return "Semestre [idSemestre=" + this.idSemestre + ", dateDebut=" + this.dateDebut + ", dateFin=" + this.dateFin 
				+ ", nom=" + this.nom + "]";
	}
}
