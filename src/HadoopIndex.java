import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;


import java.io.FileWriter;
import java.nio.file.Paths;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.CSVNLineInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CSVLineRecordReader;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;



public class HadoopIndex {
    private static final HashSet<String> stopWords;
    static {
        stopWords = new HashSet<>();
        stopWords.add("i");
        stopWords.add("me");
        stopWords.add("my");
        stopWords.add("myself");
        stopWords.add("we");
        stopWords.add("our");
        stopWords.add("ours");
        stopWords.add("ourselves");
        stopWords.add("you");
        stopWords.add("your");
        stopWords.add("yours");
        stopWords.add("yourself");
        stopWords.add("yourselves");
        stopWords.add("he");
        stopWords.add("him");
        stopWords.add("his");
        stopWords.add("himself");
        stopWords.add("she");
        stopWords.add("her");
        stopWords.add("hers");
        stopWords.add("herself");
        stopWords.add("it");
        stopWords.add("its");
        stopWords.add("itself");
        stopWords.add("they");
        stopWords.add("them");
        stopWords.add("their");
        stopWords.add("theirs");
        stopWords.add("themselves");
        stopWords.add("what");
        stopWords.add("which");
        stopWords.add("who");
        stopWords.add("whom");
        stopWords.add("this");
        stopWords.add("that");
        stopWords.add("these");
        stopWords.add("those");
        stopWords.add("am");
        stopWords.add("is");
        stopWords.add("are");
        stopWords.add("was");
        stopWords.add("were");
        stopWords.add("be");
        stopWords.add("been");
        stopWords.add("being");
        stopWords.add("have");
        stopWords.add("has");
        stopWords.add("had");
        stopWords.add("having");
        stopWords.add("do");
        stopWords.add("does");
        stopWords.add("did");
        stopWords.add("doing");
        stopWords.add("a");
        stopWords.add("an");
        stopWords.add("the");
        stopWords.add("and");
        stopWords.add("but");
        stopWords.add("if");
        stopWords.add("or");
        stopWords.add("because");
        stopWords.add("as");
        stopWords.add("until");
        stopWords.add("while");
        stopWords.add("of");
        stopWords.add("at");
        stopWords.add("by");
        stopWords.add("for");
        stopWords.add("with");
        stopWords.add("about");
        stopWords.add("against");
        stopWords.add("between");
        stopWords.add("into");
        stopWords.add("through");
        stopWords.add("during");
        stopWords.add("before");
        stopWords.add("after");
        stopWords.add("above");
        stopWords.add("below");
        stopWords.add("to");
        stopWords.add("from");
        stopWords.add("up");
        stopWords.add("down");
        stopWords.add("in");
        stopWords.add("out");
        stopWords.add("on");
        stopWords.add("off");
        stopWords.add("over");
        stopWords.add("under");
        stopWords.add("again");
        stopWords.add("further");
        stopWords.add("then");
        stopWords.add("once");
        stopWords.add("here");
        stopWords.add("there");
        stopWords.add("when");
        stopWords.add("where");
        stopWords.add("why");
        stopWords.add("how");
        stopWords.add("all");
        stopWords.add("any");
        stopWords.add("both");
        stopWords.add("each");
        stopWords.add("few");
        stopWords.add("more");
        stopWords.add("most");
        stopWords.add("other");
        stopWords.add("some");
        stopWords.add("such");
        stopWords.add("no");
        stopWords.add("nor");
        stopWords.add("not");
        stopWords.add("only");
        stopWords.add("own");
        stopWords.add("same");
        stopWords.add("so");
        stopWords.add("than");
        stopWords.add("too");
        stopWords.add("very");
        stopWords.add("s");
        stopWords.add("t");
        stopWords.add("can");
        stopWords.add("will");
        stopWords.add("just");
        stopWords.add("don");
        stopWords.add("should");
        stopWords.add("now");
    }    
    public static class TokenizerMapper extends Mapper<Object, List<Text>, Text, Text> {
        private final Text word = new Text();
        private final Text id = new Text();
        public void map(Object key, List<Text> values, Context context) throws IOException, InterruptedException {
            if(values.size() > 3 && values.get(0).toString().length() == 19){
                String[] itr = values.get(3).toString().split("[^a-zA-Z]",0);
                id.set(values.get(0));
                String lowerWord;
                for(int i = 0; i < itr.length; i++){
                    lowerWord = itr[i].toLowerCase();
                    if(!itr[i].isEmpty() && !stopWords.contains(lowerWord)){
                        word.set(lowerWord);
                        context.write(word, id);
                    }
                }
            }
        }
    }
    public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            HashMap<String,Integer> tweet_idToCount = new HashMap<String,Integer>();
            for (Text val : values) {
                if(tweet_idToCount.containsKey(val.toString())){
                    tweet_idToCount.put(val.toString(), tweet_idToCount.get(val.toString())+1);
                }
                else{
                    tweet_idToCount.put(val.toString(), 1);
                }
            }
            String invertedIndex = "";
            for(String tweet_id: tweet_idToCount.keySet()){
                invertedIndex += tweet_id + " : " + tweet_idToCount.get(tweet_id) + "\t"; 
            }
            context.write(key, new Text(invertedIndex));
        }
    }
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(CSVLineRecordReader.IS_ZIPFILE, false);
        conf.setInt(CSVNLineInputFormat.LINES_PER_MAP, 40000);
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(HadoopIndex.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        //job.setReducerClass(IntSumReducer.class);
        job.setInputFormatClass(CSVNLineInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        CSVNLineInputFormat.addInputPath(job, new Path("tweets_new.csv"));
        FileOutputFormat.setOutputPath(job, new Path("index.hadoop"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
