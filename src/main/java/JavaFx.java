
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * Gestione dell'interfaccia grafica
 * 
 * @author Mariella Bonomo
 * @author Alessio Lombardo
 * @version 1.1
 */
public class JavaFx extends Application {

	/**
	 * Messaggio per l'utente
	 */
	private static Text message = new Text();

	/**
	 * Messaggio relativo alla connessione con Arduino
	 */
	private static Text messageArduino = new Text();

	/**
	 * Memorizza quali serie del grafico devono essere visualizzate
	 */
	private static boolean[] stati_grafico = new boolean[5];

	/**
	 * Colore del grafico selezionato
	 */
	private static int selectedColorPlot = -1;

	/**
	 * Radiobuttons collegati alla finestra "Test Palette"
	 */
	private static RadioButton rb[] = new RadioButton[16];

	/**
	 * Bottone finestra principale
	 */
	private static Button testnostra, testcga, testvicii, demo;

	/**
	 * Finestra "Grafico"
	 */
	private static Stage scatterPlot;

	/**
	 * Finestra "Test Palette"
	 */
	private static Stage testPalette;

	/**
	 * Main dell'interfaccia grafica
	 * 
	 * @param args
	 *            Argomenti (non richiesti)
	 */
	public static void main(String[] args) {

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Calibrazione Light pen");
		primaryStage.setResizable(false);
		GridPane root = new GridPane();

		for (int i = 0; i < 5; i++) {
			stati_grafico[i] = true;
		}

		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), ae -> {
			try {

				if (selectedColorPlot != -1) {

					if (selectedColorPlot != 5) {
						if (stati_grafico[selectedColorPlot] == true) {
							stati_grafico[selectedColorPlot] = false;
						} else {
							stati_grafico[selectedColorPlot] = true;
						}
					} else {
						for (int i = 0; i < 5; i++) {
							stati_grafico[i] = true;
						}
					}

					scatterPlot.setScene(getScatterPlotScene());
					selectedColorPlot = -1;
				}

			} catch (Exception e) {
			}
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		scatterPlot = new Stage();
		scatterPlot.setOnCloseRequest(event -> {
			demo.setDisable(false);
			testnostra.setDisable(false);
			testcga.setDisable(false);
			testvicii.setDisable(false);
		});
		testPalette = new Stage();
		testPalette.setOnCloseRequest(event -> {
			demo.setDisable(false);
			testnostra.setDisable(false);
			testcga.setDisable(false);
			testvicii.setDisable(false);
		});

		demo = new Button("Grafico d'Esempio");
		demo.setPrefSize(250, 30);
		demo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				scatterPlot.setTitle("DEMO GRAFICO");
				try {
					scatterPlot.setScene(getScatterPlotScene());
					selectedColorPlot = -1;
					Controller.setPalette("palette-grafico");
					demo.setDisable(true);
					testnostra.setDisable(true);
					testcga.setDisable(true);
					testvicii.setDisable(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				scatterPlot.setMaximized(true);
				scatterPlot.show();
			}
		});

