
Assumptions
-------------

In writing my algorithm, I made five major assumptions:
1.) As the party planner, my goal is to provide the highest utility for my guests through food and drinks.
2.) Each guest is equal.
	Example: Fulfilling the preferences of Bob and Janice are of equal importance.
3.) Since each guest's consumable preferences are not ranked, each listed food or drink item is equally preferred by the guest.
	Example: Pizza and chips yield the same utility, regardless of their costs.
4.) The cost associated with each food or drink item is the amount for 1 guest;
	So, in the case where 2 different guests prefer the same item, I would need to buy 2 portions of that item.
	Example: If Bob and Janice both prefer chips, and chips cost $2.00, to fulfill both of their preferences I would need to buy 	$4.00 worth of chips.
5.) The user correctly inputs values into the people.txt, food.txt, and drinks.txt files, meaning proper formatting, spelling, no 	duplicate names, etc.

Algorithm
-------------

Since each food and drink item is equally preferred, I can maximize the happiness of the guests by buying the most items possible.
In other words, from assumptions 1-3 we assume that the less expensive items yield the same utility as the more expensive ones.
To do this, the algorithm finds the cheapest items first, and works its way up to the more expensive ones until the budget
allows for no more items. This is the end goal, but there are numerous steps the algorithm takes to get there.

The steps are as follows:

1.) The algorithm creates two maps to better organize the information: preferencesMap and consumablesMap.
	PreferencesMap maps each guest to his/her preferences, while consumablesMap maps each item to its cost.
	It does this by parsing the three text files and adding the appropriate values from the files to their respective maps.

2.) A copy of each map, called tmpPreferences and tmpConsumables, are made, so that these maps can be modified.

3.) The algorithm iterates through all of the entries in tmpPreferences, which consist of the guest name as the key
	and a list of food and drink items as the value. Within each entry, the algorithm iterates through the
	list of food and drinks to get each preferred consumable. Then, it takes that item and compares it to the key
	of each entry in tmpConsumables, which contains a food or drink item as the key, and its corresponding cost as the value.
	If the preferenced item from tmpPreferences matches the key from tmpConsumables, the price value is then extracted.
	This price is then compared to a variable called cheapestPrice that stores the lowest price,
	and if the price of this item is lower than what is stored in cheapestPrice, cheapestPrice updates to this new lowest price.
	However, more than just cheapestPrice is updated; guestsPleased is a list that will add the name of the guest who desires this 	item, and preferenceIndices is a list that will store the index of the preference list from tmpPreferences that the 
	cheapest item is located at. There is also a variable cheapestItem that stores the name of the item that is currently
	associated with the lowest price, which is updated if a new lowest price is found. Once every food item in tmpPreferences
	is iterated through, the final value of the lowest price is held within the cheapestPrice variable,
	while its name is stored with cheapestItem. Additionally, guestsPleased will have the names of all the guests
	who prefer this item, and preferenceIndices will have the index location of the item for each guest who listed it,
	which comes into play later in the algorithm.
	
4.) Now that the lowest available consumable has been identified, it is time to see what can be afforded. For each unit
	of the cheapest item desired, if cheapestPrice + amountSpent remains lower than the budget, then the item can be bought
	and the algorithm will update amountSpent. The algorithm will also update a map called itemsBought, which has the name
	of the item bought as the key and the number of units bought as the value. To update tmpPreferences and tmpConsumables 	accordingly, the algorithm goes through tmpPreferences again and uses guestsPleased and preferenceIndices to find the guest who 	wants the item, and the index of the cheapest item that was identified within the list of this guest's preferences.
	The algorithm then removes that item from the list of preferences. This process is executed for each guest that listed the item, 
	until there are no more guests in guestsPleased that prefer the item.
	However, if not all the units of the item could have been purchased, that means the algorithm detected that buying another
	unit of the item puts the user over the budget. In this scenario, the algorithm returns itemsBoughtMap as it is,
	which is then used to display which items were affordable under the given budget, and how many of each item the user could buy.
	On the other hand, if all units of the item are affordable, then the map entry for the item is removed from tmpConsumables, 
	since that item is no longer needed, and the loop continues and seeks the next cheapest item.

5.) This process will continue until the algorithm detects that buying the next cheapest item puts the user above the budget.
	However, in the case that the budget is above the collective cost of all the desired consumables,
	then the algorithm returns itemsBoughtMap, which in this case contains every unit of every item preference
	listed, thus satisfying every guest completely.

------------
Test Cases
------------

To test if my algorithm and front-end display work, I primarily changed values in the people.txt, food.txt, and drinks.txt files.
Some specific cases I tested were: integer and double values for prices, and guests who prefer no drinks or food.
By running the program with different variations of the file input, I proved the following:

1.) The order of the food and drinks in the food.txt and drinks.txt files does not matter
2.) The program works for multiple guests with the same name, although the user is recommended to differentiate by last name
3.) The algorithm correctly handles both integer prices and double prices
4.) When there is a guest that prefers no food or no drinks, the algorithm still correctly works;
	however, the user needs to have a blank line in place, instead of moving the preferences up.
	Example: If Joe likes no food but only prefers drinks, his preferences must listed as:
	
	Joe
	
	pineapple juice
	Aaron
	cheese pizza
	mountain dew, root beer
	
	It cannot be:
	
	Joe
	pineapple juice
	Aaron
	cheese pizza
	mountain dew, root beer
	
One interesting nuance that I left unresolved was the case of duplicate names, because I don't agree that a user would do that.
A person would not list, for example, the preferences of John and John. Rather, they would likely make some distinction either
by last name or last initial to list the preferences of John R. and John K. or John Reynolds and John Kowalski to avoid confusion.
Thus, my algorithm requires the user to use distinct names for guests as I consider that part of the proper convention.

As for the other file formatting, the algorithm expects the file to be correctly formatted and follow the convention.
For example, any food or drink item listed as a preference must also have a corresponding price in the correct text file.
Also, the spelling between the preference and food/drink item must match exactly.
Another thing to note is that if the user wants to update preferences of an existing guest, the user should go to the guest's name
in the preferences file and make the changes accordingly there, whether that be removing or adding any preferences.

Other testing I did involved the budget input. Although the budget was supposed to only be an integer, I expanded the functionality
to allow for doubles as well, thus simulating the input of real dollars. To test, I tried the following cases:

0.10 - works 5 - works 5.10 - works 1,234 - works 100,101 - works 100,101.58 - works

.10 - does not work 5.1 - does not work 100,1011 - does not work 100,101.580 - does not work f - does not work f.50 - does not work [blank] - does not work


By going through these cases, I ensured the input would only allow correct formats for the budget.
