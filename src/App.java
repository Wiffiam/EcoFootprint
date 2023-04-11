import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.*;
import java.util.*;

public class App {
    public static void clearScreen(){
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
    public static double findWattage(){
        //initializes scanner and chrome webdriver
        Scanner input = new Scanner(System.in);
        WebDriver driver = new ChromeDriver();
        //opens a window to the PCPartPicker url
        driver.get("https://pcpartpicker.com");
        System.out.println("A window has been opened for you to configure your current computer specifications.");
        System.out.println("What is your PCPartPicker build URL?");
        String url = input.nextLine();
        //opens the users build url and pulls the css element corresponding to the pc's estimated wattage
        driver.get(url);
        String HTMLwattage = driver.findElement(By.cssSelector(".partlist__keyMetric")).getAttribute("innerHTML");
        //trims the CSS element down to only contain the numerical value of the wattage followed by the W suffix
        String wattage = ((HTMLwattage.substring(HTMLwattage.indexOf("</span>"))).replace("</span>","").replace("</a>","")).trim();
        //debug
        System.out.println("How many hours do you use your PC per day?");
        double usage = input.nextDouble();
        int wattageNum = Integer.parseInt((wattage).replace("W","").trim());
        double kgCarbon = 0.000433*usage*wattageNum;
        System.out.println("Your PC's estimated wattage: ");
        System.out.println(wattage);
        System.out.println("Your PC's estimated carbon emissions per year: ");
        driver.quit();
        return kgCarbon;
    }
    public static String [][] csvConversion() throws FileNotFoundException {
        Scanner readFile = new Scanner(new File("src/sorted_vehicles.csv"));
        List<List<String>> dataList = new ArrayList<>();

        while(readFile.hasNext()){
            String[] line = readFile.nextLine().split(",");
            List<String> row = new ArrayList<>();
            for (String s : line) {
                row.add(s.trim());
            }
            dataList.add(row);
        }
        int rows = dataList.size();
        int columns = dataList.get(0).size();
        String [][]csvFile = new String[rows][columns];
        for(int r=0;r<rows;r++){
            for(int c=0;c<columns;c++){
                csvFile[r][c]=dataList.get(r).get(c);
            }
        }
        //make: column 46
        //model: column 47
        //year: column 63
        return csvFile;
    }
    public static double carCarbon() throws FileNotFoundException{
        //initializes a scanner for the method to capture user inputs
        Scanner input = new Scanner(System.in);
        //gets user's car information
        System.out.println("What model year is your car?");
        String carYear = input.nextLine();
        System.out.println("What make is your car?");
        String carMake = input.nextLine();
        System.out.println("How many kilometers do you drive per year?");
        //converts km to miles for use in later calculations
        double milesDriven = (input.nextInt())/1.609;

        //calls upon the csv conversion method in order to get a 2D array containing all US car EPA fuel mileages since 1985
        String carDatabase[][] = csvConversion();
        //creates a list to store all elements in the 2D array that match the user's model year and make
        List<Integer> modelRowIndexes = new ArrayList<Integer>();
        System.out.println("Select your vehicle from the following options:");
        //initializes a counter to save the number of vehicles that match the user's search
        int counter = 0;
        //linear search algorithm to find which elements in the 2D array match both the car make and car year
        for(int row=0;row<carDatabase.length;row++){
            //column 46 corresponds to the make of the car while column 63 corresponds to the year in the CSV file
            if(carDatabase[row][46].equals(carMake)&&carDatabase[row][63].equals(carYear)){
                counter++;
                modelRowIndexes.add(row);
                System.out.println(counter+": "+carDatabase[row][47]);
            }
        }
        int carChoice = input.nextInt();
        System.out.println(carDatabase[modelRowIndexes.get(carChoice-1)][47]);
        System.out.println("Gas Mileage: "+ carDatabase[modelRowIndexes.get(carChoice-1)][15]+"mpg combined");
        double gallonsConsumed = milesDriven/(Integer.parseInt(carDatabase[modelRowIndexes.get(carChoice-1)][15]));
        double kgCarbon = gallonsConsumed * 8.887;
        System.out.println(kgCarbon);
        return kgCarbon;
    }
    public static double homeCarbon(){
        Scanner input = new Scanner(System.in);
        System.out.println("Test");
        
    }

    public static void main(String[] args) throws FileNotFoundException{
        //initializes 1D array to save the number of times the user has run through each menu option of the program. This will be useful when comparing the user's carbon emissions data
        int[] calculationsPerformed = {0,0,0};
        double[] carbon = {0,0,0};
        double totalCarbon = 0;
        boolean exit = false;
        Scanner input = new Scanner(System.in);
        while(!exit){
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
            switch(menu_option){
                case "a":
                    carbon[0] = findWattage();
                    calculationsPerformed[0]+=1;
                    break;
                case "b":
                    carbon[1] = carCarbon();
                    calculationsPerformed[1]+=1;
                    break;
                case "c":
                    carbon[2] = homeCarbon();
                    calculationsPerformed[2]+=1;
                    break;
                case "d":
                    System.out.println("Option D");
                    totalCarbon = carbon[0]+carbon[1]+carbon[2];
                    System.out.println(totalCarbon);
                    input.nextLine();
                    break;
                case "e":
                    System.out.println("Option E placeholder");
                    input.nextLine();
                    break;
                case "f":
                    carbon[0]=carbon[1]=carbon[2] = 0;
                    calculationsPerformed[0]=calculationsPerformed[1]=calculationsPerformed[2]=0;
                    System.out.println("Carbon emissions data has been reset");
                    System.out.println("Press any key to continue");
                    input.nextLine();
                default:
                    System.out.println("Goodbye");
                    exit = true;
                    break;
            }
        }
    }
}