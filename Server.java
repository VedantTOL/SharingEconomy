import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242);

        while (true) {

            Socket socket = null;

            try {
                socket = serverSocket.accept();
                System.out.println("Client connected");

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                System.out.println("Now assigning a new thread to the client");

                Thread t = new ClientHandler(dis, dos, socket);

                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket socket;



    ClientHandler(DataInputStream dis, DataOutputStream dos, Socket socket) {
        this.dis = dis;
        this.dos = dos;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println(dis.readUTF());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


}