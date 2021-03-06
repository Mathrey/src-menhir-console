package partie;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedList;

import joueur.Joueur;


import carte.Carte;
import carte.ChienDeGarde;
import carte.Ingredient;
import carte.TaupeGeante;

/**
 * Cette classe permet de cr�er une manche. Une partie peut �tre compos�e de plusieurs manches qui se succ�dent, mais les manches sont lanc�es les unes apr�s les autres
 * @author Mathieu & Laurie
 *
 */
public class Manche {

	/**
	 * La saison en cours sous forme d'entier
	 */
	private int saisonEnCours;
	/**
	 * Un tableau de cha�nes de caract�res correspondant aux saisons possibles
	 */
	private String listeSaison[] = { "printemps", "�t�", "automne", "hiver" };
	/**
	 * La liste de toutes les cartes ingr�dients
	 */
	private LinkedList<Carte> listeCIngredients = new LinkedList<Carte>();
	/**
	 * La liste de toutes les cartes alli�s
	 */
	private LinkedList<Carte> listeCAllies = new LinkedList<Carte>();

	/**
	 * Le constructeur d'une manche qui intialise les saisons � 0 (ce qui correspond d'apr�s le tableau des saisons � la saison hiver)
	 * @see Manche#listeSaison
	 */
	public Manche() {
		saisonEnCours = 0;
	}
	
	/**
	 * Modifie la saison en cours
	 * @param saisonEnCours La nouvelle saison
	 */
	public void setSaisonEnCours(int saisonEnCours) {
		this.saisonEnCours = saisonEnCours;
	}

	/**
	 * Instancie toutes les cartes dont on a besoin en fonction de la partie
	 * Distribue 4 cartes ingr�dients � tous les joueurs
	 * Dans une partie avanc�e, fait appel � la fonction permettant de savoir si un joueur veut une carte alli� ou recevoir deux graines
	 * @see Manche#initialisationListeCarte()
	 * @see Manche#demanderGrainesOuCarteAllie(Joueur)
	 */
	public void distribuerCarteJoueur() {
		Partie p = Partie.getInstance();
		if (p.getPartieAvancee()) {

			this.initialisationListeCarte();
			Collections.shuffle(listeCIngredients);
			Collections.shuffle(listeCAllies);

			for (Iterator<Joueur> it = p.getListeJoueur().iterator(); it
					.hasNext();) {
				Joueur joueurActif = it.next();
				demanderGrainesOuCarteAllie(joueurActif);
			}

			for (Iterator<Joueur> it = p.getListeJoueur().iterator(); it.hasNext();) {
				Joueur joueurActif = it.next();
				for (int i = 0; i < 4; i++) {
					Carte c = this.listeCIngredients.removeFirst();
					joueurActif.getMainDuJoueur().add(c);
				}
			}
		} else {
			this.initialisationListeCarte();
			Collections.shuffle(listeCIngredients);

			for (Iterator<Joueur> it = p.getListeJoueur().iterator(); it
					.hasNext();) {
				Joueur joueurActif = it.next();
				for (int i = 0; i < 4; i++) {
					Carte c = this.listeCIngredients.removeFirst();
					joueurActif.getMainDuJoueur().add(c);
				}
				this.listeCIngredients.removeAll(joueurActif.getMainDuJoueur());
			}
		}
	}

	/**
	 * Dmande aux joueurs s'ils veulent une carte alli� ou recevoir 2 graines avant de commencer la partie
	 * @param j Le joueur en train de recevoir ses cartes
	 */
	public void demanderGrainesOuCarteAllie(Joueur j) {
		Partie p = Partie.getInstance();
		System.out.println(j.getNom() + " choisissez-vous une carte alli� (1) ou deux graines (2)?\n");
		int choix = 0;
		do {
			try {
				choix = p.getChoix();
				if (choix == 1) {
					Carte c = listeCAllies.get(0);
					j.getMainDuJoueur().add(c);
					listeCAllies.remove(0);
				} else {
					if (choix == 2) {
						j.setNbGraineDuJoueur(2);
					} else {
						System.out.println("Choix incorrect.\n");
					}
				}
			} catch (InputMismatchException e) {
				System.out
						.println("Veuillez entrer votre choix en tapant 1 ou 2.\n");
			}
		} while (choix != 1 && choix != 2);
	}
	
