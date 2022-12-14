package fr.eni.javaee.auctions.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.eni.javaee.auctions.be.BusinessException;
import fr.eni.javaee.auctions.bo.Utilisateur;

public class UtilisateurDAOJdbcImpl implements UtilisateurDAO {
	private static final String MODIFIER = "UPDATE Utilisateurs SET pseudo = ?, nom = ? ,  prenom = ?,  email = ?,  telephone = ?,  rue = ?,  code_postal = ?,  ville = ?,  mot_de_passe = ? WHERE no_utilisateur = ? ;";
	private static final String INSERT = "INSERT INTO Utilisateurs (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String SELECT_ID_CONNEXION = " SELECT * FROM utilisateurs WHERE (pseudo = ? or email = ?) and mot_de_passe = ? ;";
	private static final String SELECT_PSEUDO_EMAIL = "SELECT pseudo, email FROM Utilisateurs WHERE pseudo = ? and email = ? ;";
	private static final String REMOVE_USER = "DELETE FROM Utilisateurs WHERE no_utilisateur = ? ;";
	private static final String SELECT_ON_ID = "SELECT * FROM utilisateurs WHERE no_utilisateur = ?;";
	private static final String MODIFIER_CREDIT = "UPDATE Utilisateurs SET credit = ? WHERE no_utilisateur = ? ;";
	
	

