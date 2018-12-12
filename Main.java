//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title:           JavaFX Team Project
// Files:           Main.java
// Course:          CS400
//
// Author:          Sophia Choi, Felipe Shinsato, Siyuan Ma, Addison Smith (Team 9)
// Email:           hchoi247@wisc.edu
// Lecturer's Name: Andrew Kuemmel
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ///////////////////
//
// Partner Name:    (name of your pair programming partner)
// Partner Email:   (email address of your programming partner)
// Partner Lecturer's Name: (name of your partner's lecturer)
// 
// VERIFY THE FOLLOWING BY PLACING AN X NEXT TO EACH TRUE STATEMENT:
//   _ Write-up states that pair programming is allowed for this assignment.
//   _ We have both read and understand the course Pair Programming Policy.
//   _ We have registered our team prior to the team registration deadline.
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully 
// acknowledge and credit those sources of help here.  Instructors and TAs do 
// not need to be credited here, but tutors, friends, relatives, room mates, 
// strangers, and others do.  If you received no outside help from either type
//  of source, then please explicitly indicate NONE.
//
// Persons:         (identify each person and describe their help in detail)
// Online Sources:  (identify each URL and describe their assistance in detail)
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Main extends Application {
	
	
	//Create a new FoodData object
	FoodData foodObject = new FoodData();
	
	
	//Create a new list for Meal List 
	List<FoodItem> meals = new LinkedList<FoodItem>();
	
	//Create a new list for FilteredList
	List<FoodItem> filtered = new LinkedList<FoodItem>();
	
	//Filename String
	String fileName="";
	
	//List of rules 
	List<String> displayRuleList = new LinkedList<String>();
	
	
	public void start(Stage primaryStage) {
		try {
			// Setting up the root. Cells will contain nodes of application
			GridPane root = new GridPane();
			root.setHgap(10);
			root.setVgap(10);
			root.setPadding(new Insets(10, 10, 10, 10));


			// ListView object for food list
			ListView<FoodItem> foodList = new ListView<FoodItem>();
			foodList.setPrefHeight(500);
			foodList.setPrefWidth(280);
			foodList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			root.add(foodList, 0, 1, 1, 3);
			
			// Text object for food list which includes the number of food items in the list
			Text foodText = new Text("Food List (" + foodList.getItems().size() + ")");
			
			root.add(foodText, 0, 0);
			root.setHalignment(foodText, HPos.CENTER);

			// Text object for the current meal list
			Text mealText = new Text("Current Meal");
			root.add(mealText, 1, 0);
			root.setHalignment(mealText, HPos.CENTER);
			// ListView object for the current meal list
			ListView<FoodItem> mealList = new ListView<FoodItem>();
			mealList.setPrefHeight(500);
			mealList.setPrefWidth(280);
			mealList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			root.add(mealList, 1, 1, 1, 3);

			// Create the Input File and add Food buttons
			Button addFromFileButton = new Button("Add Food from File");
			Button addFoodButton = new Button("Manually Add Food");

			// Add Food from File button
			addFromFileButton.setPrefHeight(40);
			addFromFileButton.setPrefWidth(140);
			addFromFileButton.setPadding(new Insets(5, 5, 5, 5));
			

			///////////////////////LOAD FROM FILE SECTION//////////////////////////
			// Create modal pop up when Add Food from File button is clicked
			addFromFileButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					
					//clear the food Object, since we are loading a new file 
					clear();
					
					final Stage filePopUp = new Stage();
					filePopUp.initModality(Modality.WINDOW_MODAL);

					Button addFileButton = new Button("Add File");
					
					
					VBox addFileBox = new VBox();
					addFileBox.setAlignment(Pos.CENTER);
					TextField nameOfFile = new TextField("Enter name of file here (i.e., food.csv)");
					addFileBox.getChildren().addAll(new Label("File: "), 
							nameOfFile, 
							addFileButton);
					addFileBox.setSpacing(10);
					addFileBox.setPadding(new Insets(10, 10, 10, 10));
					addFileBox.setPrefWidth(300);
					Scene addFileScene = new Scene(addFileBox);

					//Set action for the "Add File" button
					addFileButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							
							//load the file into the foodObject
							foodObject.loadFoodItems(nameOfFile.getText());
							//check if load was successful 
							if (foodObject.getAllFoodItems()!=null) {
								
								//Get 2 lists. One of food items, and one of observable food items 
								List<FoodItem> foodItemList = foodObject.getAllFoodItems();
								ObservableList<FoodItem> ObservableFoodItemList = FXCollections.observableArrayList(); 
								
								
								for (int i =0; i<foodItemList.size(); i++) {
									ObservableFoodItemList.add(foodItemList.get(i));
								}
								
								foodList.setItems(ObservableFoodItemList);
								
								fileName = nameOfFile.getText();
							}
							else {
								
								Alert a = new Alert(AlertType.INFORMATION, "Invalid File Name.", ButtonType.OK);
								a.showAndWait();
							}
							filePopUp.close();
							
							//update food item counter 
							foodText.setText("Food List (" + foodList.getItems().size() + ")");
						}
					});
					
					filePopUp.setScene(addFileScene);
					filePopUp.show();
				}
			});
			///////////////////////END OF LOAD FROM FILE SECTION//////////////////////////
			
			
			// add Food Button
			addFoodButton.setPrefHeight(40);
			addFoodButton.setPrefWidth(140);
			addFoodButton.setPadding(new Insets(5, 5, 5, 5));
			
			
			///////////////////////MANUALLY ADD FOOD SECTION//////////////////////////
			// Create modal pop up when Manually Add Food button is clicked
			addFoodButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					final Stage foodPopUp = new Stage();
					foodPopUp.initModality(Modality.WINDOW_MODAL);

					Button addFoodButton = new Button("Add Food");
					TextField foodId = new TextField("Enter food ID here");
					TextField foodName = new TextField("Enter name of food here");
					TextField caloriesVal = new TextField("Enter number of calories here");
					TextField carbsVal = new TextField("Enter number of carbs here");
					TextField fatVal = new TextField("Enter number of fat here");
					TextField fiberVal = new TextField("Enter number of fiber here");
					TextField proteinVal = new TextField("Enter number of protein here");

					VBox addFoodBox = new VBox();
					addFoodBox.setAlignment(Pos.CENTER);
					addFoodBox.getChildren().addAll(new Label("Food ID: "), foodId,
							new Label("Food Name: "), foodName,
							new Label("Calories: "), caloriesVal,
							new Label("Carbs: "), carbsVal,
							new Label("Fat: "), fatVal,  
							new Label("Fiber: "), fiberVal, 
							new Label("Protein: "), proteinVal, 
							addFoodButton);
					addFoodBox.setSpacing(10);
					addFoodBox.setPadding(new Insets(10, 10, 10, 10));
					addFoodBox.setPrefWidth(300);
					Scene addFoodScene = new Scene(addFoodBox);
					
					addFoodButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							//get all values entered by user:
							String id = foodName.getText();
							String name = foodName.getText();
							String cal = caloriesVal.getText();
							String carbs = carbsVal.getText();
							String fat = fatVal.getText();
							String fiber = fiberVal.getText();
							String protein = proteinVal.getText();
							
							//boolean to see if foodItem is valid
							//boolean isItemValid = false; 
							//See if nutrients are alphanumeric
							try {
								int calInt = Integer.parseInt(cal);
								int carbsInt = Integer.parseInt(carbs);
								int fatInt = Integer.parseInt(fat);
								int fiberInt = Integer.parseInt(fiber);
								int proteinInt = Integer.parseInt(protein);
								
								//see if nutritional values are negative 
								if (calInt<0||carbsInt<0||fatInt<0||fiberInt<0||proteinInt<0) {
									Alert a = new Alert(AlertType.INFORMATION, "Value must be an positive.", ButtonType.OK);
									a.showAndWait();
								}
								
								FoodItem foodItemToAdd = new FoodItem(id,name);
								foodItemToAdd.addNutrient("CALORIES", calInt);
								foodItemToAdd.addNutrient("CARBOHYDRATE", carbsInt);
								foodItemToAdd.addNutrient("FAT", fatInt);
								foodItemToAdd.addNutrient("FIBER", fiberInt);
								foodItemToAdd.addNutrient("PROTEIN", proteinInt);
								
								//add new food item to food data object
								foodObject.addFoodItem(foodItemToAdd);
								
								//add the new item name to foodNameList
								
								//Get 2 lists. One of food items, and one of food names 
								List<FoodItem> foodItemList1 = foodObject.getAllFoodItems();
								
								ObservableList<FoodItem> ObservableFoodItemList1 = FXCollections.observableArrayList(); 
								for (int i = 0; i<foodItemList1.size(); i++) {
									ObservableFoodItemList1.add(foodItemList1.get(i));
								
								}
								foodList.setItems(ObservableFoodItemList1);
								
								foodPopUp.close();
								
								//update food item counter 
								foodText.setText("Food List (" + foodList.getItems().size() + ")");
								
							} catch (NumberFormatException e){
								Alert a = new Alert(AlertType.INFORMATION, "Value must be an integer.", ButtonType.OK);
								a.showAndWait();
							}
							
							
						}
					});

					foodPopUp.setScene(addFoodScene);
					foodPopUp.show();
				}
			});
			///////////////////////END OF MANUALLY ADD FOOD SECTION//////////////////////////
			
			
			// Create a HBox to hold the two add buttons for Food List
			HBox foodListHBox = new HBox();
			foodListHBox.getChildren().addAll(addFromFileButton, addFoodButton);
			root.add(foodListHBox, 0, 4);

			// Create the “Add Food to Meal” button
			Button addToMealListButton = new Button("Add Food to Meal");

			// Create the “Remove Food from Meal” button
			Button removeFoodButton = new Button("Remove Food\n   from Meal");

			// Setting height and width for addToMealListButton
			addToMealListButton.setPrefHeight(40);
			addToMealListButton.setPrefWidth(140);

			// Setting height and width for removeFoodButton
			removeFoodButton.setPrefHeight(40);
			removeFoodButton.setPrefWidth(140);

			
			///////////////////////ADD/REMOVE FROM MEAL LIST SECTION//////////////////////////
			// Move food items in food list to meal list
			addToMealListButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
						//Get 2 lists. One of food items, and one of food names 
						List<FoodItem> mealItemList2 = foodList.getSelectionModel().getSelectedItems();
						
						ObservableList<FoodItem> ObservableFoodItemList2 = FXCollections.observableArrayList(); 
						
						for (int i = 0; i<mealItemList2.size(); i++) {
							ObservableFoodItemList2.add(mealItemList2.get(i));
						}
						
						
						//Get current list of food items in meal list
						List<FoodItem> currentMealList = mealList.getItems();
						for (int j = 0; j<currentMealList.size(); j++) {
							ObservableFoodItemList2.add(currentMealList.get(j));
						}
						
						//sort meal list alphabetically 
						if (ObservableFoodItemList2!=null) {
					    	Collections.sort(ObservableFoodItemList2, 
					    			(food1, food2) -> 
					    	food1.getName().toUpperCase().compareTo(food2.getName().toUpperCase()));
				    	}
						
						
						mealList.setItems(ObservableFoodItemList2);
				}
			});
			
			// Remove food items in meal list
			removeFoodButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
						// Get a list of food items to be removed, and removes the items 
						List<FoodItem> removedItemsList = new LinkedList<FoodItem> (mealList.getSelectionModel().getSelectedItems());
						
						ObservableList<FoodItem> f = mealList.getItems();
						//int removeListSize = removedItemsList.size();
						for (int i =0; i<removedItemsList.size(); i++) {
							mealList.getItems().remove(removedItemsList.get(i));
						}
						
						mealList.setItems(f);
						
						
				}
			});
			///////////////////////ADD/REMOVE FROM MEAL LIST SECTION//////////////////////////
			
			
			
			// Create a HBox to hold the two add and remove buttons for Meal List
			HBox mealListHBox = new HBox();
			mealListHBox.getChildren().addAll(addToMealListButton, removeFoodButton);
			root.add(mealListHBox, 1, 4);

			// Create the Text for the Applied Filter ListView
			Text filterText = new Text("Applied Filters");
			root.add(filterText, 2, 0);
			root.setHalignment(filterText, HPos.CENTER);

			// Create ListView for the filter rule segment and add to grid
			ListView<String> filterList = new ListView<String>();
			filterList.setPrefHeight(300);
			filterList.setPrefWidth(280);
			root.add(filterList, 2, 1, 1, 1);

			// Create the “Add to Filter” and “Reset Filter” buttons
			Button addFilter = new Button("Add to Filter");
			Button resetFilter = new Button("Reset Filter");

			// Set minimum width and height of the addFilter and resetFilter buttons
			addFilter.setPrefWidth(140);
			addFilter.setPrefHeight(25);
			resetFilter.setPrefWidth(140);
			resetFilter.setPrefHeight(25);

			resetFilter.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						
						// Clear our displayRuleList and add the filter listview 
						displayRuleList.clear();
						filterList.getItems().clear();
						 
						// Repopulate listview with all of food items in foodObject
						List<FoodItem> foodItemList4 = foodObject.getAllFoodItems();
						ObservableList<FoodItem> ObservableFoodItemList4 = FXCollections.observableArrayList(); 
						for (int i =0; i<foodItemList4.size(); i++) {
							ObservableFoodItemList4.add(foodItemList4.get(i));
						}
						
						foodList.setItems(ObservableFoodItemList4); 
						
						//update food item counter 
						foodText.setText("Food List (" + foodList.getItems().size() + ")");
					}
				}
					
			);
			
			// Create modal pop up when Add Filter button is clicked
			addFilter.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					final Stage filterPopUp = new Stage();
					filterPopUp.initModality(Modality.WINDOW_MODAL);

					Button addFilterButton = new Button("Add Filter");
					TextField nutrientName = new TextField("Enter nutrient/food name here");
					TextField comparator = new TextField("Enter comparator here (<= OR >= OR ==)");
					TextField value = new TextField("Enter value here");
					Text instruction = new Text("Specify the nutrient or food name you would \n"
							+ "               like to filter your food list by.");
					instruction.setStyle("-fx-font-size: 18 pt");
				
					VBox addFilterBox = new VBox();
					addFilterBox.setAlignment(Pos.CENTER);
					addFilterBox.getChildren().addAll(instruction, new Label("Nutrient/Name: "), 
							nutrientName, new Label("Comparator: "), comparator, 
							new Label("Value: "), value, 
							addFilterButton);
					
					addFilterBox.setSpacing(10);
					addFilterBox.setPadding(new Insets(10, 10, 10, 10));
					addFilterBox.setPrefWidth(300);
					
					// ListView object for filters preview list
					ListView<String> filtersPreviewList = new ListView<String>();
					filtersPreviewList.setPrefHeight(400);
					filtersPreviewList.setPrefWidth(300);
					
					// Button to apply all filters
					Button applyFilterButton = new Button("Apply Filter(s)");
					
					// Create a VBox to hold the list of filters and apply button
					VBox filterPreviewBox = new VBox();
					filterPreviewBox.setAlignment(Pos.CENTER);
					filterPreviewBox.getChildren().addAll(new Label("Applying Filters: "), 
							filtersPreviewList, 
							applyFilterButton);
					filterPreviewBox.setSpacing(10);
					filterPreviewBox.setPadding(new Insets(10, 10, 10, 10));
					filterPreviewBox.setPrefWidth(300);
					
					// Create a HBox to hold the two views of the filter pop up
					HBox filterPopUpHBox = new HBox();
					filterPopUpHBox.getChildren().addAll(addFilterBox, filterPreviewBox);
					
					List<String> rulesList = new LinkedList<String>();
					
					
					///////////////////////FILTER SECTION//////////////////////////
					//Set Action for add Filter Button 
					addFilterButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							
							String nutr = nutrientName.getText().toUpperCase();
							String comp = comparator.getText();
							String val = value.getText();
							
							
							boolean isName = false;
							if (nutr.equals("NAME")) {
								isName = true;
								if (comp.equals("==")){
									rulesList.add(nutr+" "+comp+" "+val);
									
								}
								else {
									Alert a = new Alert(AlertType.INFORMATION, "Invalid Operator for name search."
											+ "must be ==.", ButtonType.OK);
									a.showAndWait();
								}
							}
							
							if (!isName) {
								try {
									int valInt = Integer.parseInt(val);
									if (nutr.equals("CARBOHYDRATE") || nutr.equals("FAT") 
											|| nutr.equals("CALORIES") || nutr.equals("FIBER")
											|| nutr.equals("PROTEIN")) {
										
										if (comp.equals(">=") || comp.equals("<=") || comp.equals("==")) {
											
											rulesList.add(nutr+" "+comp+" "+valInt);
											
										}
										else {
											Alert a = new Alert(AlertType.INFORMATION, "Invalid Operator."
													+ "must be <=, >=, or ==.", ButtonType.OK);
											a.showAndWait();
										}
									}
	//								else if (nutr.equals("NAME")) {
	//									if (comp.equals("==")) {
	//										rulesList.add(nutr +" "+comp+" "+ valInt);
	//									}
	//									else {
	//										Alert a0 = new Alert(AlertType.INFORMATION, "Invalid Operator."
	//												+ "searching on name must use == operator.", ButtonType.OK);
	//										a0.showAndWait();
	//									}
	//								}
									else {
										Alert a1 = new Alert(AlertType.INFORMATION, "Invalid search."
												+ "must search on calories, carbohydrate, "
												+ "fat, fiber, protein, or name.", ButtonType.OK);
										a1.showAndWait();
									}
									
								
								} catch (NumberFormatException e){
									Alert a2 = new Alert(AlertType.INFORMATION, "Value is not an integer.", ButtonType.OK);
									a2.showAndWait();
								}
							}
								
							//add rules to preview list 
							ObservableList<String> ObservableRulesList7 = FXCollections.observableArrayList(); 
							for (int i = 0; i<rulesList.size(); i++) {
								if (!ObservableRulesList7.contains(rulesList.get(i))) {
									ObservableRulesList7.add(rulesList.get(i)); 
								}
							}
								
							//update filtersPreviewList
							filtersPreviewList.setItems(ObservableRulesList7);
						}
					});
					
					//Set Action for Apply filter button
					
					applyFilterButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {	
							
							//add to existing rules list
							for (int i = 0; i<rulesList.size(); i++) {
								String rule = rulesList.get(i);
								if(!displayRuleList.contains(rule)) {
									displayRuleList.add(rule);
								}
							}

							//Get observable list of food items 
							filtered = foodObject.filterByNutrients(displayRuleList);
							
							//sort query results 
							if (filtered!=null) {
						    	Collections.sort(filtered, 
						    			(food1, food2) -> food1.getName().toUpperCase().compareTo(food2.getName().toUpperCase())
						    			);
					    	}
							
							//Observable food item list for foodList List View
							ObservableList<FoodItem> ObservableFilteredList8 = FXCollections.observableArrayList(); 
							
							for (int i =0; i<filtered.size(); i++) {
								ObservableFilteredList8.add(filtered.get(i));
							}
							
							//Make observable filter list for the filter listview
							ObservableList<String> ObservableFilteredRuleList8 = FXCollections.observableArrayList(); 
							for (int i=0; i<displayRuleList.size(); i++) {
								ObservableFilteredRuleList8.add(displayRuleList.get(i));
							}
							
							//Set the filter List View to display the rules
							filterList.setItems(ObservableFilteredRuleList8);
							
							foodList.setItems(ObservableFilteredList8);
							filterPopUp.close();
							
							//update food item counter 
							foodText.setText("Food List (" + foodList.getItems().size() + ")");
							
						
						}
					});

					Scene addFilterScene = new Scene(filterPopUpHBox);
					filterPopUp.setScene(addFilterScene);
					filterPopUp.show();
				}
			});
			
			// Create a HBox to hold the two Filter buttons
			HBox filterHBox = new HBox();
			filterHBox.getChildren().addAll(addFilter, resetFilter);
			root.add(filterHBox, 2, 2);

			// Create the Save Food List button
			Button saveFoodButton = new Button("Save Food List to File");

			// Save Food List Button
			saveFoodButton.setPrefHeight(40);
			saveFoodButton.setPrefWidth(280);
			saveFoodButton.setPadding(new Insets(5, 5, 5, 5));

			// Create modal pop up when Save Food List button is clicked
			saveFoodButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					final Stage saveFoodPopUp = new Stage();
					saveFoodPopUp.initModality(Modality.WINDOW_MODAL);

					// Create button to save file with new name
					Button saveFoodButton = new Button("Save File");
					// Create button to overwrite current file
					Button replaceFoodButton = new Button("Save to Current File");
					// Create field where user will write new file name
					TextField enteredFileName = new TextField("Enter name of file here");
					// Create text to separate 2 options in view
					Label orText = new Label("OR");
					orText.setStyle("-fx-font-size: 18 pt");
					// Create text to tell user instructions
					Text instruction = new Text("Save food list to current file or "
							+ "specify a new file name.");
					instruction.setStyle("-fx-font-size: 18 pt");

					// Create VBox to hold the different options
					VBox saveFoodBox = new VBox();
					saveFoodBox.setAlignment(Pos.CENTER);
					saveFoodBox.getChildren().addAll(instruction, replaceFoodButton, 
							orText, new Label("File Name: "), enteredFileName, saveFoodButton);
					saveFoodBox.setSpacing(10);
					saveFoodBox.setPadding(new Insets(10, 10, 10, 10));
					saveFoodBox.setPrefWidth(300);
					Scene addFoodScene = new Scene(saveFoodBox);
					
					// When save food button is clicked, new file is created
					saveFoodButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							//extract entered filename string and length for ease 
							String str = enteredFileName.getText().toLowerCase();
							int strLen = enteredFileName.getText().length();
							
							if (strLen<=4 || !(str.substring(strLen-4, strLen).equals(".csv"))) {
								Alert noFile = new Alert(AlertType.INFORMATION, 
										"Invalid File Name. Must "
										+ "be in the format of <filename>.csv", ButtonType.OK);
								noFile.showAndWait();
								
							}else {
								foodObject.saveFoodItems(str);
								Alert saveSuccess = new Alert(AlertType.INFORMATION, 
										"Save Successful", ButtonType.OK);
								saveSuccess.showAndWait();
							}
							saveFoodPopUp.close();
						}
					});
					
					// When replace food button is clicked, current file is overwritten
					// with new data from food list
					replaceFoodButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							
							if (fileName=="") {
								Alert noFile = new Alert(AlertType.INFORMATION, 
										"No File Currently Loaded to Save.", ButtonType.OK);
								noFile.showAndWait();
								
							}else {
								foodObject.saveFoodItems(fileName);
								Alert saveSuccess = new Alert(AlertType.INFORMATION, 
										"File Overwritten", ButtonType.OK);
								saveSuccess.showAndWait();
							}
							saveFoodPopUp.close();
						}
					});

					saveFoodPopUp.setScene(addFoodScene);
					saveFoodPopUp.show();
				}
			});
			
			// Create the Analyze button
			Button analyzeButton = new Button("Analyze Meal");

			// Style for the Analyze button
			analyzeButton.setStyle("-fx-font-size: 24pt");

			// Analyze Button
			analyzeButton.setPrefHeight(70);
			analyzeButton.setPrefWidth(280);
			analyzeButton.setPadding(new Insets(5, 5, 5, 5));
			
			// Create modal pop up when Analyze button is clicked
			analyzeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					final Stage analyzePopUp = new Stage();
					analyzePopUp.initModality(Modality.WINDOW_MODAL);

					Button closeButton = new Button("Close");
					Label mealSummary = new Label("Meal Summary");
					mealSummary.setStyle("-fx-font-size: 24 pt");
					
					//Sum nutrient values to display - initialize variables to store totals
					List<FoodItem> mealItemList9 = mealList.getItems();
					int totalCals = 0;
					int totalCarbs = 0;
					int totalFat = 0;
					int totalFiber = 0;
					int totalProtein = 0;
					
					//for loop sums up the nutrient totals
					for(int i = 0; i < mealItemList9.size();i++) {
						totalCals += mealItemList9.get(i).getNutrientValue("CALORIES");
						totalCarbs += mealItemList9.get(i).getNutrientValue("CARBOHYDRATE");
						totalFat += mealItemList9.get(i).getNutrientValue("FAT");
						totalFiber += mealItemList9.get(i).getNutrientValue("FIBER");
						totalProtein += mealItemList9.get(i).getNutrientValue("PROTEIN");
					}
					
					// VBox setup to display nutrient info
					
					VBox analyzeBox = new VBox();
					analyzeBox.setAlignment(Pos.CENTER);
					analyzeBox.getChildren().addAll(mealSummary,
							new Label("Total Calories: "+ totalCals),
							new Label("Total Carbs: "+ totalCarbs),
							new Label("Total Fat: "+ totalFat),
							new Label("Total Fiber: "+ totalFiber),
							new Label("Total Protein: "+ totalProtein),
							closeButton);
					analyzeBox.setSpacing(10);
					analyzeBox.setPadding(new Insets(10, 10, 10, 10));
					analyzeBox.setPrefWidth(300);
					Scene analyzeScene = new Scene(analyzeBox);
					
					closeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							analyzePopUp.close();
						}
					});

					analyzePopUp.setScene(analyzeScene);
					analyzePopUp.show();
				}
			});

			// Create a HBox to hold the two Filter buttons
			VBox vBoxButtons = new VBox();
			vBoxButtons.getChildren().addAll(saveFoodButton, analyzeButton);
			vBoxButtons.setSpacing(13);
			root.add(vBoxButtons, 2, 3, 1, 2);

			root.setMargin(vBoxButtons, new Insets(81, 0, 0, 0));

			// set alignments for buttons at the bottom of the grid
			root.setValignment(addToMealListButton, VPos.BASELINE);
			root.setValignment(removeFoodButton, VPos.BASELINE);
			root.setValignment(vBoxButtons, VPos.BASELINE);

			Scene scene = new Scene(root, 900, 650); // every scene needs a root layout

			// Adds the stylesheets
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//clear method 
	private void clear() {
		foodObject = new FoodData();
	}
	public static void main(String[] args) {
		launch(args);
	}
}