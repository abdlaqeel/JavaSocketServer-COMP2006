import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static String output = "";
    private static String eor = "[EOR]"; // a code for end-of-response
    
    // establishing a connection
    private static void setup() throws IOException {
        
        serverSocket = new ServerSocket(0);
        toConsole("Server port is " + serverSocket.getLocalPort());
        
        clientSocket = serverSocket.accept();

        // get the input stream and attach to a buffered reader
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        // get the output stream and attach to a printwriter
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        toConsole("Accepted connection from "
                 + clientSocket.getInetAddress() + " at port "
                 + clientSocket.getPort());
            
        sendGreeting();
    }
    
    private static void sendGreeting()
    {
        appendOutput("Greetings from Wordnet!\n");
        appendOutput("Please Enter your username:");
        sendOutput();
    }
    
    // what happens while client and server are connected
    private static void talk() throws IOException {
        gameClient(); //calls the game method
        disconnect();
    }
    
    // repeatedly take input from client and send back in upper case
    private static void echoClient() throws IOException
    {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            appendOutput(inputLine.toUpperCase());
            sendOutput();
            toConsole(inputLine);
        }
    }
    
    private static void disconnect() throws IOException {
        out.close();
        toConsole("Disconnected.");
        System.exit(0);
    }
    
    // add a line to the next message to be sent to the client
    private static void appendOutput(String line) {
        output += line + "\r";
    }
    
    // send next message to client
    private static void sendOutput() {
        out.println( output + "[EOR]");
        out.flush();
        output = "";
    }
    
    // because it makes life easier!
    private static void toConsole(String message) {
        System.out.println(message);
    }
    
    public static void main(String[] args) {
        try {
            setup();
            talk();
        }
        catch( IOException ioex ) {
            toConsole("Error: " + ioex );
        }
    }
    //method for the comp,ete game
    private static void gameClient() throws IOException{
        char word [] = {'T','O','P','P','L','E'}; //correct words
        String answer; //string for commandline input
        int guesses = 1;  //tracks total number of guesses
        int loginTries = 0; //tracks total number of login tries

        while ((answer = in.readLine()) != null) {
            if (answer.equals("Sammy")){
                appendOutput("Please Enter the Password ");
                sendOutput();
                toConsole("Correct Username");
                break;
            }
            else{
                loginTries++;
                if (loginTries == 5) {disconnect();}
                appendOutput("Username not found\n");
                appendOutput("Please enter username: ");
                sendOutput();
                toConsole("Incorrect username Entered");
                continue;
            }
        }

        while ((answer = in.readLine()) != null) {
            if (answer.equals("WOOF")){
                appendOutput("Welcome Sammy!\n");
                appendOutput("Guess the mystery six-letter word");
                sendOutput();
                toConsole("Correct Password");
                break;
            }
            else{
                loginTries++;
                if (loginTries == 5) {disconnect();}
                appendOutput("Incorrect Password\n");
                appendOutput("Please enter password again: ");
                sendOutput();
                toConsole("Wrong password entered");
                continue;
            }
        }
        while ((answer = in.readLine()) != null) {
            char returnCaps [] = new char[6]; //to store capitals
            char[] guessWord = answer.toCharArray();
            if ((answer.toCharArray().length != 6) || (!answer.matches("[a-zA-Z]+"))){
                guesses++;
                appendOutput("Oops! You need enter a six letter word containing only alphabetical characters and no spaces.\n");
                appendOutput("Try Again: ");
                sendOutput();
                toConsole("Wrong format was entered");
                continue;
            }

            for (int i=0; i<6; i++){ //converts to capital
                if (Character.toUpperCase(guessWord[i]) == word[i]){
                    returnCaps[i] = word[i];
                }
                else{
                    returnCaps[i] = '*';//adds aestricks
                }
            }

            //gameplay

            if (String.valueOf(returnCaps).equals(String.valueOf(word))){//if the word entered is the correct guess
                appendOutput("You got it in " + guesses + " turns - well done and goodbye!"); //game ends and outputs tries
                toConsole("Correct answer!");
                sendOutput();
                disconnect();
            }
            else{
                guesses++;
                appendOutput(String.valueOf(returnCaps)+ "\n");
                appendOutput("Please try again: ");
                sendOutput();
                toConsole("Wrong answer!");
                continue;
            }
        }
    }
}
