
import java.util.Comparator;
import java.util.LinkedList;

import javafx.application.Application;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Population extends Application implements Comparator<List50Polygons> {
	LinkedList<List50Polygons> l; // si trop long faire avec priorityQueue
	private Population p1;
	private int effectif = 10;// effectif initial de la population
	int effectifSSPop = 5;// effectif de la sous-population suite à la séléction
	private int numPoint = 6; // nb de sommets max pour les polygons
	private static final double epsilon = 0.5; // seuil d'arret
	private double probaMutation = 0.8;
	// ???
	// vraiment nécessaire d'ajouter la génération à l'individu ? on fait le
	// décompte avec le n
	int nbgeneration;

	public Population() {
		super();
		l = new LinkedList<List50Polygons>();
		nbgeneration = 0;
		for (int i = 0; i < effectif; i++) {
			l.add(new List50Polygons(numPoint, nbgeneration));
		}

	}

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	// l'application javafx tourne dans le start suite à l'appel du main

	public void start(Stage primaryStage) throws Exception {
		long startChrono = System.currentTimeMillis();
		p1 = new Population();
		// pour le calcul de la variance
		double s1 = 0;
		double s2 = 0;
		int n = 0;// nombre de générations

		double V = 1; // on initialise la variance a 1

		// deux possibilités pour la condition d'arrêt : elitiste ou quand le score
		// semble convenable
		double scoreFinal = 50;
		double testScore = 200;

		while (V > epsilon) {
			n++; // nombre d'iterations

			p1.l.sort(p1); // on la trie en fonction du score
			LinkedList<List50Polygons> generationNouvelle = selection(p1.l, effectifSSPop);

			// ????
			nbgeneration++;

			LinkedList<List50Polygons> CrossOver = p1.crossover(generationNouvelle);
			p1.l = mutation(CrossOver);


			// calcul de la variance
			p1.l.sort(p1);
			s1 = p1.l.get(l.size() - 1).score; // somme des meilleurs scores le dernier individu est le meilleur
			s2 = (s2 + p1.l.get(l.size() - 1).score) / 2; // somme des carrés des meilleurs scores
			V = Math.pow(s1 - s2, 2.0) / n;
			
			System.out.println("Variance =" + V);
			// affichage
			testScore = p1.l.get(p1.l.size() - 1).score;
			System.out.println(testScore);
		}
		for (int i = 0; i < p1.l.size(); i++) {
			System.out.println("liste " + i + " " + p1.l.get(i).score);
		}
		System.out.println("----------------------");
		System.out.println("liste size" + p1.l.size());
		Scene scene = Test.start(p1.l.getFirst().list);
		System.out.println("n" + n);
		primaryStage.setScene(scene);

		primaryStage.show();
		System.out.print("chrono  ");
		System.out.println(System.currentTimeMillis() - startChrono);
		System.out.println("nb generation " + nbgeneration);
		System.out.println("score final" + p1.l.get(p1.l.size() - 1).score);
		System.out.println("nb generation " + p1.l.get(p1.l.size() - 1).generation);

	}

	// il faut que la séléction donne un sous-ensemble de la population qu'on
	// utilise ensuite dans le crossover
	public LinkedList<List50Polygons> selection(LinkedList<List50Polygons> l, int nbIndividus) {
		int k = l.size() - nbIndividus;
		// System.out.println(k);
		for (int i = 0; i < k; i++) {
			l.remove();
		}
		// System.out.println( "taille après selec"+l.size());
		return l;
	}

	// retourne la liste des enfants créés par le crossover de même taille qu ela
	// poop initial
	// prend en paramètre la liste crée suite à la sélection
	public LinkedList<List50Polygons> crossover(LinkedList<List50Polygons> l) {
		int nbCouples = effectif / 2;
		System.out.println(" nb couples : " + nbCouples);
		LinkedList<List50Polygons> progeniture = new LinkedList<List50Polygons>();
		int crossoverPoint = -1;
		for (int i = 0; i < nbCouples; i++) {
			int indiceparent1 = (int) (Math.random() * (l.size() - 1));// on choisit les coiuples de parents de façon
																		// totalement aléatoire
			int indiceparent2 = (int) (Math.random() * (l.size() - 1));
			while (crossoverPoint < 0 || crossoverPoint >= 50) { // On garde le cas où =0 les parents restent
																	// indentiques dans ce cas ou =50
				crossoverPoint = (int) (Math.random() * 50);

			}
			// System.out.println("crossoverPoint " +crossoverPoint);

			List50Polygons parent1 = l.get(indiceparent1);
			List50Polygons parent2 = l.get(indiceparent2);
			// System.out.println("test1");
			List50Polygons enfant1 = new List50Polygons(nbgeneration);
			List50Polygons enfant2 = new List50Polygons(nbgeneration);
			// System.out.println("test2");
			for (int j = 0; j < crossoverPoint; j++) { // avant le point de crossover on effectue une copie
				enfant1.list.add(parent1.list.get(j));
				enfant2.list.add(parent2.list.get(j));

			}

			for (int k = crossoverPoint; k < 50; k++) { // avant le point de crossover on effectue une copie
				enfant1.list.add(parent2.list.get(k));
				enfant2.list.add(parent1.list.get(k));

			}

			enfant1.setScore();
			enfant2.setScore();
			progeniture.add(enfant1);
			progeniture.add(enfant2);
		}
		System.out.println("progeniture  " + progeniture.size());
		return progeniture;

	}

	public LinkedList<List50Polygons> mutation(LinkedList<List50Polygons> l) {
		for (int i = 0; i < p1.l.size(); i++) {
			double proba = Math.random();
			if (proba < probaMutation) {
				int mutationPoint = (int) (Math.random() * 50);
				ConvexPolygon CP = new ConvexPolygon(numPoint);
				l.get(i).list.set(mutationPoint, CP);
				l.get(i).setScore();
			}
		}
		return l;
	}

	@Override
	public int compare(List50Polygons l0, List50Polygons l1) {
		if (l0.score < l1.score) {
			return 1;
		} else if (l0.score == l1.score) {
			return 0;
		}
		return -1;
	}

}