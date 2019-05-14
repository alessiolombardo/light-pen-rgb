
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * Gestione del colore della LightPen RGB e relative conversioni
 * 
 * @author Alessio Lombardo
 * @author Mariella Bonomo
 * @version 1.1
 */
public class RGBPenColor {

	/**
	 * Colore di calibrazione
	 */
	public static RGBPenColor RED_CAL, YELLOW_CAL, GREEN_CAL, CYAN_CAL, BLUE_CAL, MAGENTA_CAL, BLACK_CAL, WHITE_CAL;

	/**
	 * Valore di aggiustamento per la saturazione (discriminazione del bianco)
	 */
	private final static double adjSat = 0.08;

	/**
	 * Valore di aggiustamento per la saturazione (discriminazione dei colori puri)
	 */
	private final static double adjSat2 = 0.00;

	/**
	 * Valore di aggiustamento per la brillantezza
	 */
	private final static double adjBrightness = 0.5;

	/**
	 * Canale Rosso
	 */
	private int R;

	/**
	 * Canale Verde
	 */
	private int G;

	/**
	 * Canale Blu
	 */
	private int B;

	/**
	 * Canale Bianco
	 */
	private int W;

	/**
	 * Costruttore
	 * 
	 * @param r
	 *            Canale Rosso
	 * @param g
	 *            Canale Verde
	 * @param b
	 *            Canale Blu
	 * @param w
	 *            Canale Bianco
	 */
	public RGBPenColor(int r, int g, int b, int w) {
		super();
		R = r;
		G = g;
		B = b;
		W = w;
	}

	/**
	 * Restituisce valore del canale Rosso
	 * 
	 * @return Rosso
	 */
	public int getR() {
		return R;
	}

	/**
	 * Imposta valore del canale Rosso
	 * 
	 * @param r
	 *            Rosso
	 */
	public void setR(int r) {
		R = r;
	}

	/**
	 * Restituisce valore del canale Verde
	 * 
	 * @return Verde
	 */
	public int getG() {
		return G;
	}

	/**
	 * Imposta valore del canale Verde
	 * 
	 * @param g
	 *            Verde
	 */
	public void setG(int g) {
		G = g;
	}

	/**
	 * Restituisce valore del canale Blu
	 * 
	 * @return Blu
	 */
	public int getB() {
		return B;
	}

	/**
	 * Imposta valore del canale Blu
	 * 
	 * @param b
	 *            Blu
	 */
	public void setB(int b) {
		B = b;
	}

	/**
	 * Restituisce valore del canale Bianco
	 * 
	 * @return Bianco
	 */
	public int getW() {
		return W;
	}

	/**
	 * Imposta valore del canale Bianco
	 * 
	 * @param w
	 *            Bianco
	 */
	public void setW(int w) {
		W = w;
	}

	/**
	 * Calcola la tonalità dello spazio HSB partendo dal colore RGBW della penna
	 * 
	 * @return Tonalità
	 */
	private double getHue() {
		// Calcolo le distanze con i colori di calibrazione della penna
		List<Double> dist = new ArrayList<Double>(); // Vettore delle distanze
		Point3D p = new Point3D(R, G, B); // Trasformo il colore della penna in un punto 3D
		dist.add(p.distance(RED_CAL.getR(), RED_CAL.getG(), RED_CAL.getB()));
		dist.add(p.distance(YELLOW_CAL.getR(), YELLOW_CAL.getG(), YELLOW_CAL.getB()));
		dist.add(p.distance(GREEN_CAL.getR(), GREEN_CAL.getG(), GREEN_CAL.getB()));
		dist.add(p.distance(CYAN_CAL.getR(), CYAN_CAL.getG(), CYAN_CAL.getB()));
		dist.add(p.distance(BLUE_CAL.getR(), BLUE_CAL.getG(), BLUE_CAL.getB()));
		dist.add(p.distance(MAGENTA_CAL.getR(), MAGENTA_CAL.getG(), MAGENTA_CAL.getB()));

		double mindist = Collections.min(dist); // Trovo la minima distanza
		int mincolor = dist.indexOf(mindist); // Trovo il colore alla minima distanza

		// Trovo colori adiacenti del colore alla minima distanza
		int leftcolor = mincolor - 1;
		if (leftcolor == -1)
			leftcolor = 5;
		int rightcolor = mincolor + 1;
		if (rightcolor == 6)
			rightcolor = 0;

		// Trovo la seconda minima distanza confrontando i colori adiacenti del colore
		// alla minima distanza
		int mincolor2;
		double mindist2;
		if (dist.get(leftcolor) < dist.get(rightcolor)) {
			mindist2 = dist.get(leftcolor);
			mincolor2 = leftcolor;
		} else {
			mindist2 = dist.get(rightcolor);
			mincolor2 = rightcolor;
		}

		double prop = mindist / (mindist + mindist2); // Calcolo proporzione fra minima distanza e seconda minima
														// distanza

		// Prelevo la tonalità dai 6 colori principali
		List<Double> hsb = new ArrayList<Double>(); // Vettore delle tonalità HSB
		hsb.add(Color.RED.getHue());
		hsb.add(Color.YELLOW.getHue());
		hsb.add(Color.GREEN.getHue());
		hsb.add(Color.CYAN.getHue());
		hsb.add(Color.BLUE.getHue());
		hsb.add(Color.MAGENTA.getHue());

		if (mincolor == 0 && mincolor2 == 5) { // Aggiustamento necessario se ci si trova fra l'ultimo colore e il primo
			hsb.add(Color.RED.getHue()); // Inserisco ulteriore elemento ROSSO alla fine
			mincolor = 6;
		}

		// Calcolo della tonalità tramite interpolazione
		if (mincolor < mincolor2) {
			return hsb.get(mincolor) + 60 * prop;
		} else {
			return hsb.get(mincolor) - 60 * prop;
		}

	}

