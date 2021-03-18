import java.io.IOException;

import java.util.Scanner;


public class HadoopQueryTest {
    public static void main(String[] args) throws IOException {
        HadoopQuery theQuery = new HadoopQuery("./index.hadoop/part-r-00000");
        String[][] output;
        Scanner obj = new Scanner(System.in);
        while (true){
            System.out.println("Enter Query: (\"quit\" to exit)");
            String querystr = obj.nextLine();
            output = theQuery.query(querystr);
            if(output != null){
                for(int i = 0; i < output.length; i++){
                    System.out.println(output[i][0] + ", " + output[i][1] + ", " + output[i][2]);
                }
            }
            else{
                System.out.println("No Tweet Found for Query.");
            }
        }
    }
}
