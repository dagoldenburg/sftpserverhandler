public class Main {

    public static void main(String[] args ){
        (new Thread(new Filehandler())).start();
        (new Thread(new TcpConnectionHandler())).start();
    }

}
