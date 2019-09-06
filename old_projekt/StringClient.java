import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

class StringClient {

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("192.168.137.1", 10000);

            OutputStream raus = socket.getOutputStream();
            PrintStream ps = new PrintStream(raus, true);
            ps.println("Hallo Welt!");
            ps.println("Hallo Otto!");

            InputStream rein = socket.getInputStream();
            System.out.println("verf\u00FCgbare Bytes: " + rein.available());
            BufferedReader buff = new BufferedReader(new InputStreamReader(rein));
            
            String line_ = buff.readLine();
            while(line_ != null){
                System.out.println(line_);
                line_ = buff.readLine();
            }

//            int i = 1;
//            while (i++ < 3) {
//                System.out.println(buff.readLine());
//            }

        } catch (UnknownHostException e) {
            System.out.println("Unknown Host...");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOProbleme...");
            e.printStackTrace();
        } finally {
            if (socket != null)
                try {
                    socket.close();
                    System.out.println("Socket geschlossen...");
                } catch (IOException e) {
                    System.out.println("Socket nicht zu schliessen...");
                    e.printStackTrace();
                }
        }
    }
}