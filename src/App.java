import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.*;
import java.util.*;
import java.lang.*;

public class App {
    /**
     * The clearScreen method prints out characters and uses flush to wipe out any previous text in the console.
     * 
     * @author javatpoint
     * @version 1.0
     * @since 2023-04-11
     */
    public static void clearScreen(){
        //CREDIT https://www.javatpoint.com/how-to-clear-screen-in-java
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
    /**
     * The findWattage method extracts the wattage of a computer given its PCPartPicker link using the browser automation tool Selenium.
     * It goes to the site and scrapes the CSS element corresponding to the wattage of the computer, and saves it as a double.
     * 
     * @return A double corresponding to the kg of carbon the user's PC emits.
     * @author William Wu
     * @version 1.0
     * @since 2023-04-11
     */
    public static double findWattage(){
        //initializes scanner and chrome webdriver
        Scanner input = new Scanner(System.in);
        ChromeOptions options = new ChromeOptions();

        //bot detection bypass is found online
        //disable bot flags for selenium
        //CREDIT: https://www.zenrows.com/blog/selenium-avoid-bot-detection#how-websites-detect-selenium
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features", "AutomationControlled");

        //headless operation
        //CREDIT: https://www.zenrows.com/blog/selenium-avoid-bot-detection#how-websites-detect-selenium
        options.addArguments("--headless"); // Use a headless browser to bypass Cloudflare
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        //mimic user agent
        //CREDIT: https://www.zenrows.com/blog/selenium-avoid-bot-detection#how-websites-detect-selenium
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");

        //original code from here
        //change resolution
        options.addArguments("--window-size=1920,1080");

        //starts the web driver and opens chrome browser
        WebDriver driver = new ChromeDriver();

        clearScreen();
        //opens a window to the PCPartPicker url and prompts user to enter the link of their PC build
        driver.get("https://pcpartpicker.com/list/");
        System.out.println("A window has been opened for you to configure your current computer specifications.");
        System.out.println("What is your PCPartPicker build URL?");
        String url = input.nextLine();
        //opens the users build url and pulls the css element corresponding to the pc's estimated wattage
        driver.get(url);
        String HTMLwattage = driver.findElement(By.cssSelector(".partlist__keyMetric")).getAttribute("innerHTML");
        //trims the CSS element down to only contain the numerical value of the wattage followed by the W suffix
        String wattage = ((HTMLwattage.substring(HTMLwattage.indexOf("</span>"))).replace("</span>","").replace("</a>","")).trim();
        //stores hours of use as a double for later calculations
        System.out.println("How many hours do you use your PC per day?");
        double usage = input.nextDouble();
        //extra input scanner to prevent the next scanner from being empty
        input.nextLine();
        //gets the wattage as a number by deleting the W suffix
        int wattageNum = Integer.parseInt((wattage).replace("W","").trim());
        //calculates number of watt hours and multiplies by 0.000433 which is the estimated kg of carbon emissions per Wh used and multiples by 365 to find the yearly total
        double kgCarbon = 365*0.000433*usage*wattageNum;
        //outputs the data to the user and formats to two decimal places using printf
        System.out.println("Your PC's estimated wattage: "+wattage);
        System.out.printf("Your PC's estimated carbon emissions per year: %.2f",kgCarbon);
        System.out.print(" kg\n");
        System.out.println("Press any key to continue");
        input.nextLine();
        //closes the chrome browser
        driver.quit();
        return kgCarbon;
    }
    /**
     * The csvConversion method converts a user specified CSV file into a 2D array by looping through the given file.
     * It also separates elements into rows and columns.
     * 
     * @param file
     * @return A 2D string array corresponding to the data in the given CSV file.
     * @throws FileNotFoundException
     * @author William Wu
     * @version 1.0
     * @since 2023-04-11
     */
    public static String [][] csvConversion(String file) throws FileNotFoundException {
        //creates a new scanner to read from the given file
        //this method will throw any FileNotFoundException. this is done so the code does not have to be surrounded with try and catch statements
        Scanner readFile = new Scanner(new File("src/"+file+".csv"));
        //creates a 2D string list to store the data from the CSV files. a list is used instead of an array here as we do not know how many elements we will need yet
        List<List<String>> dataList = new ArrayList<>();

        //reads from the file until there are no more lines
        while(readFile.hasNext()){
            //splits each line by commas as it is the delimiter for CSV files and saves as 1D string array
            String[] line = readFile.nextLine().split(",");
            //creates a new empty string list to store the values of each row
            List<String> row = new ArrayList<>();
            //iterates through each element of the line array (storing the elements of each row) and adds it to the list
            for (String s : line) {
                row.add(s.trim());
            }
            //after all elements in the row are added to the list, the list is added to the main datalist and the program moves onto the next line
            dataList.add(row);
        }
        //sets up a 2D string array with the dimensions of the list
        int rows = dataList.size()-1;
        int columns = dataList.get(0).size()-1;
        String [][]csvFile = new String[rows][columns];
        //converts the 2D list into a 2D array by iterating through each row and column and setting the element in the 2D array equal to that of the list
        for(int r=0;r<rows;r++){
            for(int c=0;c<columns;c++){
                csvFile[r][c]=dataList.get(r).get(c);
            }
        }
        //returns the final 2D array
        return csvFile;
    }
    /**
     * The carCarbon method calculates the yearly carbon emissions of the user's vehicle in kg, given the model year, make, and model.
     * It uses linear search on a 2D array in order to find the user's vehicle emissions.
     * 
     * @return A double corresponding to the car's yearly carbon emissions in kg.
     * @throws FileNotFoundException
     * @author William Wu
     * @version 1.0
     * @since 2023-04-12
     */
    public static double carCarbon() throws FileNotFoundException{
        //this method will throw any FileNotFoundException. this is done so the code does not have to be surrounded with try and catch statements
        clearScreen();
        //initializes a scanner for the method to capture user inputs
        Scanner input = new Scanner(System.in);
        //gets user's car information
        System.out.println("What model year is your car?");
        String carYear = input.nextLine();
        System.out.println("What make is your car?");
        String carMake = input.nextLine();
        System.out.println("How many kilometers do you drive per year?");
        //converts km to miles for use in later calculations
        double milesDriven = (Integer.parseInt(input.nextLine()))/1.609;

        //calls upon the csv conversion method in order to get a 2D array containing all US car EPA fuel mileages since 1985
        String carDatabase[][] = csvConversion("vehicles");
        //creates a list to store all elements in the 2D array that match the user's model year and make
        List<Integer> modelRowIndexes = new ArrayList<Integer>();
        System.out.println("Select your vehicle from the following options:");
        //initializes a counter to save the number of vehicles that match the user's search
        int counter = 0;
        //linear search algorithm to find which elements in the 2D array match both the car make and car year
        for(int row=0;row<carDatabase.length;row++){
            //column 46 corresponds to the make of the car while column 63 corresponds to the year in the CSV file
            if(carDatabase[row][46].equalsIgnoreCase(carMake)&&carDatabase[row][63].equals(carYear)){
                //adds to the counter when a match is found
                counter++;
                //saves the row index of the match
                modelRowIndexes.add(row);
                //prints the matching element to the user
                System.out.println(counter+": "+carDatabase[row][47]);
            }
        }
        //gets the correct model input from the user
        int carChoice = Integer.parseInt(input.nextLine());
        //confirms choice
        System.out.println("Your choice: "+carDatabase[modelRowIndexes.get(carChoice-1)][47]);
        //shows the gas mileage by pulling from column 15 of the converted CSV 2d array
        System.out.println("Gas Mileage: "+ carDatabase[modelRowIndexes.get(carChoice-1)][15]+" mpg combined");
        //calculates the number of gallons consumed by dividing miles driven by miles per gallon
        double gallonsConsumed = milesDriven/(Integer.parseInt(carDatabase[modelRowIndexes.get(carChoice-1)][15]));
        //calculates kg of carbon used per year by multiplying by 8.887 kg of carbon per gallon of gas consumed
        double kgCarbon = gallonsConsumed * 8.887;
        //prints out yearly carbon emissions formatted to 2 decimal places
        System.out.printf("Carbon emissions per year: %.2f",kgCarbon);
        System.out.print(" kg\n");
        System.out.println("Press any key to continue");
        input.nextLine();
        //returns the carbon emissions back to the main program
        return kgCarbon;
    }
    /**
     * The homeCarbon method calculates the yearly carbon emissions of a user's electricity and natural gas consumption.
     * It is a recursive method and progresses through the questions by calling itself and adding one to its iteration count.
     * 
     * @param count
     * @return A double corresponding to the user's home carbon emissions in kg.
     * @author William Wu
     * @version 1.0
     * @since 2023-04-12
     */
    public static double homeCarbon(int count){
        //all carbon emission constants are obtained from the EPA
        Scanner input = new Scanner(System.in);
        double kgCarbon = 0;
        //halt condition for the recursive method
        if(count>=3){
            return 0;
        //first option, divides the total home carbon emissions by number of people in the household
        }else if(count == 0){
            System.out.println("How many people are in your household, including you?");
            int people = input.nextInt();
            return homeCarbon(count+1)/people;
        //second option, multiplies the gas bill by 60.78 which is the kg of carbon emitted per dollar on a monthly natural gas bill multiplied by 12 for the total emissions in one year
        //the gas carbon emissions are then added to the rest of the home carbon emissions
        }else if(count == 1){
            System.out.println("What is your monthly estimated natural gas bill?");
            double gasBill = input.nextDouble();
            return (gasBill * 60.78)+homeCarbon(count+1);
        //third option, multiplies the electricity bill by 1.814 which is a constant for how many kg of carbon are emitted per dollar spent on a monthly electricity bill which is then multiplied by 12 for emissions in one year
        //the electricity carbon emissions are then added to the rest of the home carbon emissions
        }else if(count == 2){
            System.out.println("What is your monthly estimated electricity bill?");
            double electricityBill = input.nextDouble();
            return (electricityBill * 1.814)+homeCarbon(count+1);
        //catch all for if the parameters do not match any of these cases
        }else{
            return kgCarbon;
        }
    }
    /**
     * The binarySearchCarbonEmissions method uses binary search to find a user specified country's carbon emissions per capita in a given year.
     * It first calls on the csvConversion method to get the 2D array necessary to do binary search on, then progresses through the search algorithm until a match is found.
     * 
     * @param country
     * @return A string corresponding to specified country's average carbon emissions per capita in kg.
     * @throws FileNotFoundException
     * @author William Wu
     * @version 1.0
     * @since 2023-04-12
     */
    public static String binarySearchCarbonEmissions(String country) throws FileNotFoundException{
        //this method throws FileNotFoundException so there is no need to surround the code with try catch statements
        //initializes a 2D array with data in the file co2data.csv
        String[][] co2database = csvConversion("co2data");
        //initializes variables to use for binary search
        int left = 0;
        int right = co2database.length - 1;
        while(left<=right){
            //searches first in the middle of the array
            int middle = (left+right)/2;
            //if the search is on the right side of the target, the left side of the array will be deleted and the search will continue
            //if the search is on the left side of the target, the right side of the array will be deleted and the search will continue
            if(co2database[middle][0].compareToIgnoreCase(country)<0){
                left = middle + 1;
            }else if(co2database[middle][0].compareToIgnoreCase(country)>0){
                right = middle - 1;
            //if a match is found, return the CO2 emissions data of the target row
            }else{
                return co2database[middle][3];
            }
        }
        //returns a blank string if no match is found
        return "";

    }
    /**
     * The saveFile method uses the FileWriter and PrintWriter to write to a file using formatted text.
     * The method will write the user's carbon emissions data from their car, home, and PC.
     * 
     * @param pc
     * @param car
     * @param home
     * @throws FileNotFoundException
     * @throws IOException
     * @author William Wu
     * @version 2023-04-12
     * @since 2023-04-12
     */
    public static void saveFile(double pc, double car, double home) throws FileNotFoundException, IOException{
        //throws FileNotFoundException and IOException to prevent surrounding with try catch statements
        //initializes filewriter and printwriter for outputting of formatted text to a file
        FileWriter writer = new FileWriter("carbon_results.txt");
        PrintWriter fOutput = new PrintWriter(writer);
        System.out.println("Saving carbon emission data to file...");
        //prints out the pc, car, and home carbon emissions formatted to 2 decimal places
        fOutput.printf("PC carbon emissions: %.2f",pc);
        fOutput.print(" kg\n");
        fOutput.printf("Car carbon emissions: %.2f",car);
        fOutput.print(" kg\n");
        fOutput.printf("Home carbon emissions: %.2f",home);
        fOutput.print(" kg");
        //prints a message that the file is finished saving and closes the 2 writers
        System.out.println("Finished saving");
        fOutput.close();
        writer.close();
    }
    /**
     * The main method contains the menu and calls on the other functions in this program. 
     * It also stores all of the user's carbon emissions data.
     * It can also give the user recommendations to reduce their carbon emissions.
     * 
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     * @author William Wu
     * @version 1.0
     * @since 2023-04-12
     */
    public static void main(String[] args) throws FileNotFoundException, IOException{
        //initializes 1D array to save the number of times the user has run through each menu option of the program. This will be useful when comparing the user's carbon emissions data
        double[] carbon = {0,0,0};
        //initializing the carbon consumption to 0
        double totalCarbon = 0;
        //sets the menu exit to false until user decides to exit
        boolean exit = false;
        //initializes scanner
        Scanner input = new Scanner(System.in);
        while(!exit){
            //clears the screen of clutter when the user is directed to the menu
            clearScreen();
            //prompts the user to input a menu selection
            System.out.println("Welcome to the Carbon Footprint calculator.");
            System.out.println("Please select a menu option:");
            System.out.println("a: Calculate your PC's carbon emissions");
            System.out.println("b: Calculate your car's carbon emissions");
            System.out.println("c: Calculate your home's carbon emissions");
            System.out.println("d: Get recommendations to reduce your carbon footprint");
            System.out.println("e: Save results to a text file");
            System.out.println("f: Reset");
            System.out.println("Press any other key to exit");

            String menu_option = input.nextLine();
            //switch statement based on which option the user selects
            switch(menu_option){
                case "a":
                    //directs the user to the findWattage method and sets the first element in the carbon array to be equal to the PC's carbon emissions
                    carbon[0] = findWattage();
                    break;
                case "b":
                    //directs the user to the carCarbon method and sets the second element in the carbon array to be equal to the car's carbon emissions
                    carbon[1] = carCarbon();
                    break;
                case "c":
                    clearScreen();
                    //directs the user to the homeCarbon method and sets the third element in the carbon array to be equal to the home's carbon emissions
                    carbon[2] = homeCarbon(0);
                    //outputs the user's home carbon emissions formatted to 2 decimal places
                    System.out.printf("Your home carbon emissions per year: %.2f",carbon[2]);
                    System.out.print(" kg\n");
                    System.out.println("Press any key to continue");
                    input.nextLine();
                    break;
                case "d":
                    //adds up the user's carbon emissions by adding all elements in the carbon array
                    totalCarbon = carbon[0]+carbon[1]+carbon[2];
                    clearScreen();
                    //outputs the user's total carbon emissions formatted to 2 decimal places
                    System.out.printf("Your total carbon emissions: %.2f",totalCarbon);
                    System.out.print(" kg\n");
                    System.out.println("What country would you like to compare your carbon footprint to?");
                    String country = input.nextLine();
                    //searches for the user specified country's carbon emissions using the binary search method and converts it to a double
                    double compareEmissions = (Double.parseDouble(binarySearchCarbonEmissions(country))*1000);
                    //outputs the average carbon footprint in the user specified country
                    System.out.print("The average carbon footprint in "+country+" is ");
                    System.out.printf("%.2f",compareEmissions);
                    System.out.print(" kg\n");
                    double percentage = 0;
                    //compares the user's carbon footprint versus the specified country
                    if(totalCarbon>compareEmissions){
                        //calculates how much more the user emits than the country and outputs the percentage
                        percentage = (totalCarbon-compareEmissions)/Math.abs(compareEmissions)*100;
                        System.out.println("-------------------------------------------------------------");
                        System.out.printf("Your carbon footprint is %.2f",percentage);
                        System.out.print("% greater than the average resident of "+country+"\n");
                        System.out.println("-------------------------------------------------------------");
                        System.out.print("Your biggest source of carbon emissions is");
                        //pc is largest source of carbon emissions
                        //gives tips to reduce PC power consumption
                        if(carbon[0]>carbon[1]&&carbon[0]>carbon[2]){
                            System.out.println(" your computer.");
                            System.out.println("No offense, but maybe try going outside and touching some grass.");
                            System.out.println("Some ways you can reduce your PC's electricity consumption:");
                            System.out.println("Turn on power savings mode. The couple of FPS lost is worth saving the planet.");
                            System.out.println("If your PC is very old, consider upgrading to modern technology. Lots of advancements have been made to increase both performance and efficiency over the years.");
                            System.out.println("If you do plan to do this, make sure to recycle your old PC through ewaste facilities.");
                        //car is largest source of carbon emissions
                        //gives tips to reduce car carbon emissions
                        }else if(carbon[1]>carbon[0]&&carbon[1]>carbon[2]){
                            System.out.println(" your car.");
                            System.out.println("Some ways you can reduce your car's carbon footprint.");
                            System.out.println("Drive more efficiently by avoiding hard acceleration and braking. It's ok to not be a Formula 1 racer in traffic, and you can increase your gas mileage by up to 40% (EPA)!");
                            System.out.println("You can also increase your mileage by going the speed limit. The EPA estimates that driving over the speed limit can reduce efficiency by up to 14%. It's also much safer.");
                            System.out.println("If your car is very old or low in gas mileage, you can also consider purchasing a hybrid or electric vehicle. These vehicles get much better fuel economy, save you money, and help the environment. Try to buy second hand if you can!");
                        //home is largest source of carbon emissions
                        //gives tips to reduce home carbon emissions
                        }else if(carbon[2]>carbon[0]&&carbon[2]>carbon[1]){
                            System.out.println(" your home.");
                            System.out.println("An easy way to save electricity at home is to turn off the lights when you are not using them. You can also upgrade incandescant light bulbs to LED or ENERGY STAR bulbs to save up to 75% of the energy.");
                            System.out.println("A great way to reduce your natural gas bill is to use less hot water by doing washing your clothes with cold water. You can also turn on the vacation mode on your furnace when you are away for extended periods of time.");
                            System.out.println("You can also check if your home is insulated properly. Most home carbon emissions and electricity/natural gas costs come from losses associated with improperly insulated homes.");
                            System.out.println("Make sure to check with your local government to see if there are incentives for upgrading to more efficient furnaces/air conditioning and other major appliances.");
                        }
                    //same as above but calculates how much percent less carbon the user emits compared to the specified country
                    }else if(totalCarbon<compareEmissions){
                        System.out.println("-------------------------------------------------------------");
                        percentage = (compareEmissions-totalCarbon)/Math.abs(compareEmissions)*100;
                        System.out.printf("Your carbon footprint is %.2f",percentage);
                        System.out.print("% less than the average resident of "+country+"\n");
                        System.out.println("-------------------------------------------------------------");
                        System.out.println("Even though you are doing well compared to the average, here are some ways you can reduce your carbon footprint:");
                        System.out.println("-------------------------------------------------------------");
                        System.out.print("Your biggest source of carbon emissions is");
                        if(carbon[0]>carbon[1]&&carbon[0]>carbon[2]){
                            System.out.println(" your computer.");
                            System.out.println("No offense, but maybe try going outside and touching some grass");
                            System.out.println("Some ways you can reduce your PC's electricity consumption:");
                            System.out.println("Turn on power savings mode. The couple of FPS lost is worth saving the planet.");
                            System.out.println("If your PC is very old, consider upgrading to modern technology. Lots of advancements have been made to increase both performance and efficiency over the years.");
                            System.out.println("If you do plan to do this, make sure to recycle your old PC through ewaste facilities.");
                        }else if(carbon[1]>carbon[0]&&carbon[1]>carbon[2]){
                            System.out.println(" your car.");
                            System.out.println("Some ways you can reduce your car's carbon footprint");
                            System.out.println("Drive more efficiently by avoiding hard acceleration and braking. It's ok to not be a Formula 1 racer in traffic, and you can increase your gas mileage by up to 40% (EPA)!");
                            System.out.println("You can also increase your mileage by going the speed limit. The EPA estimates that driving over the speed limit can reduce efficiency by up to 14%. It's also much safer.");
                            System.out.println("If your car is very old or low in gas mileage, you can also consider purchasing a hybrid or electric vehicle. These vehicles get much better fuel economy, save you money, and help the environment. Try to buy second hand if you can!");
                        }else if(carbon[2]>carbon[0]&&carbon[2]>carbon[1]){
                            System.out.println(" your home.");
                            System.out.println("An easy way to save electricity at home is to turn off the lights when you are not using them. You can also upgrade incandescant light bulbs to LED or ENERGY STAR bulbs to save up to 75% of the energy.");
                            System.out.println("A great way to reduce your natural gas bill is to use less hot water by doing washing your clothes with cold water. You can also turn on the vacation mode on your furnace when you are away for extended periods of time.");
                            System.out.println("You can also check if your home is insulated properly. Most home carbon emissions and electricity/natural gas costs come from losses associated with improperly insulated homes.");
                            System.out.println("Make sure to check with your local government to see if there are incentives for upgrading to more efficient furnaces/air conditioning and other major appliances.");
                        }else{
                            //goes to this case if the user has not made any calculations yet
                            System.out.println(" unavailable. Please run some of the menu options before comparing.");
                        }
                    }else{
                        //catch all in case no information is available
                        System.out.println("Please try again");
                    }
                    System.out.println("Press any key to continue");
                    input.nextLine();
                    break;
                case "e":
                    //calls on the save file method to write the pc, car, and home carbon data
                    clearScreen();
                    saveFile(carbon[0],carbon[1],carbon[2]);
                    System.out.println("Press any key to continue");
                    input.nextLine();
                    break;
                case "f":
                    //sets all carbon data back to 0 by setting each array element to 0
                    carbon[0]=carbon[1]=carbon[2] = 0;
                    clearScreen();
                    System.out.println("Carbon emissions data has been reset");
                    System.out.println("Press any key to continue");
                    input.nextLine();
                    break;
                default:
                    //exits if the user presses any other key
                    System.out.println("Goodbye");
                    exit = true;
                    input.close();
                    break;
            }
        }
    }
}