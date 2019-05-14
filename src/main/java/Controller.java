
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 * Gestione dello stato dell'applicazione e del coordinamento con le altre
 * classi
 * 
 * @author Mariella Bonomo
 * @author Alessio Lombardo
 * @version 1.1
 */
public class Controller {

	/**
	 * Porta seriale della penna
	 */
	private static RGBPenSerialPort pen;
	/**
	 * Lista dei colori in uso
	 */
	private static List<Color> clist = new ArrayList<Color>();
	/**
	 * Lista dei nomi dei colori principali
	 */
	private static List<String> clistname = new ArrayList<String>();
	/**
	 * Nome della Palette in uso
	 */
	private static String palette;
	/**
	 * Flag per il calcolo della saturazione nella conversione RGB-HSB
	 */
	private static boolean saturationFlag = false;

	/**
	 * Main dell'applicazione
	 * 
	 * @param args
	 *            Argomenti (non richiesti)
	 * @throws InterruptedException
	 *             Nel caso di errori alla creazione di Thread o allo sleep()
	 * @throws SerialPortException
	 *             Nel caso di errori di comunicazione con la porta seriale
	 */
	public static void main(String[] args) throws InterruptedException, SerialPortException {

		Thread th = new Thread(() -> JavaFx.main(args));
		th.start();
		Thread.sleep(3000);
		// Definisco i colori principali (e il loro nome)
		setPalette("palette-8colori");
		clistname.add("ROSSO");
		clistname.add("GIALLO");
		clistname.add("VERDE");
		clistname.add("CIANO");
		clistname.add("BLU");
		clistname.add("MAGENTA");
		clistname.add("NERO");
		clistname.add("BIANCO");

		System.out.println("FASE CALIBRAZIONE");
		setCalibration();
		System.out.println("FINE CALIBRAZIONE");
		System.out.println("FASE LETTURA");
		lettura();
	}

	/**
	 * Richiede ed imposta la calibrazione dei colori
	 * 
	 * @throws InterruptedException
	 *             Nel caso di errori allo sleep()
	 * @throws SerialPortException
	 *             Nel caso di errori di comunicazione con la porta seriale
	 */
	public static void setCalibration() throws InterruptedException, SerialPortException {

		for (int i = 0; i < 8; i++) {
			if (!portConnected()) {
				i--; // Evita che l'indice venga incrementato
				continue; // Se la penna non e' connessa ricomincia
			}
			try {

				RGBPenColor c0, c, m;
				JavaFx.setMessage("Calibrazione: Selezionare il colore " + clistname.get(i));

				do {
					c0 = pen.readColor();
				} while (!c0.isValid());

				m = new RGBPenColor(0, 0, 0, 0); // Colore di media
				int num = 0;
				do {
					JavaFx.setMessage("Calibrazione: Lettura in corso (" + num + ")...");
					c = pen.readColor();
					if (c.isValid()) {
						m = new RGBPenColor(m.getR() + c.getR(), m.getG() + c.getG(), m.getB() + c.getB(),
								m.getW() + c.getW());
					}
					num++;
				} while (c.isValid());

				m = new RGBPenColor((int) m.getR() / num, (int) m.getG() / num, (int) m.getB() / num,
						(int) m.getW() / num);

				switch (i) {
				case 0:
					RGBPenColor.RED_CAL = m;
					break;
				case 1:
					RGBPenColor.YELLOW_CAL = m;
					break;
				case 2:
					RGBPenColor.GREEN_CAL = m;
					break;
				case 3:
					RGBPenColor.CYAN_CAL = m;
					break;
				case 4:
					RGBPenColor.BLUE_CAL = m;
					break;
				case 5:
					RGBPenColor.MAGENTA_CAL = m;
					break;
				case 6:
					RGBPenColor.BLACK_CAL = m;
					break;
				case 7:
					RGBPenColor.WHITE_CAL = m;
					break;
				}

			} catch (SerialPortTimeoutException e) { // Se la porta si e' scollegata prova ad aprirne una nuova
				System.out.println("Errore: Timeout");
				pen.closePort();
				pen = null;
				continue;
			}

		}
		JavaFx.setMessage("Calibrazione: COMPLETA");
	}

