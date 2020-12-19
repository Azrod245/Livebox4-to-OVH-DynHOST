import java.io.IOException;

public class Main {

    private static boolean stop = false;
    private static WorkingThread workingThread;

    public static void main(String[] args) throws IOException {
        if(args.length>=5) {
            String command;
            workingThread = new WorkingThread(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
            workingThread.start();
            while (!stop) {
                command = readCommand();
                switch (command) {
                    case "hello":
                        System.out.println("Hello World");
                        break;
                    case "stop":
                        workingThread.quit();
                        stop = true;
                        break;
                    case "refresh":
                        workingThread.sendIP();
                        break;
                    default:
                        System.out.println("Error: Unknown command");
                }
            }
            System.out.println("stop");
        }else{
            System.out.println("Not enough arguments were given");
        }
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
