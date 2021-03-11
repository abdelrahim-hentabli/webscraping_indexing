import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.FSDirectory;


import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;

import com.opencsv.CSVReader;
import java.util.Arrays;
import java.util.Scanner;


public class LuceneIndex {
    public static void main(String[] args) throws IOException {
        //prep for csv reader
        CSVReader csvReader = new CSVReader(new FileReader("./tweets_new.csv"));
        String [] nextLine;

        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        FSDirectory index = FSDirectory.open(Paths.get("./index.lucene"));
        // Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        FileWriter timingFile = new FileWriter("timing.csv");
        
        long startTime = System.nanoTime();
        long documentCount = 0;
        long endTime; 
        while ((nextLine = csvReader.readNext()) != null){

            Document doc = new Document(); 
            doc.add(new TextField("id", nextLine[1], Field.Store.YES));
            doc.add(new TextField("tweet", nextLine[3], Field.Store.YES));
            //Hashtag inputting (Work in progress)
            if(!nextLine[6].equals("[]")){
                String temp = nextLine[6].substring(1,nextLine[6].length()-1);
                String[] hashtags = temp.split(",");
                String tempHash = "";
                for(int i = 0; i < hashtags.length; i++){
                    tempHash += hashtags[i].substring(i==0?1:2,hashtags[i].length()-1) + " ";
                }
                doc.add(new TextField("hashtag", tempHash,Field.Store.YES));
            }
            String fullSearchableText = nextLine[1] + " " + nextLine[3] + " " + nextLine[6];
            doc.add(new TextField("content", fullSearchableText, Field.Store.NO));
            
            w.addDocument(doc);
            documentCount++;
            endTime = System.nanoTime();
            timingFile.write(Long.toString(documentCount) + "," + Long.toString((endTime-startTime)/ 1000000) + "\n");
            // String [] values = Arrays.asList(nextLine);
            // addDoc(w, nextLine[3], nextLine[0]);
            
            
            //Hashtag inputting (Work in progress)
            // if(!nextLine[6].equals("[]")){
            //     String[] hashtags = nextLine[6].split(",");
            //     for(int i = 0; i < hashtags.length; i++){
            //         addDoc(w, hashtags[i].substring(2,hashtags[i].length()-2), nextLine[0]);
            //     }
            // }
            // else{
            //     addDoc(w, nextLine[3], nextLine[0]);
            // }
        }

        // 1. create the index
        w.close();
        timingFile.close();
    }
}
