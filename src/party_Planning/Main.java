package party_Planning;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Launches PartyPlanner program by calling the initial screen and showing the stage
 * @author Samarth Desai
 */
public class Main extends Application {
	
	public static void main(String[] args) 
	{
		launch(args);
	}

	public void start(Stage stage)
	{

		WelcomeScreen welcomeScreen = new WelcomeScreen (stage);
		welcomeScreen.createWelcome();
		stage.show();
	}
	
}