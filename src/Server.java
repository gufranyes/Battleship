import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws Exception{
        //Initialize the scanner
        Scanner scanner = new Scanner(System.in);

        //Asks for the player name to display
        System.out.print("Please enter your name: ");
        String name = scanner.nextLine();

        //Asks for the Server Port to connect
        System.out.print("Please enter your port: ");
        int port = scanner.nextInt();

        GameBoard gameBoard = new GameBoard();
        ServerSocket serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();

        DataInputStream readClient = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream sendClient = new DataOutputStream(clientSocket.getOutputStream());

        System.out.println("Connected.");

        System.out.println("Here is the default board layout with coordinates:");

        //Prints default board layout like 1,1 - 1,2...
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print((i + 1) + "," + (j + 1) + " ");
            }
            System.out.println();
        }

        System.out.println("And here is your empty board:");

        //Allows the player to place ships according to their types
        gameBoard.board();
        placeShips(gameBoard, "Carrier", 'C');
        placeShips(gameBoard, "Battleship", 'B');
        placeShips(gameBoard, "Submarine", 'S');
        placeShips(gameBoard, "Destroyer", 'D');

        boolean gameIsOn = true;

        //Initialize the board of attempts.
        GameBoard attemptsBoard = new GameBoard();

        //Transfers the names via the socket
        String clientName = readClient.readLine();
        sendClient.writeBytes(name+"\n");
        sendClient.flush();

        //States stands for the exit gracefully
        while (gameIsOn && !clientSocket.isClosed()) {

            //Server sends a hit to the Client
            System.out.println("Please enter the x coordinate of your attack:");
            int x = scanner.nextInt();
            System.out.println("Please enter the y coordinate of your attack:");
            int y = scanner.nextInt();

            sendClient.writeInt(x);
            sendClient.writeInt(y);
            sendClient.flush();

            //Takes the response from the client and displays attempt board
            String result = readClient.readLine();
            System.out.println(name);
            gameBoard.board();
            if (result.equals("Hit")){
                attemptsBoard.showAttempts(true,x,y, clientName);
            }
            else if (result.equals("Miss")) {
                attemptsBoard.showAttempts(false, x, y, clientName);
            }
            System.out.println("Result From " + clientName + ": " + result);
            if (result.equals("You won!") || result.equals("You lost!")){
                break;
            }

            //Server takes hit from the Client
            int hitFromClientX = readClient.readInt() - 1;
            int hitFromClientY = readClient.readInt() - 1;
            System.out.println("Hit X from " + clientName + ": " + (hitFromClientX+1) + " Hit Y from " + clientName + ": " + (hitFromClientY+1));

            //Checks winning conditions and sends a signal to the server on hit or miss
            if (gameBoard.hitOrMiss(hitFromClientX, hitFromClientY)) {
                if (!gameBoard.checkForLoss()) {
                    sendClient.writeBytes("You won!\n");
                    System.out.println("You lost!");
                    gameIsOn = false;
                    clientSocket.close();
                    break;
                }
                sendClient.writeBytes("Hit\n");
                sendClient.flush();
            } else {
                sendClient.writeBytes("Miss\n");
                sendClient.flush();
            }

        }
    }

    //Allows Player to place ships instead of random placement.
    public static void placeShips(GameBoard gameBoard, String shipName, char shipType) {
        int placeX1, placeY1, placeX2, placeY2;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the x1 coordinate of your " + shipName);
        placeX1 = scanner.nextInt();
        System.out.println("Please enter the y1 coordinate of your " + shipName);
        placeY1 = scanner.nextInt();
        System.out.println("Please enter the x2 coordinate of your " + shipName);
        placeX2 = scanner.nextInt();
        System.out.println("Please enter the y2 coordinate of your " + shipName);
        placeY2 = scanner.nextInt();

        if (placeX1 < 1 || placeX1 > 10 || placeY1 < 1 || placeY1 > 10 ||
                placeX2 < 1 || placeX2 > 10 || placeY2 < 1 || placeY2 > 10)
        {
            System.out.println("Please enter valid coordinates.");
            placeShips(gameBoard, shipName, shipType);
        } else if (!gameBoard.placeBoat(placeX1, placeY1, placeX2, placeY2, shipType)) {
            System.out.println("Please enter valid coordinates.");
            placeShips(gameBoard, shipName, shipType);
        } else {
            gameBoard.placeBoat(placeX1, placeY1, placeX2, placeY2, shipType);
            gameBoard.board();
        }
    }
}