		testnostra = new Button("Test Palette Nostra");
		testnostra.setPrefSize(250, 30);
		testnostra.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				showPalette("TEST PALETTE NOSTRA", "palette-nostra");
			}
		});

		testcga = new Button("Test Palette CGA");
		testcga.setPrefSize(250, 30);
		testcga.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				showPalette("TEST PALETTE CGA", "palette-cga");
			}
		});

		testvicii = new Button("Test Palette VIC II");
		testvicii.setPrefSize(250, 30);
		testvicii.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				showPalette("TEST PALETTE VIC II", "palette-vicii");
			}
		});
		final CheckBox check = new CheckBox("Saturazione");
		check.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (check.isSelected()) {
					Controller.setSaturationFlag(true);
				} else
					Controller.setSaturationFlag(false);
			}
		});

		final Button blue = new Button();
		final Button red = new Button();
		final Button green = new Button();
		final Button black = new Button();
		final Button white = new Button();
		final Button yellow = new Button();
		final Button cyan = new Button();
		final Button magenta = new Button();
		demo.setStyle("-fx-font: 20 arial");
		testnostra.setStyle("-fx-font: 20 arial");
		testcga.setStyle("-fx-font: 20 arial");
		testvicii.setStyle("-fx-font: 20 arial");
		blue.setStyle("-fx-font: 30 arial;-fx-background-color: rgb(0, 0, 255)");
		red.setStyle("-fx-font: 30 arial;-fx-background-color: rgb(255, 0, 0)");
		green.setStyle("-fx-font: 30 arial;-fx-background-color: rgb(0, 255, 0)");
		black.setStyle("-fx-font: 30 arial;-fx-background-color: rgb(0, 0, 0)");
		white.setStyle("-fx-font: 30 arial;-fx-background-color: rgb(255, 255, 255)");
		yellow.setStyle("-fx-font: 30 arial;-fx-background-color: rgb(255, 255, 0)");
		cyan.setStyle("-fx-font: 30 arial;-fx-background-color: rgb(0, 255, 255)");
		magenta.setStyle("-fx-font: 30 arial;-fx-background-color: rgb(255, 0, 255)");

		Label text2 = new Label();
		text2.setMinWidth(270);

		root.setVgap(5);
		root.add(demo, 0, 0);
		root.add(testnostra, 0, 1);
		root.add(testcga, 0, 2);
		root.add(testvicii, 0, 3);
		root.add(check, 0, 4);
		root.add(text2, 0, 5);
		root.add(message, 0, 5);
		root.add(red, 1, 5);
		root.add(yellow, 2, 5);
		root.add(green, 3, 5);
		root.add(cyan, 4, 5);
		root.add(blue, 5, 5);
		root.add(magenta, 7, 5);
		root.add(black, 8, 5);
		root.add(white, 9, 5);
		root.add(messageArduino, 0, 7);

		primaryStage.setScene(new Scene(root, 600, 270));
		primaryStage.show();

		/* Chiusura applicazione */
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				try {
					Platform.exit();
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Crea la finestra Test Palette
	 * 
	 * @param paletteName
	 *            Nome della Palette da visualizzare
	 * @return Scena corrispondente alla finestra
	 */
	private Scene getTestPaletteScene(String paletteName) {

		Rectangle rectangle = new Rectangle();
		rectangle.setWidth(608.0f);
		rectangle.setHeight(150.0f);

		Image image = new Image(paletteName + ".png");
		ImagePattern radialGradient = new ImagePattern(image);
		rectangle.setFill(radialGradient);

		final HBox hbox = new HBox();
		final VBox vbox = new VBox();

		final ToggleGroup group = new ToggleGroup();
		for (int i = 0; i < 16; i++) {
			rb[i] = new RadioButton();
			rb[i].setToggleGroup(group);
			rb[i].setPadding(new Insets(5, 6, 0, 11)); // Imposta la distanza fra un radio button e un altro
			rb[i].setMouseTransparent(true); // Impedisce che il mouse possa selezionare il radiobutton
			rb[i].setFocusTraversable(false);
			hbox.getChildren().addAll(rb[i]);
		}
		vbox.getChildren().addAll(rectangle, hbox);

		Scene scene = new Scene(new Group());
		((Group) scene.getRoot()).getChildren().add(vbox);

		return scene;
	}

	/**
	 * Crea la finestra Scatter Plot
	 * 
	 * @return Scena corrispondente alla finestra
	 * @throws IOException
	 *             Nel caso di errori di lettura al file
	 */
	public Scene getScatterPlotScene() throws IOException {

		final NumberAxis xAxis = new NumberAxis(2000, 2010, 1);
		final NumberAxis yAxis = new NumberAxis(0, 700, 100);

		xAxis.setTickLabelFormatter(new StringConverter<Number>() { // Evita che appaia il puntino negli anni
			@Override
			public String toString(Number object) {
				return (object.intValue()) + "";
			}

			@Override
			public Number fromString(String string) {
				return null;
			}
		});

		xAxis.setLabel("Anni");
		yAxis.setLabel("Nascite in migliaia");

		ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);

		sc.setTitle("Nascite nel decennio 2000-2010 in 5 stati");

		BufferedReader b = new BufferedReader(
				new InputStreamReader(Controller.class.getClassLoader().getResourceAsStream("valori.txt")));

		String[] stati = { "Italia", "Francia", "Germania", "Belgio", "Spagna" };
		for (int j = 0; j < 5; j++) {

			if (sc.getData() == null)
				sc.setData(FXCollections.<XYChart.Series<Number, Number>>observableArrayList());
			ScatterChart.Series<Number, Number> series = new ScatterChart.Series<Number, Number>();
			series.setName(stati[j]);
			for (int i = 0; i < 11; i++) {
				if (stati_grafico[j] == true) {
					series.getData().add(new ScatterChart.Data<Number, Number>(Integer.parseInt(b.readLine()),
							Integer.parseInt(b.readLine()) - j * 10));
				} else {
					series.getData().add(new ScatterChart.Data<Number, Number>(Integer.parseInt(b.readLine()),
							Integer.parseInt(b.readLine()) - 10000));
				}
			}

			sc.getData().add(series);

		}

		sc.setMinSize(600, 500);
		sc.setPrefSize(scatterPlot.getWidth() - 16, scatterPlot.getHeight() - 38);
		Scene scene = new Scene(sc);
		scene.getStylesheets().add("scattercolor.css");

		return scene;

	}

	/**
	 * Mostra la finestra per le palette di prova
	 * 
	 * @param title
	 *            Titolo da visualizzare
	 * @param paletteName
	 *            Palette da mostrare
	 */
	private void showPalette(String title, String paletteName) {
		testPalette.setTitle(title);
		Controller.setPalette(paletteName);
		testPalette.setScene(getTestPaletteScene(paletteName));
		demo.setDisable(true);
		testnostra.setDisable(true);
		testcga.setDisable(true);
		testvicii.setDisable(true);
		testPalette.setMaxHeight(600);
		testPalette.setMaxWidth(614);
		testPalette.setResizable(false);
		testPalette.show();
	}

	/**
	 * Visualizza un messaggio per l'utente
	 * 
	 * @param message
	 *            Messaggio da visualizzare
	 */
	public static void setMessage(String message) {
		JavaFx.message.setText(message);
	}

	/**
	 * Visualizza un messaggio relativo alla connessione con Arduino
	 * 
	 * @param messageArduino
	 *            Messaggio da visualizzare
	 */
	public static void setMessageArduino(String messageArduino) {
		JavaFx.messageArduino.setText(messageArduino);
	}

	/**
	 * Seleziona un colore del grafico
	 * 
	 * @param index
	 *            Indice del colore da selezionare
	 */
	public static void setColorPlot(int index) {
		selectedColorPlot = index;
	}

	/**
	 * Seleziona uno dei radiobutton della finestra "Test Palette"
	 * 
	 * @param index
	 *            Indice del radiobutton da selezionare
	 */
	public static void setSelectedRadioButton(int index) {
		rb[index].setSelected(true);
	}

}
