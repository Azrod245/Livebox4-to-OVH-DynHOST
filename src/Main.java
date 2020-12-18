import java.io.IOException;

public class Main {

    private static boolean stop = false;
    private static WorkingThread workingThread;

    public static void main(String[] args) throws IOException {
        String command;
        workingThread = new WorkingThread("192.168.1.1", 1000);
        workingThread.start();
        while(!stop){
            command = readCommand();
            switch(command){
                case "hello":
                    System.out.println("Hello World");
                    break;
                case "stop":
                    workingThread.quit();
                    stop = true;
                    break;
                default:
                    System.out.println("Error: Unknown command");
            }
        }
        System.out.println("stop");
    }

    private static String readCommand() throws IOException {
        char c;
        String command = "";
        while(true) {
            c = (char) System.in.read();
            if(c == '\n')
                break;
            command += c;
        }
        return command;
    }

}
