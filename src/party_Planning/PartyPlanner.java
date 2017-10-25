package party_Planning;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The main GUI for the planning application
 * @author Samarth Desai
 *
 */
public class PartyPlanner {

	private static final String BACKGROUND_COLOR_CODE = "fff5ee";
	private static final int PLANNER_PADDING = 10;
	private static final int BUDGET_BOX_GAP = 10;
	private static final int INPUT_BOX_GAP = 3;
	private static final String DOLLAR_SIGN = "$";
	private static final String DEFAULT_AMOUNT = "$0.00";
	private static final String REGEX_MATCH = "(?=.)^(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+)(\\.[0-9]{2})?$";
	private static final String COMMA = ",";
	private static final String INCORRECT_ENTRY = "Invalid number entry";
	private static final String CURRENCY_FORMAT = "#,##0.00";
	private static final String ITEM_LIST_TITLE = "Food and Drinks";
	private static final String BUDGET_ERROR_MESSAGE = "Please enter a correct budget";
	private static final Color BUDGET_ERROR_COLOR = Color.RED;
	private static final String LIST_ENTRY_BRIDGE = ": ";
	private static final String REMAINING_LIST_TITLE = "Remaining Items";
	private static final String PREFERENCES_MATCHER = "\\[(.*?)\\]";
	private static final int STATISTICS_GAP = 20;
	private static final String STATISTICS_OUTLINE = "-fx-border-style: solid inside;" + 
													 "-fx-border-width: 2;" +
													 "-fx-border-insets: 5;" + 
													 "-fx-border-radius: 5;" +
													 "-fx-border-color: ";
	private static final String ITEM_STATISTICS_OUTLINE_COLOR = "orange;";
	private static final String REMAINING_STATISTICS_OUTLINE_COLOR = "darkorange;";
	private static final String COST_TRACKER_TITLE = "Total Cost: ";
	private static final String LEFTOVER_FUNDS_TRACKER_TITLE = "Leftover Funds: ";
	private static final String AFFORDABLE_ITEMS_TITLE = "# of Items to Buy: ";
	private static final String REMAINING_ITEMS_TITLE = "# of Items Remaining: ";
	
	private Stage stage;
	private Scene scene;
	private BorderPane root;
	
	private TextField budgetInput;
	private HBox budgetBox;
	private double budget;
	
	private ObservableList<String> items;
	private ObservableList<String> remainingItems;
	private ListView <String> itemList;
	private ListView <String> remainingItemList;
	private Label spendingAmount;
	private Label leftoverAmount;
	private SimpleIntegerProperty itemsCount;
	private SimpleIntegerProperty remainingCount;
	private DecimalFormat currency;
	
	/**
	 * Constructor that sets up the structure of the main planner frame
	 * @param currentStage
	 */
	public PartyPlanner (Stage currentStage) {
		
		stage = currentStage;
		root = new BorderPane();
		root.setStyle("-fx-background-color: #" + BACKGROUND_COLOR_CODE);
		root.setPadding(new Insets(PLANNER_PADDING, PLANNER_PADDING, PLANNER_PADDING, PLANNER_PADDING));
		scene = new Scene(root, WelcomeScreen.WIDTH, WelcomeScreen.HEIGHT, WelcomeScreen.BACKGROUND_COLOR);
		stage.setScene(scene);
		
	}
	
	/**
	 * Creates the major features of the main planner frame
	 */
	public void createPlanner() {
		createBudgetInput();
		createItemList();
		createRemainingList();
		createStatisticsTracker();
	}
	
	/**
	 * Creates panel for user budget input
	 */
	private void createBudgetInput() {
		Label budgetLabel = new Label ("Please enter your budget:");
		budgetInput = new TextField();
		handleEnter();
		
		budgetBox = new HBox(BUDGET_BOX_GAP);
		HBox inputBox = new HBox(INPUT_BOX_GAP);
		Label dollarSign = new Label(DOLLAR_SIGN);
		inputBox.getChildren().addAll(dollarSign, budgetInput);
		inputBox.setAlignment(Pos.CENTER);
		budgetBox.getChildren().addAll(budgetLabel, inputBox);
		budgetBox.setAlignment(Pos.CENTER);
		
		root.setTop(budgetBox);
	}
	
