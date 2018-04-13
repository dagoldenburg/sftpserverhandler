import DB.DbI;
import DB.Model.Transaction;
import DB.PostGreSQLDb;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Filehandler implements Runnable{

    @Override
    public void run() {
        DbI dbRef = new PostGreSQLDb();
        dbRef.createConnection();
        while(true){
            try {
                Thread.sleep(1000);
                File testFile = new File("/Users/do/Documents/REQUESTDOCUMENTS/");
                File[] files = testFile.listFiles();
                String line;
                for (File f : files) {
                    File path = f.getAbsoluteFile();
                    FileReader fr = new FileReader(path);
                    BufferedReader br = new BufferedReader(fr);
                    while ((line = br.readLine()) != null){
                        String[] strings = line.split(" ");
                        dbRef.makeTransaction(strings[0],strings[1],Double.parseDouble(strings[2]));
                    }
                    path.delete();
                }
            } catch (NullPointerException e){
                System.out.println("No file");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
