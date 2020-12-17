import java.io.IOException;

public class main {
    private static boolean stop = false;
    public static void main(String[] args) throws IOException {
        String command;
        while(!stop){
            command = readCommand();
            switch(command){
                case "hello":
                    System.out.println("Hello World");
                    break;
                case "stop":
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
