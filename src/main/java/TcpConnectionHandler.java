import DB.DbI;
import DB.Model.Transaction;
import DB.PostGreSQLDb;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TcpConnectionHandler implements Runnable {



    public void run() {
        Socket connectionSocket = null;
        try {
            String clientSentence;
            ServerSocket welcomeSocket = new ServerSocket(2222);
            while (true) {
                connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                clientSentence = inFromClient.readLine();
                System.out.println("Received: " + clientSentence);
                String[] strings = clientSentence.split(" ");
                String filename;
                if(strings[0].equals("CF")){
                    filename = createNewResponseFile(strings[1],strings[2]);
                    if(!filename.equals(null)) {
                        outToClient.writeBytes("DONE "+filename+"\n");
                    }else{
                        outToClient.writeBytes("NIET\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                connectionSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String createNewResponseFile(String username,String nrOfTransactions){
        DbI dbRef = new PostGreSQLDb();
        dbRef.createConnection();
        String filename = null;
        try {
            StringBuilder sb = new StringBuilder();
            for (Transaction t : dbRef.retrieveNrOfTransactions(username, Integer.parseInt(nrOfTransactions))) {
                sb.append(t.getTo() + " " + t.getFrom() + " " + t.getAmount());
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes("UTF-8"));

            byte[] encodedBytes = Base64.getEncoder().encode(hash);
            filename = new String(encodedBytes);
            filename = filename.replace("/", "!");
            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/do/Documents/RESPONSEDOCUMENTS/" + filename));
            writer.write(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }


}
