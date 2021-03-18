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
        BufferedReader hadoopIndex = new BufferedReader(new FileReader("./index.hadoop/part-r-00000"));
        String line;
        while((line = hadoopIndex.readLine())!= null){
            nextLine = line.split("\\s+");
            for(int i = 1; i + 2 < nextLine.length; i+=3){
                if(word_to_index_count.containsKey(nextLine[0])){
                    if(id_to_index.containsKey(nextLine[i])){
                        word_to_index_count.get(nextLine[0]).add(new Pair<Integer, Integer>(id_to_index.get(nextLine[i]), Integer.parseInt(nextLine[i+2])));
                    }
                }
                else{
                    if(id_to_index.containsKey(nextLine[i])){
                        word_to_index_count.put(nextLine[0], new ArrayList<Pair<Integer, Integer>>());
                        word_to_index_count.get(nextLine[0]).add(new Pair<Integer, Integer>(id_to_index.get(nextLine[i]), Integer.parseInt(nextLine[i+2])));
                    }
                }

            }
        }
        hadoopIndex.close();
    }
    public String[][] query(String querystr){
        String[] words = querystr.split("\\s+");
        HashMap<Integer, Integer> tweetToCount = new HashMap<Integer, Integer>();
        ArrayList<Pair<Integer, Integer>> temp = new ArrayList<Pair<Integer, Integer>>();
        for(int i = 0; i < words.length; i++){
            if(word_to_index_count.containsKey(words[i])){
                temp = word_to_index_count.get(words[i]);
                for(int j = 0; j < temp.size(); j++){
                    if(tweetToCount.containsKey(temp.get(j).first)){
                        tweetToCount.put(temp.get(j).first, tweetToCount.get(temp.get(j).first) + temp.get(j).second );
                    }
                    else{
                        tweetToCount.put(temp.get(j).first, temp.get(j).second );
                    }
                }
            }
        }
        if(tweetToCount.size() == 0){
            return null;
        }
        String[][] output = new String[tweetToCount.size()][3];
        Integer i = 0;
        for(Integer index: tweetToCount.keySet()){
            output[i][0] = String.valueOf(i);
            output[i][1] = "@" + fullTweets.get(index)[1];
            output[i][2] = fullTweets.get(index)[3];
            i++;
        }
        return output;
    }
}
