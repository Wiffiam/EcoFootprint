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
    public static int findWattage(){
        Scanner input = new Scanner(System.in);
        WebDriver driver = new ChromeDriver();
        driver.get("https://pcpartpicker.com");
        System.out.println("A window has been opened for you to configure your current computer specifications.");
        System.out.println("What is your PCPartPicker URL?");
        String url = input.nextLine();
        driver.get(url);
        String HTMLwattage = driver.findElement(By.cssSelector(".partlist__keyMetric")).getAttribute("innerHTML");
        String wattage = ((HTMLwattage.substring(HTMLwattage.indexOf("</span>"))).replace("</span>","").replace("</a>","")).trim();
        System.out.println("Your PC's estimated wattage: ");
        System.out.println(wattage);
        int wattageNum = Integer.parseInt((wattage).replace("W","").trim());
        driver.quit();
        return wattageNum;
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
    public static void carCarbon() throws FileNotFoundException{
        Scanner input = new Scanner(System.in);
        System.out.println("What model year is your car?");
        String carYear = input.nextLine();
        System.out.println("What make is your car?");
        String carMake = input.nextLine();
        String carDatabase[][] = csvConversion();
        List<Integer> modelRowIndexes = new ArrayList<Integer>();
        System.out.println("Select your vehicle from the following options:");
        int counter = 0;
        for(int row=0;row<carDatabase.length;row++){
            if(carDatabase[row][46].equals(carMake)&&carDatabase[row][63].equals(carYear)){
                counter++;
                modelRowIndexes.add(row);
                System.out.println(counter+": "+carDatabase[row][47]);
            }
        }
        int carChoice = input.nextInt();
        System.out.println(carDatabase[modelRowIndexes.get(carChoice-1)][47]);
        System.out.println("Gas Mileage: "+ carDatabase[modelRowIndexes.get(carChoice-1)][15]+" combined");
        input.nextLine();
    }

    public static void main(String[] args) throws FileNotFoundException{
        boolean completedMenu = false;
        boolean exit = false;
        Scanner input = new Scanner(System.in);
        while(!exit){
            System.out.println("Welcome to the Carbon Footprint calculator.");
            System.out.println("Please select a menu option:");
            System.out.println("a: Calculate your PC's carbon emissions");
            System.out.println("b: Calculate your car's carbon emissions");
            System.out.println("c: Calculate your home's carbon emissions");
            if(completedMenu){
                System.out.println("d: Get recommendations to reduce your carbon footprint");
            }
            //temporary comment
            // String [][]converted = csvConversion();
            
            // System.out.println("enter column");
            // int b = input.nextInt();
            // System.out.println("enter row");
            // int a = input.nextInt();
            // System.out.println(converted[a][b]);
            // input.nextLine();

            String menu_option = input.nextLine();
            switch(menu_option){
                case "a":
                    int watts = findWattage();
                    break;
                case "b":
                    System.out.println("Option B");
                    carCarbon();
                    break;
                case "c":
                    System.out.println("Option C");
                    input.nextLine();
                    break;
                case "d":
                    System.out.println("Option D");
                    input.nextLine();
                    break;
                default:
                    System.out.println("goodbye");
                    exit = true;
                    break;
            }
        }
    }
}