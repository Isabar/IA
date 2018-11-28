import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class List50Polygons implements Comparable{
	LinkedList<ConvexPolygon> list;
	double score;
	
	int generation;
	
	public List50Polygons(int generation ) {
		super();
		list=new LinkedList<ConvexPolygon>();
		this.generation=generation;
		}

	public List50Polygons(int n, int generation) {// création de liste avec 50 polygons avec n nb de sommets max des polygones
		list=new LinkedList<ConvexPolygon>();
		this.generation=generation;
		initialisationImage();
		for ( int i=0; i<50; i++) {
			list.add(new ConvexPolygon(n)); // nb de sommet max des polygons 
		}
		//System.out.println("création liste ");
		score = score(list);
	}

	// on initialise les var max de Convexpolygon
	public static  void initialisationImage() {
		String targetImage = "monaLisa-100.jpg";
		int maxX=0;
    	int maxY=0;
    	
		try{
			BufferedImage bi = ImageIO.read(new File(targetImage));
			maxX = bi.getWidth();
			maxY = bi.getHeight();
        	ConvexPolygon.max_X= maxX;
        	ConvexPolygon.max_Y= maxY;
        }
        catch(IOException e){
        	System.err.println(e);
        	System.exit(9);
        }
		//System.out.println("Read target image " + targetImage + " " + maxX + "x" + maxY);
	}
	
	public static double score(List<ConvexPolygon> ls){
		// formation de l'image par superposition des polygones
		String targetImage = "monaLisa-100.jpg";
		// initialisation de l'image à tout blanc ? 
		Color[][] target=null;
		int maxX=0;
    	int maxY=0;
    	
		try{
			BufferedImage bi = ImageIO.read(new File(targetImage));
			maxX = bi.getWidth();
			maxY = bi.getHeight();
        	ConvexPolygon.max_X= maxX;
        	ConvexPolygon.max_Y= maxY;
        	target = new Color[maxX][maxY];
        	for (int i=0;i<maxX;i++){
        		for (int j=0;j<maxY;j++){
        			int argb = bi.getRGB(i, j);
        			int b = (argb)&0xFF;
        			int g = (argb>>8)&0xFF;
        			int r = (argb>>16)&0xFF;
        			int a = (argb>>24)&0xFF;
        			target[i][j] = Color.rgb(r,g,b);
        		}
        	}
        }
        catch(IOException e){
        	System.err.println(e);
        	System.exit(9);
        }
		//System.out.println("Read target image " + targetImage + " " + maxX + "x" + maxY);
		Group image = new Group();
		for (ConvexPolygon p : ls)
			image.getChildren().add(p);
		
		// Calcul de la couleur de chaque pixel.Pour cela, on passe par une instance de 
		// WritableImage, qui possède une méthode pour obtenir un PixelReader.
		WritableImage wimg = new WritableImage(maxX,maxY);
		image.snapshot(null,wimg);
		PixelReader pr = wimg.getPixelReader();
		// On utilise le PixelReader pour lire chaque couleur
		// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
		// de la couleur du pixel cible et celui du pixel de l'image générée	
		double res=0;
		for (int i=0;i<maxX;i++){
			for (int j=0;j<maxY;j++){
				Color c = pr.getColor(i, j);
				res += Math.pow(c.getBlue()-target[i][j].getBlue(),2)
				+Math.pow(c.getRed()-target[i][j].getRed(),2)
				+Math.pow(c.getGreen()-target[i][j].getGreen(),2);
			}
		}
		System.out.println("score score "+Math.sqrt(res));
		return Math.sqrt(res);
	}
	
	public String toString(){
		String result=new String();
		for(int i=0;i<list.size();i++) {
			result=result+list.get(i).toString();
		}
	   return  result + " " +generation;
	}


	@Override
	public int compareTo(Object o) {
		if ( o instanceof List50Polygons)
		if ( this.score< ((List50Polygons)o).score) {
			return -1;
		}
		else {
			return 1; 
		}
		return 0;
	}

	public void setScore() {
		this.score = score(list);
		System.out.println("set score"+score);
	}

}