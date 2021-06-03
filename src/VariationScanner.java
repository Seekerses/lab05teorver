import java.io.*;
import java.util.ArrayList;

public class VariationScanner {

    public static ArrayList<Double> readVariationFromFile(String pathToFile){

        ArrayList<Double> variation = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathToFile)))){
            String line;
            String[] numbers;
            while((line = reader.readLine()) != null){
                numbers = line.split(" ");
                for(String number : numbers){
                    variation.add(Double.valueOf(number));
                }
            }
        }
        catch (FileNotFoundException ex){
            System.out.println("File not found.");
            Main.main(null);
        }
        catch (NumberFormatException ex){
            System.out.println("There are have to be only float numbers in file.");
            Main.main(null);
        }
        catch (IOException ex){
            System.out.println("Some IOException occurred.");
            Main.main(null);
        }
        return variation;
    }
}
