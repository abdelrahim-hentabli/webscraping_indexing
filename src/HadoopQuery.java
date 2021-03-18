import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class HadoopQuery {
    private ArrayList<String[]> fullTweets;
    private HashMap<String, Integer> id_to_index;
    private String pathToIndex;
    private HashMap<String, ArrayList<Pair<Integer, Integer>>> word_to_index_count;
    public HadoopQuery(String path) throws IOException{
        pathToIndex = path;
        fullTweets = new ArrayList<String[]>();
        id_to_index = new HashMap<String, Integer>();
        CSVReader csvReader = new CSVReader(new FileReader("./tweets_new.csv"));
        word_to_index_count = new HashMap<String, ArrayList<Pair<Integer, Integer>>>();
        String [] nextLine;
        int index = 0;
        while((nextLine = csvReader.readNext()) != null){
            id_to_index.put(nextLine[0], index);
            fullTweets.add(nextLine);
            index++;
        }
        BufferedReader hadoopIndex = new BufferedReader(new FileReader(path));
        String line;
        while((line = hadoopIndex.readLine())!= null){
            nextLine = line.split("\\s+");
            for(int i = 1; i + 2 < nextLine.length; i+=3){
                if(word_to_index_count.containsKey(nextLine[0])){
                    if(id_to_index.containsKey(nextLine[i])){
                        word_to_index_count.get(nextLine[0]).add(new Pair<Integer, Integer>(id_to_index.get(nextLine[i]), Integer.parseInt(nextLine[i+2])));
                    }
                }

            }
        }
    }
    public void query(String querystr){

    }
}