	public void modifierCredit (Utilisateur utilisateur) {		
		
		try ( Connection cnx = ConnectionProvider.getConnection() ) {
			PreparedStatement pstmt = cnx.prepareStatement(MODIFIER_CREDIT);			
			pstmt.setInt(2, utilisateur.getNoUtilisateur());
			pstmt.setInt(1, utilisateur.getCredit());
			pstmt.executeUpdate();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void delete(Utilisateur utilisateur) {

		try (Connection cnx = ConnectionProvider.getConnection()) {
			PreparedStatement pstmt = cnx.prepareStatement(REMOVE_USER);
			pstmt.setInt(1, utilisateur.getNoUtilisateur());
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean uniciteIdentifiant(String pseudo, String email) {
		Utilisateur userVerif = null;
		boolean verif = false;

		try (Connection cnx = ConnectionProvider.getConnection()) {
			PreparedStatement pstmt = cnx.prepareStatement(SELECT_PSEUDO_EMAIL);
			ResultSet rs = null;
			pstmt.setString(1, pseudo);
			pstmt.setString(2, email);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				userVerif = new Utilisateur();
				userVerif.setPseudo(rs.getString("pseudo"));
				userVerif.setEmail(rs.getString("email"));
			}
			System.out.println(userVerif);
			if (userVerif == null) {
				verif = true;
			} else {
				verif = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return verif;

	}

	@Override
	public void insert(Utilisateur utilisateur) throws SQLException, BusinessException {

		try (Connection cnx = ConnectionProvider.getConnection()) {

			if (utilisateur == null) {
				BusinessException be = new BusinessException();
				be.ajouterErreur(CodesErreursUtilisateurDAL.INSERT_OBJECT_NULL);
				throw be;
			}

			PreparedStatement pstmt = cnx.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);

			pstmt.setString(1, utilisateur.getPseudo());
			pstmt.setString(2, utilisateur.getNom());
			pstmt.setString(3, utilisateur.getPrenom());
			pstmt.setString(4, utilisateur.getEmail());
			pstmt.setString(5, utilisateur.getTelephone());
			pstmt.setString(6, utilisateur.getRue());
			pstmt.setString(7, utilisateur.getCodePostal());
			pstmt.setString(8, utilisateur.getVille());
			pstmt.setString(9, utilisateur.getMotDePasse());
			pstmt.setInt(10, utilisateur.getCredit());
			pstmt.setBoolean(11, false);

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			BusinessException be = new BusinessException();
			be.ajouterErreur(CodesErreursUtilisateurDAL.INSERT_OBJECT_ECHEC);
			throw be;
		}
	}

	public Utilisateur verifUtilisateur(String pseudo, String mdp) throws SQLException, BusinessException {

		Utilisateur utilisateurCnx = null;

		try (Connection cnx = ConnectionProvider.getConnection()) {

			PreparedStatement pstmt = cnx.prepareStatement(SELECT_ID_CONNEXION);
			ResultSet rs = null;
			pstmt.setString(1, pseudo);
			pstmt.setString(2, pseudo);
			pstmt.setString(3, mdp);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				utilisateurCnx = new Utilisateur();
				utilisateurCnx.setNoUtilisateur(rs.getInt("no_utilisateur"));
				utilisateurCnx.setPseudo(rs.getString("pseudo"));
				utilisateurCnx.setNom(rs.getString("nom"));
				utilisateurCnx.setPrenom(rs.getString("prenom"));
				utilisateurCnx.setEmail(rs.getString("email"));
				utilisateurCnx.setTelephone(rs.getString("telephone"));
				utilisateurCnx.setRue(rs.getString("rue"));
				utilisateurCnx.setCodePostal(rs.getString("code_postal"));
				utilisateurCnx.setVille(rs.getString("ville"));
				utilisateurCnx.setMotDePasse(rs.getString("mot_de_passe"));
				utilisateurCnx.setCredit(rs.getInt("credit"));

			}

		} catch (Exception e) {
			e.printStackTrace();
			BusinessException be = new BusinessException();
			throw be;
		}
		System.out.println(utilisateurCnx);
		return utilisateurCnx;
	}

	/**
	 * M??thode retournant l'utilisateur correspondant au num??ro pass?? en param??tre
	 */
	@Override
	public Utilisateur profilUtilisateur(int idUser) throws BusinessException {

		Utilisateur utilisateurProfil = null;

		try (Connection cnx = ConnectionProvider.getConnection()) {
			
			//appel de la m??thode surcharg??e
			utilisateurProfil = profilUtilisateur(idUser, cnx);
			
		} catch (SQLException e) { //erreur li??e ?? la connexion
			e.printStackTrace();
			BusinessException be = new BusinessException();
			throw be;
		}
		System.out.println("PROFIL " + utilisateurProfil);
		return utilisateurProfil;
	}
	
	/**
	 * Surcharge de la m??thode retournant l'utilisateur correspondant au num??ro pass?? en param??tre
	 * @author mberger2022
	 * @param idUser : num??ro de l'utilisateur recherch??
	 * @param cnx : objet de type Connection permettant d'utiliser une connexion d??j?? ouverte
	 * @return Objet de type Utilisateur
	 * @throws BusinessException
	 */
	public Utilisateur profilUtilisateur(int idUser, Connection cnx) throws BusinessException {

		Utilisateur utilisateurProfil = null;

		try {
			PreparedStatement pstmt = cnx.prepareStatement(SELECT_ON_ID);
			ResultSet rs = null;
			pstmt.setInt(1, idUser);
			rs = pstmt.executeQuery();
	  
			while (rs.next()) {
				utilisateurProfil = new Utilisateur();
				utilisateurProfil.setNoUtilisateur(rs.getInt("no_utilisateur"));
				utilisateurProfil.setPseudo(rs.getString("pseudo"));
				utilisateurProfil.setNom(rs.getString("nom"));
				utilisateurProfil.setPrenom(rs.getString("prenom"));
				utilisateurProfil.setEmail(rs.getString("email"));
				utilisateurProfil.setTelephone(rs.getString("telephone"));
				utilisateurProfil.setRue(rs.getString("rue"));
				utilisateurProfil.setCodePostal(rs.getString("code_postal"));
				utilisateurProfil.setVille(rs.getString("ville"));
				utilisateurProfil.setCredit(rs.getInt("credit"));
			}

		} catch (SQLException e) { // erreur li??e ?? la requ??te effectu??e
			e.printStackTrace();
			BusinessException be = new BusinessException();
			throw be;
		}
		System.out.println("PROFIL-CNX " + utilisateurProfil);
		return utilisateurProfil;
	}

	/**
	 * Methode permettant de modifier les donn??es de l'utilisateur / cas particulier si l'user change de mdp 
	 */
	@Override
	public void modifier(Utilisateur utilisateur, String mdpSession) throws BusinessException {

		try (Connection cnx = ConnectionProvider.getConnection()) {
			PreparedStatement pstmt = cnx.prepareStatement(MODIFIER);

			pstmt.setString(1, utilisateur.getPseudo());
			pstmt.setString(2, utilisateur.getNom());
			pstmt.setString(3, utilisateur.getPrenom());
			pstmt.setString(4, utilisateur.getEmail());
			pstmt.setString(5, utilisateur.getTelephone());
			pstmt.setString(6, utilisateur.getRue());
			pstmt.setString(7, utilisateur.getCodePostal());
			pstmt.setString(8, utilisateur.getVille());
			if (utilisateur.getMotDePasse().equals("") || utilisateur.getMotDePasse().isBlank()
					|| utilisateur.getMotDePasse() == null) {
				pstmt.setString(9, mdpSession);
			} else {				
				pstmt.setString(9, utilisateur.getMotDePasse());
			}			
			pstmt.setInt(10, utilisateur.getNoUtilisateur());

			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			BusinessException be = new BusinessException();
			throw be;
		}
	}

}
