import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class ChatTest {
    public class AppTest {

        @Test
        public void testWriteMsg() throws IOException {
            String msg = "Test message";
            File file = new File("D://file.log");
            long beforeLength = file.length();
            Server.writeFileServer(msg);
            long afterLength = file.length();
            boolean afterLengthOverBefore = afterLength > beforeLength;
            String fileContent = readLogFile(file.getAbsolutePath());
            boolean haveTestMsg = fileContent.substring(fileContent.length() - msg.length()).contains(msg);
            Assertions.assertTrue(afterLengthOverBefore && haveTestMsg);
        }

        private static String readLogFile(String fileName) throws IOException, IOException {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }
}