	/**
	 * Calcola la saturazione dello spazio HSB partendo dal colore RGBW della penna
	 * 
	 * @return Saturazione
	 */
	private double getSaturation() {
		double saturationR = ((double) (R - WHITE_CAL.getR()) / (double) (WHITE_CAL.getR()));
		double saturationG = ((double) (G - WHITE_CAL.getG()) / (double) (WHITE_CAL.getG()));
		double saturationB = ((double) (B - WHITE_CAL.getB()) / (double) (WHITE_CAL.getB()));
		double saturationW = ((double) (W - WHITE_CAL.getW()) / (double) (WHITE_CAL.getW()));
		double saturation = (saturationR + saturationG + saturationB + saturationW) / 4;

		if (saturation < adjSat) {
			return 0;
		} else {
			if (Controller.isSaturationEnabled()) {

				// Saturazione del colore letto
				List<Integer> coordinate = new ArrayList<Integer>();
				coordinate.add(R);
				coordinate.add(G);
				coordinate.add(B);
				coordinate.add(W);
				double max = Collections.max(coordinate);
				double min = Collections.min(coordinate);
				double sat2 = ((max - min) / max) * ((max - min) / max);

				// Saturazione del nero di calibrazione
				List<Integer> coordinateB = new ArrayList<Integer>();
				coordinateB.add(BLACK_CAL.getR());
				coordinateB.add(BLACK_CAL.getG());
				coordinateB.add(BLACK_CAL.getB());
				coordinateB.add(BLACK_CAL.getW());
				double maxB = Collections.max(coordinateB);
				double minB = Collections.min(coordinateB);
				double sat2B = ((maxB - minB) / maxB) * ((maxB - minB) / maxB);

				if (sat2 > sat2B + adjSat2) {
					return 1;
				} else {
					return sat2 / 4;
				}

			} else
				return 1;
		}
	}

	/**
	 * Calcola la brillantezza (luminosità) dello spazio HSB partendo dal colore
	 * RGBW della penna
	 * 
	 * @return Brillantezza (Luminosità)
	 */
	private double getBrightness() {
		double brightnessR = ((double) (BLACK_CAL.getR() - R) / (double) (BLACK_CAL.getR()));
		double brightnessG = ((double) (BLACK_CAL.getG() - G) / (double) (BLACK_CAL.getG()));
		double brightnessB = ((double) (BLACK_CAL.getB() - B) / (double) (BLACK_CAL.getB()));
		double brightnessW = ((double) (BLACK_CAL.getW() - W) / (double) (BLACK_CAL.getW()));
		double brightness = (brightnessR + brightnessG + brightnessB + brightnessW) / 4 + adjBrightness;

		if (getSaturation() < 1 && getSaturation() != 0) {
			brightness -= 0.4;
		}

		// Reimposto il valore nei limiti [0 - 1] se necessario
		if (brightness < 0)
			brightness = 0;
		if (brightness > 1)
			brightness = 1;
		return brightness;
	}

	/**
	 * Converte il colore RGBW della penna in un colore RGB True Color
	 * 
	 * @return Colore RGB True Color
	 */
	public Color getTrueColor() {
		return Color.hsb(getHue(), getSaturation(), getBrightness());
	}

	/**
	 * Trova il colore più vicino fra quelli forniti
	 * 
	 * @param clist
	 *            La lista di colori da confrontare
	 * @return Indice del colore più simile trovato
	 */
	public int getBestTrueColor(List<Color> clist) {
		Color c = getTrueColor();
		Point3D p = new Point3D(c.getRed(), c.getGreen(), c.getBlue()); // Trasformo il colore in un punto 3D
		List<Double> dist = new ArrayList<Double>(); // Vettore delle distanze

		for (Color cl : clist) {
			Point3D pl = new Point3D(cl.getRed(), cl.getGreen(), cl.getBlue()); // Trasformo il colore in un punto 3D
			dist.add(pl.distance(p));
		}

		double mindist = Collections.min(dist); // Trovo la minima distanza
		return dist.indexOf(mindist); // Trovo il colore alla minima distanza

	}

	/**
	 * Verifica se si tratta di un colore valido o nullo (ottenuto quando la penna
	 * non e' premuta)
	 * 
	 * @return Valido o nullo
	 */
	public boolean isValid() {
		if (R == 0 && G == 0 && B == 0 && W == 0)
			return false;
		else
			return true;
	}

}