	/**
	 * Legge iterativamente i colori letti dalla penna ed in caso di errori prova a
	 * riconnettersi
	 * 
	 * @throws InterruptedException
	 *             Nel caso di errori allo sleep()
	 * @throws SerialPortException
	 *             Nel caso di errori di comunicazione con la porta seriale
	 */
	public static void lettura() throws InterruptedException, SerialPortException {

		for (;;) {

			if (!portConnected())
				continue; // Se la penna non e' connessa ricomincia

			try {

				RGBPenColor c0, c, m;

				do {
					c0 = pen.readColor();
				} while (!c0.isValid());

				m = new RGBPenColor(0, 0, 0, 0); // Colore di media
				int num = 0;
				do {
					JavaFx.setMessage("Lettura in corso (" + num + ")...");
					c = pen.readColor();
					if (c.isValid()) {
						m = new RGBPenColor(m.getR() + c.getR(), m.getG() + c.getG(), m.getB() + c.getB(),
								m.getW() + c.getW());
					}
					num++;
				} while (c.isValid());

				m = new RGBPenColor((int) m.getR() / num, (int) m.getG() / num, (int) m.getB() / num,
						(int) m.getW() / num);

				System.out.print("Colore Sensore: " + m.getR() + ", " + m.getG() + ", " + m.getB() + ", " + m.getW());

				Color tc = m.getTrueColor(); // Converte il colore in RGB True Color

				System.out.print("\t\tColore RGB True Color: " + (int) (tc.getRed() * 255) + ", "
						+ (int) (tc.getGreen() * 255) + ", " + (int) (tc.getBlue() * 255));

				System.out.println("\t\tColore piu' simile: " + m.getBestTrueColor(clist));
				JavaFx.setMessage("");

				try {
					if (palette.equals("palette-grafico")) {
						// Comunicazione alla classe JavaFX della lettura del colore (per il grafico)
						JavaFx.setColorPlot(m.getBestTrueColor(clist));
					} else {
						// Comunicazione alla classe JavaFX del RadioButton da selezionare (per la
						// palette)
						JavaFx.setSelectedRadioButton(m.getBestTrueColor(clist));
					}
				} catch (Exception e) {
				}

			} catch (SerialPortTimeoutException e) { // Se la porta si e' scollegata prova ad aprirne una nuova
				System.out.println("Errore: Timeout");
				pen.closePort();
				pen = null;
			}

		}

	}

	/**
	 * Verifica se la penna e' connessa ed in caso contrario prova a cercare una
	 * connessione
	 * 
	 * @return Stato della penna (connessa, non connessa)
	 * @throws InterruptedException
	 *             Nel caso di errori allo sleep()
	 */
	private static boolean portConnected() throws InterruptedException {
		if (pen == null) { // Se la porta seriale non e' ancora stata aperta (o c'era stato un errore)
							// allora cerco e apro la porta seriale
			System.out.print("Ricerca Arduino in corso... ");
			JavaFx.setMessageArduino("Ricerca Arduino in corso...");
			Thread.sleep(500);
			pen = RGBPenSerialPort.searchPort(); // Cerca ed apre la porta seriale connessa ad Arduino
			if (pen != null) {
				System.out.println("Arduino Collegato (Porta " + pen.getPortName() + ")");
				JavaFx.setMessageArduino("Arduino Collegato (Porta " + pen.getPortName() + ")");
			} else {
				System.out.println("Errore: Arduino non trovato");
				JavaFx.setMessageArduino("Errore: Arduino non trovato");
				return false; // Non si è riusciti a trovare una porta
			}
		}
		return true;
	}

	/**
	 * Restituisce la lista di colori inseriti in un file in formato
	 * esadecimale/HTML
	 * 
	 * @param filename
	 *            Nome del file da cui leggere
	 * @return Lista di colori
	 */
	private static List<Color> getColorList(String filename) {
		List<Color> clist = new ArrayList<Color>(); // Lista dei colori
		try {
			BufferedReader filebuffer = new BufferedReader(
					new InputStreamReader(Controller.class.getClassLoader().getResourceAsStream(filename + ".txt")));

			String cstring = null;
			do {
				cstring = filebuffer.readLine();
				if (cstring != null)
					clist.add(Color.web(cstring));

			} while (cstring != null);

			filebuffer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return clist;
	}

	/**
	 * Imposta la palette da utilizzare
	 * 
	 * @param paletteName
	 *            Nome della palette
	 */
	public static void setPalette(String paletteName) {
		palette = paletteName;
		clist = getColorList(palette);
	}

	/**
	 * Determina se calcolare la saturazione nella conversione RGBW-HSB
	 * 
	 * @return Stato della variabile saturazione (True, False)
	 */
	public static boolean isSaturationEnabled() {
		return saturationFlag;
	}

	/**
	 * Abilita/disabilita il calcolo della saturazione nella conversione RGBW-HSB
	 * 
	 * @param saturationFlag
	 *            Stato della saturazione da impostare (True o False)
	 */
	public static void setSaturationFlag(boolean saturationFlag) {
		Controller.saturationFlag = saturationFlag;
	}
}