	/**
	 * M�thode permettant de d�signer le joueur qui commencera � jouer � chaque saison
	 * D'apr�s les r�gles du jeu, lors de la premi�re manche c'est le joueur le plus jeune qui commence
	 * Pour les manches suivantes dans une partie avanc�e, chaque joueur doit commencer � jouer une manche
	 */
	public void attribuerJoueurDeDebut() {
		Partie p = Partie.getInstance();
		Joueur jQuiCommence = null;
		if (p.getPartieAvancee()) {
			int ageMini = 130;
			for (Iterator<Joueur> it = p.getListeJoueur().iterator(); it
					.hasNext();) {
				Joueur joueurActif = it.next();
				if ((ageMini > joueurActif.getAge())
						&& !(joueurActif.getADejaCommence())) {
					jQuiCommence = joueurActif;
					ageMini = joueurActif.getAge();
				}
			}
			int a = p.getListeJoueur().indexOf(p.getListeJoueur().get(0));
			int b = p.getListeJoueur().indexOf(jQuiCommence);
			jQuiCommence.setADejaCommence(true);
			Collections.swap(p.getListeJoueur(), a, b);
		} else {
			if (p.getListeJoueur().get(0).getAge() < p.getListeJoueur().get(1)
					.getAge()) {
				jQuiCommence = p.getListeJoueur().get(0);
			} else {
				jQuiCommence = p.getListeJoueur().get(1);
			}

			for (int i = 2; i < p.getListeJoueur().size(); i++) {

				if (p.getListeJoueur().get(i).getAge() < jQuiCommence.getAge()) {
					jQuiCommence = p.getListeJoueur().get(i);
				}

				int a = p.getListeJoueur().indexOf(p.getListeJoueur().get(0));
				int b = p.getListeJoueur().indexOf(jQuiCommence);

				Collections.swap(p.getListeJoueur(), a, b);
			}
		}
	}
	
	/**
	 * Renvoie la saison en cours
	 * @return La saison en cours sous forme d'entier
	 */
	public int getSaisonEnCours() {
		return this.saisonEnCours;
	}
	
	/**
	 * M�thode qui fait se d�rouler une manche en entier et jouer chaque joueur
	 * @see Manche#attribuerJoueurDeDebut()
	 * @see Manche#distribuerCarteJoueur()
	 * @see Joueur#jouerCarte(Manche, Partie)
	 * @see Manche#changerSaison()
	 */
	public void jouerManche() {
		Partie p = Partie.getInstance();
		boolean partieAvancee = p.getPartieAvancee();
		if (partieAvancee) {
			this.attribuerJoueurDeDebut();
			this.distribuerCarteJoueur();
			System.out.println("\n-----------------\nD�but de la manche "
					+ p.getMancheNumero() + "!\n");

			for (int i = 0; i < 4; i++) {
				for (Iterator<Joueur> it = p.getListeJoueur().iterator(); it
						.hasNext();) {
					Joueur joueurActif = it.next();
					System.out.println("\nSAISON EN COURS : "
							+ this.listeSaison[this.getSaisonEnCours()] + "\n");
					System.out.println("C'est � " + joueurActif.getNom()
							+ " de jouer.\n");
					joueurActif.jouerCarte(this, p);
				}
				this.changerSaison();
			}

			for (Iterator<Joueur> it = p.getListeJoueur().iterator(); it
					.hasNext();) {
				Joueur joueurActif = it.next();
				joueurActif.getMainDuJoueur().clear();
				joueurActif.setNbChiens(0);
				joueurActif.setNbGraineDuJoueur(0);
			}

		} else {
			this.attribuerJoueurDeDebut();
			this.distribuerCarteJoueur();
			System.out.println("\n-----------------\nD�but de la manche!\n");

			for (int i = 0; i < 4; i++) {
				for (Iterator<Joueur> it = p.getListeJoueur().iterator(); it
						.hasNext();) {
					Joueur joueurActif = it.next();
					System.out.println("\nSAISON EN COURS : "
							+ this.listeSaison[this.getSaisonEnCours()] + "\n");
					System.out.println("C'est � " + joueurActif.getNom()
							+ " de jouer.\n");
					joueurActif.jouerCarte(this, p);
				}
				this.changerSaison();
			}
		}
	}
	
