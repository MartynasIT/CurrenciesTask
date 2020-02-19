import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Manipulation {
    public static void showData(InputStream inputDataBank, boolean period, String currency,
                                String dateStart, String dateEnd){
        try {
            assert inputDataBank != null;
            CSVReader csvReader = new CSVReader(new InputStreamReader(inputDataBank));
            List<String[]> allData = csvReader.readAll();

            //different data extraction methods for each mode
            if (!period){
                String text = Arrays.toString(allData.get(1)).replaceAll(";", " ")
                        .replaceAll("\"", "");

                String value = text.substring(text.indexOf(currency),
                        (text.length() -1) - dateStart.length()).replace(currency, "").trim();

                System.out.println(currency + " rate at " + dateStart + " was " + value);
            }

            else {
                String firstEntry = Arrays.toString(allData.get(allData.size()-1)).replaceAll(";"
                        , " ").replaceAll("\"", "");
                String firstValue = firstEntry.substring(firstEntry.indexOf(currency),
                        (firstEntry.length() -1) - dateStart.length()).replace(currency, "").trim();

                String lastEntry = Arrays.toString(allData.get(1)).replaceAll(";"
                        , " ").replaceAll("\"", "");
                String lastValue = lastEntry.substring(lastEntry.indexOf(currency),
                        (lastEntry.length() -1) - dateStart.length()).replace(currency, "").trim();

                float deviation = Float.parseFloat(firstValue.replace(',', '.')) -
                        Float.parseFloat(lastValue.replace(',', '.'));

                System.out.println("Deviation is: " + deviation);
                System.out.println(currency + " rate at " + dateStart + " was " + firstValue);
                System.out.println(currency + " rate at " + dateEnd + " was " + lastValue);
            }

        }

        catch (Exception e) {
            //if data extraction fails that means that website returned csv with html code since there is no data
            System.out.println("Website doesn't have data for this date");
        }

    }
}
