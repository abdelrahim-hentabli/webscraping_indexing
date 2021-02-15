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

import java.io.FileReader;
import com.opencsv.CSVReader;
import java.util.Arrays;
import java.util.Scanner;


public class LuceneTest {
    public static void main(String[] args) throws IOException, ParseException {
        //prep for csv reader
        CSVReader csvReader = new CSVReader(new FileReader("./output.csv"));
        String [] nextLine;

        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        
        while ((nextLine = csvReader.readNext()) != null){

            Document doc = new Document(); 
            doc.add(new TextField("id", nextLine[1], Field.Store.YES));
            doc.add(new TextField("tweet", nextLine[3], Field.Store.YES));
            //Hashtag inputting (Work in progress)
            if(!nextLine[6].equals("[]")){
                String[] hashtags = nextLine[6].split(",");
                for(int i = 0; i < hashtags.length; i++){
                    doc.add(new TextField("hashtag", hashtags[i].substring(2,hashtags[i].length()-2),Field.Store.YES));
                }
            }
            String fullSearchableText = nextLine[1] + " " + nextLine[3] + " " + nextLine[6];
            doc.add(new TextField("content", fullSearchableText, Field.Store.NO));
                
            w.addDocument(doc);

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

    
        //loop query
        while(true){
            Scanner obj = new Scanner(System.in); //create scanner obj

            System.out.println("Enter Query: (\"quit\" to exit)");
            String querystr = obj.nextLine(); //read user input
        
            if(querystr.equals("quit")){
                break;
            }

            // 2. query
            // String querystr = args.length > 0 ? args[0] : "Biden";

            // the "title" arg specifies the default field to use
            // when no field is explicitly specified in the query.
            // Query q = new QueryParser("title", analyzer).parse(querystr);
            
            // we can add a if condition to check for specific fields in the query based on the user input
            // for ex. if user inputs #loser, then QueryParser("hashtag") etc..
            Query q = new QueryParser("content", analyzer).parse(querystr);

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

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
}