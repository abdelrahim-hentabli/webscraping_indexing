import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class HadoopQuery {
    ArrayList<String[]> fullTweets;
    HashMap<String, Integer> id_to_index; 
    CSVReader csvReader;
    String pathToIndex;
    HashMap<String, Pair<Integer, Integer>> word_to_index_count;
    public HadoopQuery(String path) throws IOException{
        fullTweets = new ArrayList<String[]>();
        id_to_index = new HashMap<String, Integer>();
        csvReader = new CSVReader(new FileReader("./tweets_new.csv"));
        word_to_index_count = new HashMap<String, Pair<Integer, Integer>>();
        String [] nextLine;
        int index = 0;
        while((nextLine = csvReader.readNext()) != null){
            id_to_index.put(nextLine[0], index);
            fullTweets.add(nextLine);
            index++;
        }
        
    }
}
