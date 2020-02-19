import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Currency;

public class CurrencyDownloader {
    public static String FILE_NAME = "currencies ";

    public static void main(String[] args) {
        System.out.print("Use date or period?: ");
        Scanner input = new Scanner(System.in);
        String mode = input.nextLine();

        if (mode.equals("date")) {
            System.out.print("Enter date: ");
            Scanner inputDate = new Scanner(System.in);
            String date = inputDate.nextLine();
            // input validation is done
            if (isDateValid(date)){
                System.out.print("Enter currency code: ");
                String currency = inputDate.nextLine();
                if (!testCurrencyCode(currency)){
                    System.out.println("Incorrect code");
                    System.exit(0);
                }
                // if no errors csv is downloaded and stored in memory for further use
                InputStream csvStream = downloadCurrencies(currency, date, date, false);
                if (csvStream != null)
                    // pass saved csv to get data out of it
                    Manipulation.showData(csvStream, false, currency, date, date);
                else
                    System.out.println("Data retrieval failed!");
            }
            else {
               warnUser();
            }
        }

        else if (mode.equals("period")) {
            System.out.print("Enter start date: ");
            Scanner inputPeriod = new Scanner(System.in);
            String dateStart = inputPeriod.nextLine();

            if (isDateValid(dateStart)){
                System.out.print("Enter end date: ");
                String dateEnd = inputPeriod.nextLine();

                if (isDateValid(dateEnd)) {
                    System.out.print("Enter currency code: ");
                    String currency = inputPeriod.nextLine();
                    if (!testCurrencyCode(currency)){
                        System.out.println("Incorrect code");
                        System.exit(0);
                    }
                    InputStream csvStream = downloadCurrencies(currency, dateStart, dateEnd, true);
                    if (csvStream != null)
                        Manipulation.showData(csvStream, true, currency, dateStart, dateEnd);
                    else
                        System.out.println("Data retrieval failed!");
                }
                else {
                    warnUser();
                }
            }
            else {
                warnUser();
            }
        }
        else {
            System.out.println("Please choose a correct option");
            main(null);
        }

    }

    public static InputStream downloadCurrencies(String currency, String dateStart, String dateEnd, boolean period) {
        //URL is used to get csv with according values
        String url = "https://www.lb.lt/lt/currency/exportlist/?csv=1&currency=" + currency +
                "&ff=1&class=Eu&type=day&date_from_day=" + dateStart + "&date_to_day=" + dateEnd;
        InputStream inputDataBank = null;
        try {
            inputDataBank = new URL(url).openStream();
        } catch (IOException e) {
            System.out.println("There is problem getting data");
            System.exit(0);
        }

        String nameFileToSave = null;
        //csvs are named accordingly to the mode, date and currency
        if (dateStart.equals(dateEnd))
            nameFileToSave = FILE_NAME + dateStart + " " + currency + ".csv";
        else
            nameFileToSave = FILE_NAME + dateStart + "-" + dateEnd + " " + currency + ".csv";

        checkFIleExists(nameFileToSave); // lets check if file already exists so old data is deleted

        //file is saved in project's directory
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(nameFileToSave)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("Failed to download data");
            System.exit(0);
        }

        return inputDataBank;
    }

    public static void checkFIleExists(String fileName) {
        File file = new File(fileName);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            System.out.println("Problem with deleting old data");
        }
    }

    public static boolean isDateValid(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static void warnUser() {
        System.out.println("Invalid date was passed");
        main(null);
    }

    public static boolean testCurrencyCode(String currency) {
        try {
            Currency curr = Currency.getInstance(currency);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
