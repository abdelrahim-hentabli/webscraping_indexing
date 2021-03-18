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

public class LuceneQuery {
    private  FSDirectory index;
    StandardAnalyzer analyzer;
    public LuceneQuery() throws IOException{
        index = FSDirectory.open(Paths.get("./index.lucene"));
        StandardAnalyzer analyzer = new StandardAnalyzer();
    }

    public void query(String querystr) throws IOException, ParseException{
        Scanner obj = new Scanner(System.in); //create scanner obj
        Query q = new QueryParser("content", analyzer).parse(querystr);
        int hitsPerPage = 100;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
    }
    public static void main(String[] args) throws IOException, ParseException{
        
        FSDirectory index = FSDirectory.open(Paths.get("./index.lucene"));
        StandardAnalyzer analyzer = new StandardAnalyzer();

        while(true){
            Scanner obj = new Scanner(System.in); //create scanner obj
    
            System.out.println("Enter Query: (\"quit\" to exit)");
            String querystr = obj.nextLine(); //read user input
        
            if(querystr.equals("quit")){
                break;
            }

            // the "title" arg specifies the default field to use
            // when no field is explicitly specified in the query.
            Query q = new QueryParser("content", analyzer).parse(querystr);
            
    
            // we can add a if condition to check for specific fields in the query based on the user input
            // for ex. if user inputs #loser, then QueryParser("hashtag") etc..
    
            // 3. search
            int hitsPerPage = 100;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
    
            // 4. display results
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                if(d.get("hashtag") == null){
                    System.out.println((i + 1) + ". "+ "(score: " + hits[i].score + ") " + "@" + d.get("id") + " - " + d.get("tweet"));
                }
                else{
                    System.out.println((i + 1) + ". "+ "(score: " + hits[i].score + ") " + "@" + d.get("id") + " - " + d.get("tweet") + " | " + d.get("hashtag"));
                }
            }
            // reader can only be closed when there
            // is no need to access the documents any more.
            reader.close();
        }
    }
    
}
