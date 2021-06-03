import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("Enter the path to file:");

        Scanner scanner = new Scanner(System.in);

        String pathToFile = scanner.next();

        ArrayList<Double> variation = VariationScanner.readVariationFromFile(pathToFile);

        DataAnalyzer dataAnalyzer = new DataAnalyzer(variation);

        dataAnalyzer.doAnalyze(variation);
    }
}