	/**
	 * Checks the user budget input, and if it is valid based on the regex specifications,
	 * it is sent to the Spending Algorithm for the necessary food and drinks optimization
	 */
	private void handleEnter() {
		ArrayList<Label> errorMessages = new ArrayList<Label>();
		budgetInput.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent enter)
			{
				if (enter.getCode().equals(KeyCode.ENTER)) {
					
					resetData();
					
					if (!budgetInput.getText().isEmpty() && budgetInput.getText().matches(REGEX_MATCH)) 
					{
						if(errorMessages.size()>0) {
							budgetBox.getChildren().remove(errorMessages.get(0));
							errorMessages.remove(0);
						}
						String budgetEntry = budgetInput.getText();
						double budgetDouble;
						//Handles case where user inputs budget with commas by removing them for internal use
						if (budgetEntry.contains(COMMA)) {
							try {
								NumberFormat removeCommas = NumberFormat.getInstance(Locale.US);
						        budgetDouble = removeCommas.parse(budgetEntry).doubleValue();
						        updateAll(budgetDouble);
							} catch (ParseException e) {
								System.out.println(INCORRECT_ENTRY);
							}
						}
						else {
							budgetDouble = Double.parseDouble(budgetEntry);
							updateAll(budgetDouble);
						}
					}
					else {
						if(errorMessages.size()>0) {
							budgetBox.getChildren().remove(errorMessages.get(0));
							errorMessages.remove(0);
						}
						Label blankBudget = new Label(BUDGET_ERROR_MESSAGE);
						blankBudget.setTextFill(BUDGET_ERROR_COLOR);
						budgetBox.getChildren().add(blankBudget);
						errorMessages.add(blankBudget);
					}
					
				}
			}
		});
	}
	
	/**
	 * Resets the tracker statistics and elements of the ListView
	 */
	private void resetData() {
		items.clear();
		itemsCount.set(0);
		spendingAmount.setText(DEFAULT_AMOUNT);
		
		remainingItems.clear();
		remainingCount.set(0);
		leftoverAmount.setText(DEFAULT_AMOUNT);
	}
	
	/**
	 * Creates the ListView that displays the affordable food and drinks after the user inputs a budget
	 */
	private void createItemList() {

		items = FXCollections.observableArrayList();
		VBox itemBox = new VBox();
		itemBox.setAlignment(Pos.CENTER);
		Label itemLabel = new Label(ITEM_LIST_TITLE);
		itemList = new ListView <String> ();
		itemList.setPrefSize(WelcomeScreen.CENTER_WIDTH-PLANNER_PADDING, WelcomeScreen.HEIGHT);
		itemList.setEditable(true);

		itemBox.getChildren().addAll(itemLabel, itemList);

		root.setLeft(itemBox);
	}
	
	/**
	 * Updates all the statistical displays and ListViews accordingly
	 * @param currentBudget
	 */
	private void updateAll(double currentBudget) {
		budget = currentBudget;
		currency = new DecimalFormat(CURRENCY_FORMAT);
		SpendingAlgorithm spendingAlgorithm = new SpendingAlgorithm();
		Map<String, Integer> consumablesMap = spendingAlgorithm.selectConsumables(budget);
		double amountSpent = spendingAlgorithm.getAmountSpent();
		Map <String, ArrayList<String>> remainingMap = spendingAlgorithm.getRemainingItems();
		
		updateItems(consumablesMap);
		updateRemainingItems(remainingMap);
		updateAmountSpent(amountSpent);
		updateLeftoverFunds(currentBudget, amountSpent);
	}
	
	/**
	 * Updates the food and drinks displayed in the Food and Drinks ListView,
	 * along with the number of items to buy in the statistics panel
	 * @param newItems
	 */
	private void updateItems(Map <String, Integer> newItems) {
		int itemCounter = 0;
		for (Entry<String, Integer> itemEntry : newItems.entrySet()) {
			String listElement = itemEntry.getKey() + LIST_ENTRY_BRIDGE + itemEntry.getValue();
			itemCounter += itemEntry.getValue();
			items.add(listElement);
		}
		itemList.setItems(items);
		itemsCount.set(itemCounter);
	}
	
	/**
	 * Updates cost of items in statistics panel
	 * @param amtSpent
	 */
	private void updateAmountSpent(double amtSpent) {
		String formattedAmount = currency.format(amtSpent);
	    spendingAmount.setText(DOLLAR_SIGN + formattedAmount);
	}
	
	/**
	 * Creates the ListView that displays the food and drinks preferences that
	 * the user is unable to satisfy based on their entered budget
	 */
	private void createRemainingList() {
		
		remainingItems = FXCollections.observableArrayList();
		VBox remainingItemBox = new VBox();
		remainingItemBox.setAlignment(Pos.CENTER);
		Label remainingItemLabel = new Label(REMAINING_LIST_TITLE);
		remainingItemList = new ListView <String> ();
		remainingItemList.setPrefSize(WelcomeScreen.CENTER_WIDTH, WelcomeScreen.HEIGHT);
		remainingItemList.setEditable(true);

		remainingItemBox.getChildren().addAll(remainingItemLabel, remainingItemList);

		root.setCenter(remainingItemBox);
		
	}
	
	/**
	 * Updates the remaining items that are displayed in the Remaining Items ListView
	 * @param remaining
	 */
	private void updateRemainingItems (Map<String, ArrayList<String>> remaining) {
		int numPreferencesRemaining = 0;
		for (Entry<String, ArrayList<String>> itemEntry : remaining.entrySet()) {
			String remainingListElement = itemEntry.getKey() + LIST_ENTRY_BRIDGE + extractRemainingItems(itemEntry.getValue().toString());
			remainingItems.add(remainingListElement);
			for (int i = 0; i < itemEntry.getValue().size(); i++) {
				numPreferencesRemaining++;
			}
		}
		remainingItemList.setItems(remainingItems);
		remainingCount.set(numPreferencesRemaining);
	}
	
	/**
	 * Parses the remaining food and drink items for the Remaining Items ListView
	 * @param item
	 * @return
	 */
	private String extractRemainingItems (String item) {
		Matcher matcher = Pattern.compile(PREFERENCES_MATCHER).matcher(item);
		String parsedName = null;
		while(matcher.find()) {
			return (matcher.group(1));
		}
		return parsedName;
	}
	
	private void updateLeftoverFunds(double budget, double amtSpent) {
		double amtLeftover = budget-amtSpent;
		String formattedAmount = currency.format(amtLeftover);
	    leftoverAmount.setText(DOLLAR_SIGN + formattedAmount);
	}
	
	/**
	 * Creates statistics panel that displays the important figures from the party planning
	 */
	private void createStatisticsTracker() {
		
		HBox plannerStatistics = new HBox();
		
		itemsCount = new SimpleIntegerProperty(0);
		remainingCount = new SimpleIntegerProperty(0);
		spendingAmount = new Label(DEFAULT_AMOUNT);
		leftoverAmount = new Label(DEFAULT_AMOUNT);
		
		HBox purchaseStatistics = createStatisticsBox(ITEM_STATISTICS_OUTLINE_COLOR, COST_TRACKER_TITLE, spendingAmount, itemsCount, AFFORDABLE_ITEMS_TITLE);
		HBox remainingStatistics = createStatisticsBox(REMAINING_STATISTICS_OUTLINE_COLOR, LEFTOVER_FUNDS_TRACKER_TITLE, leftoverAmount, remainingCount, REMAINING_ITEMS_TITLE);
		
		plannerStatistics.getChildren().addAll(purchaseStatistics, remainingStatistics);
		
		root.setBottom(plannerStatistics);
		
	}
	
	/**
	 * Factory method that creates the statistics box for each ListView
	 * @param color
	 * @param moneyTrackerTitle
	 * @param moneyTracker
	 * @param itemTracker
	 * @param itemTrackerTitle
	 * @return
	 */
	private HBox createStatisticsBox (String color, String moneyTrackerTitle, Label moneyTracker, SimpleIntegerProperty itemTracker, String itemTrackerTitle) {
		HBox statisticsBox = new HBox(STATISTICS_GAP);
		statisticsBox.setStyle(STATISTICS_OUTLINE + 
									color);
		
		HBox moneyBox = new HBox();
		Label moneyTrackerLabel = new Label(moneyTrackerTitle);
		moneyBox.getChildren().addAll(moneyTrackerLabel, moneyTracker);
		
		Label itemsCountLabel = new Label();
		itemsCountLabel.textProperty().bind(Bindings.concat(itemTrackerTitle, itemTracker));
		
		statisticsBox.getChildren().addAll(moneyBox, itemsCountLabel);
		statisticsBox.setPrefWidth(WelcomeScreen.CENTER_WIDTH);
		
		return statisticsBox;
	}
	
}
