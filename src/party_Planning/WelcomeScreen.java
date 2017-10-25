package party_Planning;

import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Opening screen of PartyPlanner
 * @author Samarth Desai
 *
 */
public class WelcomeScreen {

	public static final int WIDTH = 800;
	public static final int CENTER_WIDTH = WIDTH/2;
	public static final int HEIGHT = 600;
	public static final int CENTER_HEIGHT = HEIGHT/2;
	public static final Color BACKGROUND_COLOR = Color.SEASHELL;
	private static final int LINE_WIDTH = 2;
	private static final Color TEXT_COLOR = Color.INDIANRED;
	private static final String TITLE = "PartyPlanner";
	private static final int TITLE_POSITION_Y_GAP = 20;
	private static final int TOP_TITLE_POSITION_Y = 250;
	private static final int MIDDLE_TITLE_POSITION_Y = TOP_TITLE_POSITION_Y + TITLE_POSITION_Y_GAP;
	private static final int BOTTOM_TITLE_POSITION_Y = MIDDLE_TITLE_POSITION_Y + TITLE_POSITION_Y_GAP;
	private static final Color TITLE1_COLOR = Color.RED;
	private static final Color TITLE2_COLOR = Color.BROWN;
	private static final Color TITLE3_COLOR = Color.INDIANRED;
	private static final Font TITLE_FONT = Font.font("Times New Roman", FontWeight.BOLD, 100);
	private static final Glow TITLE_GLOW = new Glow (1.0);
	private static final int DURATION = 1500;
	private static final Font DEV_FONT = Font.font("French Script MT", 18);
	private static final int DEV_BUFFER = 5;
	private static final String DEV_CREDITS = "Developed by Samarth Desai";
	private static final int DEV_POSITION_Y = HEIGHT - 23 - DEV_BUFFER;
	private static final Font START_FONT = Font.font("Verdana", 22);
	private static final String START_TEXT = "Press any key to continue";
	private static final Glow START_GLOW = new Glow (0.7);
	private static final double FADE_START = 0.7;
	private static final double FADE_STOP = 0.1;

	private Stage stage;
	private Pane root;
	private Scene scene;
	private Canvas canvas;
	private GraphicsContext graphics;

	/**
	 * Sets up structure of the Welcome Screen frame
	 * @param introStage
	 */
	public WelcomeScreen (Stage introStage) {

		stage = introStage;
		root = new Pane();
		scene = new Scene(root, WIDTH, HEIGHT, BACKGROUND_COLOR);
		stage.setScene(scene);
		stage.setTitle(TITLE);
		stage.sizeToScene();
		stage.setResizable(false);

	}

	/**
	 * Creates the main features of the Welcome Screen
	 */
	public void createWelcome() {
		setupCanvas();
		setupGraphics();
		createTitle();
		createStartLabel();
		createDeveloperLabel();
		beginPlanner();
	}

	/**
	 * Creates canvas
	 */
	private void setupCanvas() {
		canvas = new Canvas(WIDTH, HEIGHT);
		root.getChildren().add(canvas);
	}

	/**
	 * Sets up graphics and background color
	 */
	private void setupGraphics() {
		graphics = canvas.getGraphicsContext2D();
		graphics.setTextAlign(TextAlignment.CENTER);
		graphics.setFill(BACKGROUND_COLOR);
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
		graphics.setLineWidth(LINE_WIDTH);
	}

	/**
	 * Creates "PartyPlanner" title
	 */
	private void createTitle () {
		graphics.setFont(TITLE_FONT);
		graphics.setEffect(TITLE_GLOW);
		graphics.setStroke(TITLE1_COLOR);
		graphics.strokeText(TITLE, CENTER_WIDTH, TOP_TITLE_POSITION_Y);
		graphics.setFill(TITLE2_COLOR);
		graphics.fillText(TITLE, CENTER_WIDTH, MIDDLE_TITLE_POSITION_Y);
		graphics.setFill(TITLE3_COLOR);
		graphics.fillText(TITLE, CENTER_WIDTH, BOTTOM_TITLE_POSITION_Y);
	}

	/**
	 * Creates start instructions to progress to next screen
	 */
	private void createStartLabel() {

		Label start = new Label(START_TEXT);
		start.layoutXProperty().bind(root.widthProperty().subtract(start.widthProperty()).divide(2));
		start.layoutYProperty().bind(root.heightProperty().subtract(TOP_TITLE_POSITION_Y).add(start.heightProperty()));
		start.setTextFill(TEXT_COLOR);
		start.setFont(START_FONT);
		start.setEffect(START_GLOW);
		root.getChildren().add(start);

		createInfiniteTransition(start);
	}

	/**
	 * Creates the infinite fade transition for the start label
	 * @param transitionLabel
	 */
	private void createInfiniteTransition(Label transitionLabel) {
		FadeTransition infiniteTransition = new FadeTransition(Duration.millis(DURATION), transitionLabel);
		infiniteTransition.setFromValue(FADE_START);
		infiniteTransition.setToValue(FADE_STOP);
		infiniteTransition.setCycleCount(Timeline.INDEFINITE);
		infiniteTransition.setAutoReverse(true);
		infiniteTransition.play();
	}

	/**
	 * Adds the developer credits
	 */
	private void createDeveloperLabel() {
		Label developer = new Label (DEV_CREDITS);
		developer.setLayoutX(DEV_BUFFER);
		developer.setLayoutY(DEV_POSITION_Y);
		developer.setTextFill(TEXT_COLOR);
		developer.setFont(DEV_FONT);
		root.getChildren().add(developer);
	}

	/**
	 * Transitions to main Party Planner when any key is typed
	 */
	private void beginPlanner() {

		ArrayList<String> input = new ArrayList<String>();
		stage.getScene().setOnKeyTyped(
				new EventHandler<KeyEvent>()
				{
					@Override
					public void handle(KeyEvent begin)
					{

						String key = begin.getCode().toString();
						input.add(key);
						if(!input.isEmpty()) {
							PartyPlanner partyPlanner = new PartyPlanner (stage);
							partyPlanner.createPlanner();
						}
					}
				}

				);
	}

}