	/**
	 * Renvoie la collection de toutes les cartes ingr�dients
	 * @return Les cartes ingr�dients
	 */
	public LinkedList<Carte> getListeCIngredients() {
		return this.listeCIngredients;
	}
	
	/**
	 * Renvoie la collection de toutes les cartes alli�s
	 * @return Les cartes alli�s
	 */
	public LinkedList<Carte> getListeCAllies() {
		return this.listeCAllies;
	}
	/**
	 * Change la saison en cours
	 */
	public void changerSaison() {
		int nouvSaison = this.saisonEnCours;
		System.out.println("La saison " + this.listeSaison[this.saisonEnCours]
				+ " est termin�e.\n");
		nouvSaison++;
		if (nouvSaison == 4) {
			nouvSaison = 0;
		} else {
			System.out.println("La nouvelle saison est "
					+ this.listeSaison[this.saisonEnCours + 1]);
		}
		this.saisonEnCours = nouvSaison;
	}

	/**
	 * Permet de cr�er toutes les cartes utilis�es dans le Jeu du Menhir
	 */
	public void initialisationListeCarte() {

		int tabIng1[][] = { { 1, 1, 1, 1 }, { 2, 0, 1, 1 }, { 2, 0, 2, 0 } };
		int tabIng2[][] = { { 2, 0, 1, 1 }, { 1, 3, 0, 0 }, { 0, 1, 2, 1 } };
		int tabIng3[][] = { { 0, 0, 4, 0 }, { 0, 2, 2, 0 }, { 0, 0, 1, 3 } };
		int tabIng4[][] = { { 1, 3, 1, 0 }, { 1, 2, 1, 1 }, { 0, 1, 4, 0 } };
		int tabIng5[][] = { { 2, 1, 1, 1 }, { 1, 0, 2, 2 }, { 3, 0, 0, 2 } };
		int tabIng6[][] = { { 1, 2, 2, 0 }, { 1, 1, 2, 1 }, { 2, 0, 1, 2 } };
		int tabIng7[][] = { { 2, 1, 1, 2 }, { 1, 1, 1, 3 }, { 2, 0, 2, 2 } };
		int tabIng8[][] = { { 0, 3, 0, 3 }, { 2, 1, 3, 0 }, { 1, 1, 3, 1 } };
		int tabIng9[][] = { { 1, 2, 1, 2 }, { 1, 0, 1, 4 }, { 2, 4, 0, 0 } };
		int tabIng10[][] = { { 1, 3, 1, 2 }, { 2, 1, 2, 2 }, { 0, 0, 3, 4 } };
		int tabIng11[][] = { { 2, 2, 0, 3 }, { 1, 1, 4, 1 }, { 1, 2, 1, 3 } };
		int tabIng12[][] = { { 2, 2, 3, 1 }, { 2, 3, 0, 3 }, { 1, 1, 3, 3 } };
		int tabIng13[][] = { { 2, 2, 3, 1 }, { 2, 3, 0, 3 }, { 1, 1, 3, 3 } };
		int tabIng14[][] = { { 2, 2, 2, 2 }, { 0, 4, 4, 0 }, { 1, 3, 2, 2 } };
		int tabIng15[][] = { { 3, 1, 3, 1 }, { 1, 4, 2, 1 }, { 2, 4, 1, 1 } };
		int tabIng16[][] = { { 4, 1, 1, 1 }, { 1, 2, 1, 3 }, { 1, 2, 2, 2 } };
		int tabIng17[][] = { { 2, 3, 2, 0 }, { 0, 4, 3, 0 }, { 2, 1, 1, 3 } };
		int tabIng18[][] = { { 2, 2, 3, 0 }, { 1, 1, 1, 4 }, { 2, 0, 3, 2 } };
		int tabIng19[][] = { { 3, 1, 4, 1 }, { 2, 1, 3, 3 }, { 2, 3, 2, 2 } };
		int tabIng20[][] = { { 2, 4, 1, 2 }, { 2, 2, 2, 3 }, { 1, 4, 3, 1 } };
		int tabIng21[][] = { { 3, 3, 3, 0 }, { 1, 3, 3, 2 }, { 2, 3, 1, 3 } };
		int tabIng22[][] = { { 1, 2, 2, 1 }, { 1, 2, 3, 0 }, { 0, 2, 2, 2 } };
		int tabIng23[][] = { { 4, 0, 1, 1 }, { 1, 1, 3, 1 }, { 0, 0, 3, 3 } };
		int tabIng24[][] = { { 2, 0, 1, 3 }, { 0, 3, 0, 3 }, { 1, 2, 2, 1 } };

		int tabTaupeGeante1[] = { 1, 1, 1, 1 };
		int tabTaupeGeante2[] = { 0, 2, 2, 0 };
		int tabTaupeGeante3[] = { 0, 1, 2, 1 };
		int tabChienDeGarde1[] = { 2, 0, 2, 0 };
		int tabChienDeGarde2[] = { 1, 2, 0, 1 };
		int tabChienDeGarde3[] = { 0, 1, 3, 0 };

		this.listeCIngredients.add(new Ingredient("ing1", tabIng1,
				"Rayon de Lune"));
		this.listeCIngredients.add(new Ingredient("ing2", tabIng2,
				"Rayon de Lune"));
		this.listeCIngredients.add(new Ingredient("ing3", tabIng3,
				"Rayon de Lune"));
		this.listeCIngredients.add(new Ingredient("ing4", tabIng4,
				"Chant de sir�ne"));
		this.listeCIngredients.add(new Ingredient("ing5", tabIng5,
				"Chant de sir�ne"));
		this.listeCIngredients.add(new Ingredient("ing6", tabIng6,
				"Chant de sir�ne"));
		this.listeCIngredients.add(new Ingredient("ing7", tabIng7,
				"Larmes de dryade"));
		this.listeCIngredients.add(new Ingredient("ing8", tabIng8,
				"Larmes de dryade"));
		this.listeCIngredients.add(new Ingredient("ing9", tabIng9,
				"Larmes de dryade"));
		this.listeCIngredients.add(new Ingredient("ing10", tabIng10,
				"Fontaine d'eau pure"));
		this.listeCIngredients.add(new Ingredient("ing11", tabIng11,
				"Fontaine d'eau pure"));
		this.listeCIngredients.add(new Ingredient("ing12", tabIng12,
				"Fontaine d'eau pure"));
		this.listeCIngredients.add(new Ingredient("ing13", tabIng13,
				"Poudre d'or"));
		this.listeCIngredients.add(new Ingredient("ing14", tabIng14,
				"Poudre d'or"));
		this.listeCIngredients.add(new Ingredient("ing15", tabIng15,
				"Poudre d'or"));
		this.listeCIngredients.add(new Ingredient("ing16", tabIng16,
				"Racine d'arc en ciel"));
		this.listeCIngredients.add(new Ingredient("ing17", tabIng17,
				"Racine d'arc en ciel"));
		this.listeCIngredients.add(new Ingredient("ing18", tabIng18,
				"Racine d'arc en ciel"));
		this.listeCIngredients.add(new Ingredient("ing19", tabIng19,
				"Esprit de dolmen"));
		this.listeCIngredients.add(new Ingredient("ing20", tabIng20,
				"Esprit de dolmen"));
		this.listeCIngredients.add(new Ingredient("ing21", tabIng21,
				"Esprit de dolmen"));
		this.listeCIngredients.add(new Ingredient("ing22", tabIng22,
				"Rires de f�es"));
		this.listeCIngredients.add(new Ingredient("ing23", tabIng23,
				"Rires de f�es"));
		this.listeCIngredients.add(new Ingredient("ing24", tabIng24,
				"Rires de f�es"));

		this.listeCAllies.add(new ChienDeGarde("CdG1", tabChienDeGarde1,
				"Chien de garde"));
		this.listeCAllies.add(new ChienDeGarde("CdG2", tabChienDeGarde2,
				"Chien de garde"));
		this.listeCAllies.add(new ChienDeGarde("CdG3", tabChienDeGarde3,
				"Chien de garde"));
		this.listeCAllies.add(new TaupeGeante("TaupeG1", tabTaupeGeante1,
				"Taupe G�ante"));
		this.listeCAllies.add(new TaupeGeante("TaupeG2", tabTaupeGeante2,
				"Taupe G�ante"));
		this.listeCAllies.add(new TaupeGeante("TaupeG3", tabTaupeGeante3,
				"Taupe G�ante"));
	}
}
