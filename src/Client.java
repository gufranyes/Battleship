import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception{
        //Initialize the scanner
        Scanner scanner = new Scanner(System.in);

        //Asks for the player name to display
        System.out.print("Please enter your name: ");
        String name = scanner.nextLine();

        //Asks for the Server IP to connect
        System.out.print("Please enter server IP: ");
        String ip = scanner.nextLine();

        //Asks for the Server Port to connect
        System.out.print("Please enter server port: ");
        int port = scanner.nextInt();

        GameBoard clientBoard = new GameBoard();
        Socket socket = new Socket(ip, port);

        DataInputStream readServer = new DataInputStream(socket.getInputStream());
        DataOutputStream sendServer = new DataOutputStream(socket.getOutputStream());

        System.out.println("Connected");

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
        clientBoard.board();
        placeShips(clientBoard, "Carrier", 'C');
        placeShips(clientBoard, "Battleship", 'B');
        placeShips(clientBoard, "Submarine", 'S');
        placeShips(clientBoard, "Destroyer", 'D');

        //Initialize the board of attempts.
        GameBoard attemptsBoard = new GameBoard();

        //Transfers the names via the socket
        sendServer.writeBytes(name+"\n");
        String serverName = readServer.readLine();
        sendServer.flush();

        //States stands for the exit gracefully
        while (!socket.isClosed()) {

            //Client takes hit from the Server
            int hitFromServerX = readServer.readInt() - 1;
            int hitFromServerY = readServer.readInt() - 1;
            System.out.println("Hit X from " + serverName + ": " + (hitFromServerX+1) + " Hit Y from " + serverName + ": " + (hitFromServerY+1));

            //Checks winning conditions and sends a signal to the server on hit or miss
            if (clientBoard.hitOrMiss(hitFromServerX, hitFromServerY)) {
                if (!clientBoard.checkForLoss()) {
                    sendServer.writeBytes("You won!\n");
                    System.out.println("You lost!");
                    socket.close();
                    break;
                }
                sendServer.writeBytes("Hit\n");
            } else {
                sendServer.writeBytes("Miss\n");
            }
            sendServer.flush();

            //Client sends a hit to the Server
            System.out.println("Please enter the x coordinate of your attack:");
            int x = scanner.nextInt();
            System.out.println("Please enter the y coordinate of your attack:");
            int y = scanner.nextInt();

            sendServer.writeInt(x);
            sendServer.writeInt(y);
            sendServer.flush();

            //Takes the response from server and displays attempt board
            String result = readServer.readLine();
            System.out.println(name);
            clientBoard.board();
            if (result.equals("Hit")){
                attemptsBoard.showAttempts(true,x,y, serverName);
            }
            else if (result.equals("Miss")) {
                attemptsBoard.showAttempts(false, x, y, serverName);
            }
            System.out.println("Result From " + serverName + ": " + result);
            if (result.equals("You won!") || result.equals("You lost!")){
                break;
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
