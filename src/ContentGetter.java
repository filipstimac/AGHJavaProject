import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Filip Štimac on 3.1.2018..
 */
public class ContentGetter {

    static Logger log = Logger.getLogger(ContentGetter.class.getName());
    private int counter = 0;

    public String getContent(String searchTerm) {
        String formattedTerm = String.join("+", searchTerm.split(" "));
        String tableContent = "";
        boolean result = false;
        try {
            log.info("Getting content of Wikipedia results for term \"" + searchTerm + "\".");
            URL url = new URL("https://en.wikipedia.org/w/index.php?search=" + formattedTerm + "&title=Special%3ASearch&go=Go");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String imageRegex = ".*<img .* src=\"([^\"]*)\".*";

            log.info("Connection successful, reading HTML content...");

            String input;
            boolean table = false;
            while((input = br.readLine()) != null) {
                //detect beginning of table
                if(Pattern.matches(".*<table class=\"infobox.*", input)) {
                    log.info("Beginning of HTML table detected.");
                    table = true;
                }
                //detect images, download them and replace the URL
                else if(Pattern.matches(imageRegex, input) && table) {
                    log.info("Image detected, downloading...");
                    Matcher matcher = Pattern.compile(imageRegex).matcher(input);
                    if(matcher.find()) {
                        String modifiedInput = "";
                        String detectedURL = matcher.group(1);
                        String imageURL = "https:" + detectedURL;
                        String imageFile =  formattedTerm + counter + imageURL.substring(imageURL.length() - 4);
                        try (InputStream in = new URL(imageURL).openStream()) {
                            Files.copy(in, Paths.get(imageFile), StandardCopyOption.REPLACE_EXISTING);
                            modifiedInput = input.replaceAll(detectedURL, "file:" + imageFile);
                            log.info("Image \"" + imageFile + "\" download successful.");
                        } catch (IOException e) {
                            log.error("Image download unsuccessful");
                        }
                        counter++;
                        System.out.println(modifiedInput);
                        tableContent += modifiedInput + "\n";
                    }
                }
                //detect end of the table
                else if(table == true && Pattern.matches("</table>", input)) {
                    log.info("End of HTML table detected.");
                    tableContent += input;
                    result = true;
                    break;
                }
                if(table && !Pattern.matches(imageRegex, input)) tableContent += input + "\n";
            }
        } catch (MalformedURLException e) {
            log.error("Invalid URL format.");
        } catch (IOException e) {
            log.error("I/O Exception.");
        }
        if(result) {
            tableContent = "<center>" + tableContent + "</center>";
            log.info("Getting the content finished successful.");
            return tableContent;
        }
        else {
            log.info("Unable to get result, getting content unsuccessful.");
            return "<html><center><h1>Unable to find search term</h1><p>Check spelling or try different term.</p></center></html>";
        }
    }
}