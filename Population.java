import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Population extends Application implements Comparator {
	LinkedList<List50Polygons> l; // si trop long faire avec priorityQueue
	private Population p1;
	private int effectif = 60;
	private int numPoint = 4;
	int nbgeneration; // nb de generations au total
	private static final double epsilon = 0.001;

	public Population() {
		super();
		l = new LinkedList<List50Polygons>();
		nbgeneration = 0;
		for (int i = 0; i < effectif; i++) {
			l.add(new List50Polygons(numPoint, nbgeneration));
		}

	}

	public static void main(String[] args) {
		// System.out.println("debut 1");
		launch(args);

	}

	@Override
	// l'application javafx tourne dans le start suite à l'appel du main

	public void start(Stage primaryStage) throws Exception {
		long startChrono = System.currentTimeMillis();
		p1 = new Population(); // initialisation pop a effectif
		double s1 = 0;
		double s2 = 0;
		int n = 0;
		double V = 1; // on initialise la variance a 1
		double scoreFinal = 50; // deux possibilités pour la condition d'arrêt : elitiste ou quand le score non
								// convient
		double testScore = 200;
		while (V > epsilon) {
			n++; // nombre d'iterations

			p1.l.sort(p1); // on la trie en fonction du score

			selection(p1.l, effectif);// on enleve les 4 pires

			LinkedList<List50Polygons> generationNouvelle = new LinkedList<List50Polygons>();
			nbgeneration++;

			for (int i = 0; i < p1.l.size(); i++) {
				for (int j = i + 1; j < p1.l.size(); j++) {
					LinkedList<List50Polygons> l = p1.crossover(i, j);
					generationNouvelle.add(l.get(0));
					generationNouvelle.add(l.get(1)); // on recupere les enfants on les mets dans une nouvelle
														// generation
				}
			}
			// System.out.println(" taille generationNouvelle" + generationNouvelle.size());

			p1.l = generationNouvelle;

			// muation sur 1 individu de la pop
			
			  int individuChoisi = (int) (Math.random() * (p1.l.size() - 1));
			  List50Polygons individuMute = p1.mutation(p1.l.get(individuChoisi));
			  p1.l.set(individuChoisi, individuMute);
			 
			// mutation sur tous les individus de la pop
			/*for (int i = 0; i < p1.l.size(); i++) {
				List50Polygons individuMute = p1.mutation(p1.l.get(i));
				p1.l.set(i, individuMute);
			}*/

			// calcul de la variance
			p1.l.sort(p1);
			s1 = p1.l.get(l.size() - 1).score; // somme des meilleurs scores le dernier individu est le meilleur
			s2 = (s2 + p1.l.get(l.size() - 1).score) / 2; // somme des carrés des meilleurs scores
			V = Math.pow(s1 - s2, 2.0) / n;
			System.out.println("score " + p1.l.get(p1.l.size() - 1).score);
			System.out.println("n " + n);
			System.out.println("s1 et s2 " + s1 + " " + s2);
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

	}

	@Override
	public int compare(Object o0, Object o1) {
		List50Polygons l0 = (List50Polygons) o0;
		List50Polygons l1 = (List50Polygons) o1;
		if (l0.score < l1.score) {
			return 1;
		} else if (l0.score == l1.score) {
			return 0;
		}
		return -1;
	}

	// on garde 6 elements sur 10 suprimé les 4 premiers voir si on doit pas garder
	// des score bas
	public void selection(LinkedList<List50Polygons> l, int nbIndividus) {
		int k = l.size() - nbIndividus;
		// System.out.println(k);
		for (int i = 0; i < k; i++) {
			l.remove();
		}
		// System.out.println( "taille après selec"+l.size());
	}

	// retourne la liste des enfanst créés par le crossover

	public LinkedList<List50Polygons> crossover(int indiceparent1, int indiceparent2) {

		LinkedList<List50Polygons> progeniture = new LinkedList<List50Polygons>();
		int crossoverPoint = -1;
		while (crossoverPoint <= 0 || crossoverPoint >= 50) { // On exclu le cas ou crossoverPoint=0 ou =50
			crossoverPoint = (int) (Math.random() * 50);

		}
		// System.out.println("crossoverPoint " +crossoverPoint);

		int tmp;
		List50Polygons parent1 = l.get(indiceparent1);
		List50Polygons parent2 = l.get(indiceparent2);
		// System.out.println("test1");
		List50Polygons enfant1 = new List50Polygons(nbgeneration);
		List50Polygons enfant2 = new List50Polygons(nbgeneration);
		// System.out.println("test2");
		for (int i = 0; i < crossoverPoint; i++) { // avant le point de crossover on effectue une copie
			enfant1.list.add(parent1.list.get(i));
			enfant2.list.add(parent2.list.get(i));

		}

		for (int i = crossoverPoint; i < 50; i++) { // avant le point de crossover on effectue une copie
			enfant1.list.add(parent2.list.get(i));
			enfant2.list.add(parent1.list.get(i));

		}

		enfant1.setScore();
		enfant2.setScore();
		// System.out.println("score enfant"+enfant1.score);

		/*
		 * System.out.println(parent1.list); System.out.println(enfant1.list);
		 * System.out.println(parent2.list); System.out.println(enfant2.list);
		 */
		progeniture.add(enfant1);
		progeniture.add(enfant2);

		return progeniture;

	}

	public List50Polygons mutation(List50Polygons ind) {

		int mutationPoint = (int) (Math.random() * 50); // l'indice du polygone qui sera muté
		ConvexPolygon CP = new ConvexPolygon(numPoint);
		ind.list.set(mutationPoint, CP);
		ind.setScore();
		return ind;
	}

}