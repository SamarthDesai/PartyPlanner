package party_Planning;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculates the optimal food and drinks to buy given a budget, while also keeping track of the items that the user
 * was not able to purchase with the budget constraint
 * @author Samarth Desai
 *
 */
public class SpendingAlgorithm {

	private static final String FILE_PATH = "src/Party_Files/";
	private static final String DRINKS_FILE = FILE_PATH + "drinks.txt";
	private static final String FOOD_FILE = FILE_PATH + "food.txt";
	private static final String PEOPLE_FILE = FILE_PATH + "people.txt";

	private List<String> preferencesList;
	private List<String> consumablesList;
	private List<String> currentList;
	private Map<String, ArrayList <String>> preferencesMap;
	private Map<String, ArrayList<String>> tmpPreferences;
	private Map<String, Double> consumablesMap;
	private Map<String, Double> tmpConsumables;
	private Map<String, Integer> itemsBoughtMap;
	private String guestName;
	private double budget;
	private double amountSpent;

	public SpendingAlgorithm () {

		preferencesList = new ArrayList<String>();
		consumablesList = new ArrayList<String>();
		preferencesMap = new HashMap<String, ArrayList<String>>();
		consumablesMap = new HashMap<String, Double>();
		itemsBoughtMap = new HashMap<String, Integer>();

	}

	/**
	 * Primary algorithm that uses the budget and three text files to provide the optimal food and drinks to purchase
	 * @param currentBudget
	 * @return
	 */
	public Map<String, Integer> selectConsumables(double currentBudget) {
		
		createMaps();
		
		budget = currentBudget;
		
		String cheapestItem;
		double cheapestValue;
		List<String> guestsPleased = new ArrayList<String>();
		List<Integer> preferenceIndices = new ArrayList<Integer>();
		
		amountSpent = 0;
		
		while (amountSpent < budget && !tmpPreferences.isEmpty()) {
			cheapestItem = null;
			cheapestValue = Double.MAX_VALUE;
			for (Entry<String, ArrayList<String>> preferencesEntry : tmpPreferences.entrySet()) {
				for (int i = 0; i<preferencesEntry.getValue().size(); i++) {
					for (Entry<String, Double> itemPriceEntry : tmpConsumables.entrySet()) {
						if (preferencesEntry.getValue().get(i).equals(itemPriceEntry.getKey())) {
							if (itemPriceEntry.getValue() < cheapestValue) {
								guestsPleased.clear();
								preferenceIndices.clear();
								cheapestItem = itemPriceEntry.getKey();
								cheapestValue = itemPriceEntry.getValue();
								guestsPleased.add(preferencesEntry.getKey());
								preferenceIndices.add(i);
							}
							else if(itemPriceEntry.getValue() == cheapestValue && itemPriceEntry.getKey().equals(cheapestItem)) {
								guestsPleased.add(preferencesEntry.getKey());
								preferenceIndices.add(i);
							}
						}
					}
				}
			}

			String currentGuest = null;
			int guestExtra = 0;
			for (int i = 0; i<guestsPleased.size(); i++) {
				if (amountSpent + cheapestValue <= budget) {
					if (guestsPleased.get(i).equals(currentGuest)) {
						guestExtra++;
					}
					tmpPreferences.get(guestsPleased.get(i)).remove(preferenceIndices.get(i).intValue()-guestExtra);
					if (tmpPreferences.get(guestsPleased.get(i)).isEmpty()) {
						tmpPreferences.remove(guestsPleased.get(i));
					}
					amountSpent += cheapestValue;
					currentGuest = guestsPleased.get(i);
					itemsBoughtMap.put(cheapestItem, i+1);
				}
				else {
					return itemsBoughtMap;
				}
			}
			
			tmpConsumables.remove(cheapestItem);
			guestsPleased.clear();
			preferenceIndices.clear();
			
			for(Iterator<Entry <String, ArrayList<String>>> iterator = tmpPreferences.entrySet().iterator(); iterator.hasNext();){
			     Entry<String, ArrayList<String>> entry = iterator.next();
			     if (entry.getValue().isEmpty()) {
			          iterator.remove();
			     }
			 }			
		}
		
		return itemsBoughtMap;

	}
	
	/**
	 * Creates copies of the two maps to allow modification of the data stored within
	 */
	private void createMaps() {
		organizeData();
		
		tmpPreferences = new HashMap<String, ArrayList<String>>(preferencesMap);
		preferencesMap.forEach(tmpPreferences::putIfAbsent);
		tmpConsumables = new HashMap<String, Double>(consumablesMap);
		consumablesMap.forEach(tmpConsumables::putIfAbsent);
	}
	
	/**
	 * Reads and parses the three text files, then creates maps that organize the preference and pricing information
	 */
	private void organizeData() {
		
		fileReader(DRINKS_FILE);
		fileReader(FOOD_FILE);
		fileReader(PEOPLE_FILE);

		createPreferencesMap();
		createConsumablesMap();
		
	}

	/**
	 * Reads in a given file and assigns its values to the proper list
	 * @param fileName
	 */
	private void fileReader (String fileName) {
		File file = new File (fileName);
		currentList = new ArrayList<String>();

		try {

			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				currentList.add(scanner.nextLine());
			}
			scanner.close();
		}

		catch (FileNotFoundException e) {
			System.out.println("Incorrect File Value");
		}

		if (fileName.equals(PEOPLE_FILE)) {
			preferencesList = currentList;
		}

		else {
			consumablesList.addAll(currentList);
		}
	}
	
	/**
	 * Using the preferencesList populated by reading in the people.txt file,
	 * this creates a map that maps guests to a list of the food and drinks they prefer
	 */
	private void createPreferencesMap() {
		for (int i = 0; i < preferencesList.size(); i++) {
			if ((i+1) % 3 == 1) {
				guestName = preferencesList.get(i);
				preferencesMap.put(guestName, new ArrayList<String>());
			}
			else {
				if (!preferencesList.get(i).equals("")) {
					String[] consumablePrefs = preferencesList.get(i).split(", ");
					List<String> consumablePrefsList = new ArrayList<String>(Arrays.asList(consumablePrefs));
					preferencesMap.get(guestName).addAll(consumablePrefsList);
					preferencesMap.put(guestName, preferencesMap.get(guestName));
				}
			}

		}

	}

	/**
	 * Using the consumablesList populated by reading in the food.txt and drinks.txt files,
	 * this creates map that maps food and drink items to their unit prices
	 */
	private void createConsumablesMap() {

		String[] itemAndCost;
		String itemName;
		double itemCost;

		for (int i = 0; i < consumablesList.size(); i++) {
			itemAndCost = consumablesList.get(i).split(":");
			itemName = extractItem(itemAndCost[0]);
			itemCost = Double.parseDouble(itemAndCost[1]);
			consumablesMap.put(itemName, itemCost);
		}
	}

	/**
	 * Parses the food and drink item names by removing the surrounding <>
	 * @param item
	 * @return
	 */
	private String extractItem(String item) {
		Matcher matcher = Pattern.compile("\\<(.*?)\\>").matcher(item);
		String parsedName = null;
		while(matcher.find()) {
			return (matcher.group(1));
		}
		return parsedName;
	}
	
	/**
	 * Sends map of the remaining items that the user could not purchase
	 * @return
	 */
	public Map<String, ArrayList<String>> getRemainingItems(){
		return tmpPreferences;
	}
	
	/**
	 * Sends the total cost for the food and drink items that were selected
	 * @return
	 */
	public double getAmountSpent() {
		return amountSpent;
	}

}