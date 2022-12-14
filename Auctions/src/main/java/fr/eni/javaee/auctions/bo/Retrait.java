package fr.eni.javaee.auctions.bo;

import java.io.Serializable;
/**
 * 
 * @author mberger2022
 *
 */
public class Retrait implements Serializable {
	//ATTRIBUTS D'INSTANCE
	private String rue; 			//NOT NULL - VARCHAR(30)
	private String codePostal; 		//NOT NULL - VARCHAR(15)
	private String ville; 			//NOT NULL - VARCHAR(30)
	
	//CONSTRUCTEUR VIDE
	public Retrait( ) { }
	
	public Retrait(String rue, String codePostal, String ville) {
		this();
		this.rue = rue;
		this.codePostal = codePostal;
		this.ville = ville;
	}

	//AUTRES METHODES D'INSTANCE
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Retrait [rue=");
		builder.append(rue);
		builder.append(", codePostal=");
		builder.append(codePostal);
		builder.append(", ville=");
		builder.append(ville);
		builder.append("]");
		return builder.toString();
	}
	
	//GETTERS ET SETTERS
	public String getRue() {
		return rue;
	}
	
	public void setRue(String rue) {
		this.rue = rue;
	}
	public String getCodePostal() {
		return codePostal;
	}
	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}
	

	

}
