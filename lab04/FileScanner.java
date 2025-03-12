package lab04;
import java.io.*;
import java.util.*;

public class FileScanner{
    public static void main(String[] args){
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter the path to the scenario file: ");
        String filePath = inputScanner.nextLine();
        File scenarioFile = new File(filePath);

        try {
            Scanner fileScanner = new Scanner(scenarioFile);
            while (fileScanner.hasNextLine()){
                @SuppressWarnings("unused")
                String line = fileScanner.nextLine();
            }
            fileScanner.close();
            
        } catch (FileNotFoundException e){}

        inputScanner.close();
    }
}