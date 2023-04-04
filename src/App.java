import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.*;
import java.util.*;

public class App {
    public static void clearScreen(){
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
    public static void wattage(){
        Scanner input = new Scanner(System.in);
        WebDriver driver = new ChromeDriver();
        clearScreen();
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
        if(wattageNum>171){
            System.out.println("Your PC uses above average wattage. Some ways you can save energy:");
        }else{
            System.out.println("Good job, your PC consumes less energy than average!");
        }
        driver.quit();
        input.close();
    }
    public static String [][] csvConversion() throws FileNotFoundException {
        Scanner readFile = new Scanner(new File("/Users/williamwu/ICS4U/ICSEnvironmentProject/src/vehicles.csv"));
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
        return csvFile;
    }
    public static void main(String[] args) throws FileNotFoundException{
        boolean exit = false;
        Scanner input = new Scanner(System.in);
        while(!exit){
            clearScreen();
            System.out.println("Welcome to the Carbon Footprint calculator.");
            System.out.println("Please select a menu option:");
            System.out.println("a: Calculate your PC's carbon emissions");
            System.out.println("b: Calculate your car's carbon emissions");
            System.out.println("c: Calculate your home's carbon emissions");
            String [][]converted = csvConversion();
            System.out.println("enter row");
            int a = input.nextInt();
            System.out.println("enter column");
            int b = input.nextInt();
            System.out.println(converted[a][b]);
            input.nextLine();

            String menu_option = input.nextLine();
            switch(menu_option){
                case "a":
                    System.out.println("Option A");
                    input.nextLine();
                    break;
                case "b":
                    System.out.println("Option B");
                    input.nextLine();
                    break;
                case "c":
                    System.out.println("Option C");
                    input.nextLine();
                    break;
                default:
                    System.out.println("goodbye");
                    exit = true;
                    break;
            }
        }
        input.close();
    }
}