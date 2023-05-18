import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Server extends Thread{
    public static List<Server> listOfServer = new LinkedList<>();
    private Socket socket;
    public BufferedReader in;
    private BufferedWriter out;
    public Server (Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(new File("setting.txt")));
        int SERVER_PORT = Integer.parseInt(props.getProperty("SERVER_PORT"));

        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            writeFileServer("Сервер запущен...");
            System.out.println("Сервер запущен...");
            while (true) {
                Socket socket = server.accept();
                try {
                    listOfServer.add(new Server(socket));
                    writeFileServer("Новое подключение к серверу!");
                    System.out.println("Новое подключение к серверу!");
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }

    @Override
    public void run() {
        String message;
        try {
            message = in.readLine();
            out.write(message + "\n");
            out.flush();
            while (true) {
                message = in.readLine();
                try {
                    if (message.equals("/exit")) {
                        this.downSocket();
                        break;
                    }
                }
                catch (NullPointerException ignored) {
                }
                writeFileServer(message);
                System.out.println(message);
                for (Server vr : listOfServer) {
                    vr.sendMsg(message);
                }
            }
        } catch (IOException e) {
            this.downSocket();
        }
    }

    private void sendMsg(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }

    protected void downSocket() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (Server vr : listOfServer) {
                    if(vr.equals(this)){
                        vr.interrupt();
                    }
                    listOfServer.remove(this);
                }
                writeFileServer("Сокет выключился!");
                System.out.println("Сокет выключился!");
            }
        } catch (IOException ignored) {}
    }

    protected static void writeFileServer(String msg) {
        try (FileWriter writer = new FileWriter("file.log", true)) {
            writer.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime()))
                    .append(" ")
                    .append(msg)
                    .append('\n')
                    .flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
