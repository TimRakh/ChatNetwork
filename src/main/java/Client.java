import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class Client {
    private String nickname;
    private String address;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputUser;

    public Client() throws IOException {
        Properties pro = new Properties();
        pro.load(new FileInputStream(new File("setting.txt")));
        int portOfServer = Integer.parseInt(pro.getProperty("portOfServer"));
        this.address = "localhost";
        try {
            this.socket = new Socket(address, portOfServer);
        } catch (IOException e) {
            System.err.println("Ошибка при создании socket=)");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            nickname = this.pressNickname();
            new ReadChat().start();
            new WriteChat().start();
        } catch (IOException e) {
            downSocket();
        }
    }

    public String pressNickname() {
        System.out.println("Введи своё имя");
        try {
            nickname = inputUser.readLine();
            out.write("Доброе " + nickname + "\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return nickname;
    }

    private void downSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class ReadChat extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine();
                    if (str.equals("exit")) {
                        downSocket();
                        break;
                    }
                    System.out.println(str);
                }

            } catch (IOException e) {
                downSocket();
            }
        }
    }

    private class WriteChat extends Thread {
        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    userWord = inputUser.readLine();
                    if (userWord.equals("exit")) {
                        downSocket();
                        break; // здесь изм
                    } else {
                        out.write(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ").format(Calendar.getInstance().getTime())
                                + nickname + " - " + userWord + "\n");

                    }
                    out.flush();
                } catch (IOException e) {
                    downSocket();
                }
            }
        }
    }
}
