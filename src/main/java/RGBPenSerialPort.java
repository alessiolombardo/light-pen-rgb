
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 * Gestione della comunicazione seriale con Arduino
 * 
 * @author Alessio Lombardo
 * @author Mariella Bonomo
 * @version 1.1
 */
public class RGBPenSerialPort extends SerialPort {

	/**
	 * Costruttore
	 * 
	 * @param portName
	 *            Nome della porta
	 */
	private RGBPenSerialPort(String portName) {
		super(portName);
	}

	/**
	 * Apre una comunicazione seriale con la porta seriale desiderata
	 * 
	 * @param portName
	 *            Nome della porta
	 * @return Porta seriale
	 * @throws SerialPortException
	 *             Nel caso di errori di comunicazione con la porta seriale
	 * @throws InterruptedException
	 *             Nel caso di errori allo sleep()
	 */
	private static RGBPenSerialPort openPort(String portName) throws SerialPortException, InterruptedException {
		RGBPenSerialPort pen = new RGBPenSerialPort(portName);
		pen.openPort();
		pen.setParams(BAUDRATE_9600, DATABITS_8, STOPBITS_1, PARITY_NONE);
		Thread.sleep(100);
		return pen;
	}

	/**
	 * Cerca una porta seriale su cui è connesso Arduino e prova a connettersi
	 * 
	 * @return Porta seriale
	 */
	public static RGBPenSerialPort searchPort() {
		try {
			if (Platform.isWindows()) { // Per Windows: ricerca nel registro di sistema
				String regpath = "SYSTEM\\CurrentControlSet\\Enum\\USB";
				for (String dev : Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, regpath)) {
					for (String itm : Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, regpath + "\\" + dev)) {
						try {
							String fn = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
									regpath + "\\" + dev + "\\" + itm, "FriendlyName");
							if (fn.contains("Arduino")) {
								return openPort(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
										regpath + "\\" + dev + "\\" + itm + "\\Device Parameters", "PortName"));
							}
						} catch (Exception e) {
						}
					}
				}
			} else if (Platform.isLinux()) { // Per Linux: ricerca tra i dispositivi in /dev/serial
				for (Path dev : Files.newDirectoryStream(Paths.get("/dev/serial/by-id"))) {
					if (dev.toString().contains("Arduino")) {
						if (!Paths.get(dev.toString()).toRealPath().toFile().canWrite()) {
							Thread.sleep(1500);
							try {
								Runtime.getRuntime()
										.exec("pkexec chmod 777 " + Paths.get(dev.toString()).toRealPath().toString())
										.waitFor(); // Richiedo privilegi di root se necessario
							} catch (Exception e) {
							}
							if (!Paths.get(dev.toString()).toRealPath().toFile().canWrite()) {
								System.out.println(
										"\nErrore: Accesso negato alla porta seriale. Ricontrollare i privilegi di accesso.");
								System.exit(0);
							}
						}
						return openPort(Paths.get(dev.toString()).toRealPath().toString());
					}
				}
			} else {
				System.out.println("\nErrore: Sistema operativo non supportato");
				System.exit(0);
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Legge dalla porta seriale un pacchetto da 24Byte nella forma
	 * "RXXXXXGXXXXXBXXXXXWXXXXX" (con "X" numerico) corrispondente al colore letto
	 * dal sensore
	 * 
	 * @return Colore letto
	 * @throws SerialPortException
	 *             Nel caso di errori di comunicazione con la porta seriale
	 * @throws SerialPortTimeoutException
	 *             Nel caso di timeout durante la comunicazione con la porta seriale
	 */
	public RGBPenColor readColor() throws SerialPortException, SerialPortTimeoutException {

		while (!readString(1, 5000).equals("W")) {
		}

		readString(5, 5000);
		String frame = readString(24, 15000);
		try {
			Integer R = new Integer(frame.substring(1, 6));
			Integer G = new Integer(frame.substring(7, 12));
			Integer B = new Integer(frame.substring(13, 18));
			Integer W = new Integer(frame.substring(19, 24));
			return new RGBPenColor(R, G, B, W);
		} catch (Exception e) { // In caso di errori di conversione dei numeri
								// restituisco il colore nullo (come se la penna
								// non fosse attiva)
			System.out.println("WARNING: Errore nella conversione dei dati.");
			return new RGBPenColor(0, 0, 0, 0);
		}

	}

}