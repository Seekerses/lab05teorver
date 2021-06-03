import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataAnalyzer {

    private ArrayList<Double> variation;

    public DataAnalyzer(ArrayList<Double> variation) {
        this.variation = variation;
    }

    public void doAnalyze(ArrayList<Double> variation){
        System.out.println("Variation range:");
        sortArray(variation);
        System.out.println(variation);
        System.out.println("Variation extremes:");
        System.out.println("Min = " + min(variation) + " ; Max = " + max(variation));
        System.out.println("Variation range = " + range(variation));
        System.out.println("Variation expectation = " + expectation(variation));
        System.out.println("Variation standard deviation = " + Math.sqrt(variationDispersion(variation)));
        System.out.println("Variation empirical function:");
        ArrayList<Double> borders = intervalBorders(variation);
        ArrayList<Double> empiricalFunc = empiricalFunction(variation, borders);
        System.out.println(empiricalFunc.get(0) + ", x < " + borders.get(0));
        for(int i = 1; i < empiricalFunc.size(); i++) {
            System.out.println(empiricalFunc.get(i) + ", " + borders.get(i - 1) + " <= x < " + borders.get(i));
        }
        System.out.println("1.00, x > " + max(variation));
        showEmpiricalFuncGraph(variation, borders);
        showFrequencyPolygon(variation, borders);
        showFrequencyHistogram(variation, borders);
    }

    public void showEmpiricalFuncGraph(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Double> empirical = empiricalFunction(variation, borders).stream()
                .flatMap((p) -> Stream.of(p ,p))
                .collect(Collectors.toCollection(ArrayList::new));
        empirical.remove(0);
        empirical.add(1.00D);
        empirical.add(1.00D);

        borders = borders.stream()
                .flatMap((p) -> Stream.of(p, p))
                .collect(Collectors.toCollection(ArrayList::new));
        borders.add(borders.get(borders.size() -1) + 1);
        XYChart chart = QuickChart.getChart("Empirical function chart", "X", "F*(x)", "F*(x)", borders, empirical);
        new SwingWrapper(chart).displayChart();
    }

    public void showFrequencyPolygon(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Double> middles = intervalMiddles(borders);
        ArrayList<Double> frequencies = relativeFrequency(variation, borders);
        XYChart chart = QuickChart.getChart("Polygon frequency chart", "X", "p*(i)", "p*(i)", middles, frequencies);
        new SwingWrapper(chart).displayChart();
    }

    public void showFrequencyHistogram(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Double> heights = histogramHeights(variation, borders).stream()
                .flatMap((p) -> Stream.of(p, p, 0D))
                .collect(Collectors.toCollection(ArrayList::new));
        heights.add(0,0D);
        borders = borders.stream()
                .flatMap((p) -> Stream.of(p, p, p))
                .collect(Collectors.toCollection(ArrayList::new));
        borders.remove(0);
        borders.remove(borders.size() - 1);
        XYChart chart = QuickChart.getChart("Frequency Histogram", "X", "p*(i)/h", "f*(x)", borders, heights);
        new SwingWrapper(chart).displayChart();
    }

    public void sortArray(ArrayList<Double> variation){
        variation.sort(Comparator.comparing((v1) -> v1));
    }

    public Double min(ArrayList<Double> variation){
        Optional<Double> optionalMin = variation.stream().min(Double::compare);
        return optionalMin.orElse(null);
    }

    public Double max(ArrayList<Double> variation){
        Optional<Double> optionalMin = variation.stream().max(Double::compare);
        return optionalMin.orElse(null);
    }

    public Double range(ArrayList<Double> variation){
        return max(variation) - min(variation);
    }

    public Integer intervalsCount(ArrayList<Double> variation){
        return (int)((1 + Math.log10(variation.size()) / Math.log10(2D)));
    }

    public Double expectation(ArrayList<Double> variation){
        return variation.stream().reduce(0D, Double::sum) / variation.size();
    }

    //TODO test
    public Double variationDispersion(ArrayList<Double> variation){
        Double expectation = expectation(variation);
        return variation.stream().reduce(0D, (d1, d2) -> d1 + Math.pow(d2 - expectation, 2)) / variation.size();
    }

    public ArrayList<Double> intervalBorders(ArrayList<Double> variation){
        ArrayList<Double> borders = new ArrayList<>();
        Integer intervalsCount = intervalsCount(variation);
        Double intervalWidth = range(variation) / intervalsCount;
        borders.add(min(variation));
        for (int i = 0; i < intervalsCount - 1; i++){
            borders.add(borders.get(borders.size() - 1) + intervalWidth);
        }
        borders.add(max(variation));
        return borders;
    }

    public ArrayList<Double> intervalMiddles(ArrayList<Double> borders){
        ArrayList<Double> middles = new ArrayList<>();
        for(int i = 0; i < borders.size() -1; i++){
            middles.add((borders.get(i) + borders.get(i+1) - borders.get(i)) / 2);
        }
        return middles;
    }

    public ArrayList<Long> accurateCumulativeFrequency(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Long> frequencies = new ArrayList<>();
        for(int i = 0; i < borders.size() - 1; i++){
            int finalI = i;
            frequencies.add( variation.stream().filter((v) -> v >= borders.get(finalI) && v < borders.get(finalI + 1)).count() );
        }
        return frequencies;
    }

    public ArrayList<Long> cumulativeFrequency(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Long> frequencies = new ArrayList<>();
        for(int i = 0; i < borders.size() - 2; i++){
            int finalI = i;
            frequencies.add( variation.stream().filter((v) -> v >= borders.get(finalI) && v < borders.get(finalI + 1)).count() );
        }
        frequencies.add( variation.stream().filter((v) -> v >= borders.get(borders.size() -2) && v <= borders.get(borders.size() -1)).count() );
        return frequencies;
    }

    public ArrayList<Double> accurateRelativeFrequency(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Long> cumulative = accurateCumulativeFrequency(variation, borders);
        ArrayList<Double> relative = cumulative.stream().map(Long::doubleValue).collect(Collectors.toCollection(ArrayList::new));
        relative = relative.stream().map((v) -> v / variation.size()).collect(Collectors.toCollection(ArrayList::new));
        return relative;
    }

    public ArrayList<Double> relativeFrequency(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Long> cumulative = cumulativeFrequency(variation, borders);
        ArrayList<Double> relative = cumulative.stream().map(Long::doubleValue).collect(Collectors.toCollection(ArrayList::new));
        relative = relative.stream().map((v) -> v / variation.size()).collect(Collectors.toCollection(ArrayList::new));
        return relative;
    }

    public ArrayList<Double> empiricalFunction(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Double> relative = accurateRelativeFrequency(variation, borders);
        values.add(0D);
        for(int i = 0; i < relative.size(); i++){
            values.add( values.get(i) + relative.get(i) );
        }
        return values;
    }

    public ArrayList<Double> histogramHeights(ArrayList<Double> variation, ArrayList<Double> borders){
        ArrayList<Double> relative = relativeFrequency(variation, borders);
        Double intervalWidth = range(variation) / intervalsCount(variation);
        return relative.stream().map((v) -> v / intervalWidth).collect(Collectors.toCollection(ArrayList::new));
    }
}
