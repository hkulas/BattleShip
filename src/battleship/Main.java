package battleship;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        // Write your code here
        Game game = new Game();
        String[][] player1 = game.prepareGameBoard();
        String[][] fogOfWar1 = Arrays.stream(player1).map(String[]::clone).toArray(String[][]::new);


        String[][] player2 = game.prepareGameBoard();
        String[][] fogOfWar2 = Arrays.stream(player1).map(String[]::clone).toArray(String[][]::new);


        for (int i = 1; i < 3; i++) {
            if (i == 1) {
                game.startGame(player1, i);
            } else {
                game.startGame(player2, i);
            }
        }

        boolean gameNotFinished = true;
        int currentPlayer = 1;
        while (gameNotFinished) {
            if (currentPlayer == 2) {
                printTwoSidesOfBoard(player2, fogOfWar1, currentPlayer, game);
                currentPlayer = 1;
                gameNotFinished = game.takeAShot(player1, fogOfWar2);
            } else {
                printTwoSidesOfBoard(player1, fogOfWar2, currentPlayer, game);
                gameNotFinished = game.takeAShot(player2, fogOfWar1);
                currentPlayer = 2;
            }


        }


    }

    public static void printTwoSidesOfBoard(String[][] playerBoard, String[][] fogOfWar, int player, Game game) {
        System.out.println();
        game.printGameBoard(fogOfWar);
        System.out.println("---------------------");
        game.printGameBoard(playerBoard);
        System.out.printf(Messages.NEXT_TURN, player);
    }
}

enum LeftSide {
    A(0), B(1), C(2), D(3), E(4), F(5),
    G(6), H(7), I(8), J(9);

    int position;

    LeftSide(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public static List<String> getNames() {
        return Stream.of(LeftSide.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}

class ValidatePlacement {
    private static final String SHIP_SYMBOL = "O";

    public boolean validateIfEnumContains(String position) {
        return LeftSide.getNames().contains(position);
    }

    public boolean validateLengthOfShip(int submarineLength, String positionA, String positionB) {
        String[] positionAValues = positionA.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        String[] positionBValues = positionB.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        int startRow = LeftSide.valueOf(positionAValues[0]).getPosition();
        int endRow = LeftSide.valueOf(positionBValues[0]).getPosition();
        int startColumn = Integer.parseInt(positionAValues[1]) - 1;
        int endColumn = Integer.parseInt(positionBValues[1]) - 1;

        if (startRow == endRow) {
            return submarineLength - 1 == Math.abs(startColumn - endColumn);
        } else {
            return submarineLength - 1 == Math.abs(startRow - endRow);
        }
    }

    public boolean validateShipLocation(String positionA, String positionB) {
        String[] positionAValues = positionA.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        String[] positionBValues = positionB.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        int startRow = LeftSide.valueOf(positionAValues[0]).getPosition();
        int endRow = LeftSide.valueOf(positionBValues[0]).getPosition();
        int startColumn = Integer.parseInt(positionAValues[1]) - 1;
        int endColumn = Integer.parseInt(positionBValues[1]) - 1;

        return (startRow == endRow) || (startColumn == endColumn);

    }

    public boolean validateNeighbours(String[][] gameBoard, int startRow, int endRow, int startColumn, int endColumn) {

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                if (validatePoint(i + 1, j)) {
                    if (gameBoard[i + 1][j].equals(SHIP_SYMBOL)) {
                        return false;
                    }
                }
                if (validatePoint(i - 1, j)) {
                    if (gameBoard[i - 1][j].equals(SHIP_SYMBOL)) {
                        return false;
                    }

                }
                if (validatePoint(i, j + 1)) {
                    if (gameBoard[i][j + 1].equals(SHIP_SYMBOL)) {
                        return false;
                    }
                }
                if (validatePoint(i, j - 1)) {
                    if (gameBoard[i][j - 1].equals(SHIP_SYMBOL)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean validateNeighbours(String[][] gameBoard, int row, int column) {

        if (validatePoint(row + 1, column)) {
            if (gameBoard[row + 1][column].equals(SHIP_SYMBOL)) {
                return false;
            }
        }
        if (validatePoint(row - 1, column)) {
            if (gameBoard[row - 1][column].equals(SHIP_SYMBOL)) {
                return false;
            }

        }
        if (validatePoint(row, column + 1)) {
            if (gameBoard[row][column + 1].equals(SHIP_SYMBOL)) {
                return false;
            }
        }
        if (validatePoint(row, column - 1)) {
            if (gameBoard[row][column - 1].equals(SHIP_SYMBOL)) {
                return false;
            }

        }
        return true;
    }

    public boolean validateIfHasShips(String[][] gameBoard) {
        for (int i = 0; i <= gameBoard.length - 1; i++) {
            for (int j = 0; j <= gameBoard[0].length - 1; j++) {
                if (gameBoard[i][j].equals(SHIP_SYMBOL)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean validatePoint(int row, int column) {
        return row >= 0 && row <= 9 && column >= 0 && column <= 9;
    }
}

class Game {


    public static final LeftSide[] leftSide = LeftSide.values();
    public static final String topSide = " 1 2 3 4 5 6 7 8 9 10";
    private static final String SHIP_SYMBOL = "O";
    private static final String HIT_SYMBOL = "X";
    private static final String MISSED_SYMBOL = "M";

    public String[][] prepareGameBoard() {
        String[][] gameBoard = new String[10][10];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                gameBoard[i][j] = "~";
            }
        }

        return gameBoard;
    }

    public void printGameBoard(String[][] gameBoard) {
        System.out.println(topSide);
        for (int i = 0; i < gameBoard.length; i++) {
            System.out.print(leftSide[i].name());
            for (int j = 0; j < gameBoard[i].length; j++) {
                System.out.printf(" %s", gameBoard[i][j]);
            }
            printNewLine();
        }
    }

    private void printNewLine() {
        System.out.println();
    }

    public boolean placeShip(String[][] gameBoard, String positionA, String positionB, int shipLength) {

        String[] positionAValues = positionA.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        String[] positionBValues = positionB.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        int startRow = LeftSide.valueOf(positionAValues[0]).getPosition();
        int endRow = LeftSide.valueOf(positionBValues[0]).getPosition();
        int startColumn = Integer.parseInt(positionAValues[1]) - 1;
        int endColumn = Integer.parseInt(positionBValues[1]) - 1;
        if (startColumn > endColumn) {
            int temp = startColumn;
            startColumn = endColumn;
            endColumn = temp;
        }
        if (startRow > endRow) {
            int temp = startRow;
            startRow = endRow;
            endRow = temp;
        }

        ValidatePlacement validatePlacement = new ValidatePlacement();
        List<String> errors = new ArrayList<>();
        if (!validatePlacement.validateLengthOfShip(shipLength, positionA, positionB)) {
            errors.add(ErrorMessages.WRONG_SHIP_LENGTH);
        }

        if (!validatePlacement.validateNeighbours(gameBoard, startRow, endRow, startColumn, endColumn)) {
            errors.add(ErrorMessages.TOO_CLOSE_TO_ANOTHER_ONE);
        }

        if (!validatePlacement.validateShipLocation(positionA, positionB)) {
            errors.add(ErrorMessages.WRONG_SHIP_LOCATION);
        }

        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            return false;
        }
        if (startColumn > endColumn) {
            int temp = startColumn;
            startColumn = endColumn;
            endColumn = temp;
        }
        if (startRow > endRow) {
            int temp = startRow;
            startRow = endRow;
            endRow = temp;
        }

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {

                if (!validatePlacement.validatePoint(i, j)) {
                    System.out.println(ErrorMessages.INDEX_OUT_OF_BOARD);
                    return false;
                } else {
                    gameBoard[i][j] = SHIP_SYMBOL;
                }
            }

        }
        return true;
    }

    public boolean takeAShot(String[][] gameBoard, String[][] fogOfWar) {
        Scanner scanner = new Scanner(System.in);
        String position = scanner.next();

        String[] positionValues = position.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        int row = 0;
        int column = Integer.parseInt(positionValues[1]) - 1;
        ValidatePlacement validatePlacement = new ValidatePlacement();

        if (!validatePlacement.validateIfEnumContains(positionValues[0])) {
            System.out.println(ErrorMessages.WRONG_SHOT_COORDINATE);
            return true;
        } else {
            row = LeftSide.valueOf(positionValues[0]).getPosition();
            if (!validatePlacement.validatePoint(row, column)) {
                System.out.println(ErrorMessages.WRONG_SHOT_COORDINATE);
                return true;
            } else {
                if (takeAShot(gameBoard, fogOfWar, row, column)) {
                    if (!validatePlacement.validateIfHasShips(gameBoard)) {
                        System.out.println(Messages.CONGRATULATIONS);
                        return false;
                    } else {
                        if (validatePlacement.validateNeighbours(gameBoard, row, column)) {
                            System.out.println(Messages.SHIP_SANK);
                            printNextPlayer();
                        } else {
                            System.out.println(Messages.SHOT_HIT);
                            printNextPlayer();
                        }
                        return true;
                    }
                } else {
                    System.out.println(Messages.SHOT_MISSED);
                    printNextPlayer();

                    return true;
                }
            }
        }
    }

    public static void printNextPlayer() {
        System.out.println(Messages.NEXT_PLAYER);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean takeAShot(String[][] gameBoard, String[][] fogOfWar, int row, int column) {
        String position = gameBoard[row][column];
        if (position.equals(SHIP_SYMBOL) || position.equals(HIT_SYMBOL)) {
//            fogOfWar[row][column] = HIT_SYMBOL;
            gameBoard[row][column] = HIT_SYMBOL;
            return true;
        } else {
//            fogOfWar[row][column] = MISSED_SYMBOL;
            gameBoard[row][column] = MISSED_SYMBOL;
            return false;
        }
    }

    public void startGame(String[][] gameBoard) {

    }

    public void startGame(String[][] gameBoard, int player) {
        System.out.printf("Player %d, place your ships on the game field\n", player);
        printGameBoard(gameBoard);
        String[][] fogOfWar = Arrays.stream(gameBoard).map(String[]::clone).toArray(String[][]::new);

        Scanner scanner = new Scanner(System.in);
        boolean gameReady = false;

        int shipSize = 5;
        while (!gameReady) {
            switch (shipSize) {
                case 5 -> {
                    if (prepareAircraftCarrier(gameBoard, scanner)) {
                        shipSize--;
                        printGameBoard(gameBoard);
                    }
                }
                case 4 -> {
                    if (prepareBattleship(gameBoard, scanner)) {
                        shipSize--;
                        printGameBoard(gameBoard);
                    }
                }
                case 3 -> {
                    if (prepareSubmarine(gameBoard, scanner)) {
                        shipSize--;
                        printGameBoard(gameBoard);
                    }
                }
                case 2 -> {
                    if (prepareCruiser(gameBoard, scanner)) {
                        shipSize--;
                        printGameBoard(gameBoard);
                    }
                }
                case 1 -> {
                    if (prepareDestroyer(gameBoard, scanner)) {
                        shipSize--;
                        printGameBoard(gameBoard);
                        printNextPlayer();
                        gameReady = true;
                    }
                }
            }
        }
    }
//---------- uncomment below lines for testing
//    private boolean prepareAircraftCarrier(String[][] gameBoard, Scanner scanner) {
//        System.out.println("\nEnter the coordinates of the Aircraft Carrier (5 cells):");
//        int shipLength = 5;
//        return placeShip(gameBoard, "F3", "F7", shipLength);
//    }
//
//    private boolean prepareBattleship(String[][] gameBoard, Scanner scanner) {
//        System.out.println("\nEnter the coordinates of the Battleship (4 cells):");
//        int shipLength = 4;
//        return placeShip(gameBoard, "A1", "D1", shipLength);
//    }
//
//    private boolean prepareSubmarine(String[][] gameBoard, Scanner scanner) {
//        System.out.println("\nEnter the coordinates of the Submarine (3 cells):");
//        int shipLength = 3;
//        return placeShip(gameBoard, "J10", "J8", shipLength);
//    }
//
//    private boolean prepareCruiser(String[][] gameBoard, Scanner scanner) {
//        System.out.println("\nEnter the coordinates of the Cruiser (3 cells):");
//        int shipLength = 3;
//        return placeShip(gameBoard, "B9", "D9", shipLength);
//    }
//
//    private boolean prepareDestroyer(String[][] gameBoard, Scanner scanner) {
//        System.out.println("\nEnter the coordinates of the Destroyer (2 cells):");
//        int shipLength = 2;
//        return placeShip(gameBoard, "I2", "J2", shipLength);
//    }

    //--------------------------------------------------------------- comment below lines for testing

    private boolean prepareAircraftCarrier(String[][] gameBoard, Scanner scanner) {
        System.out.println("\nEnter the coordinates of the Aircraft Carrier (5 cells):");
        int shipLength = 5;
        String positionA = scanner.next();
        String positionB = scanner.next();
        return placeShip(gameBoard, positionA, positionB, shipLength);
    }

    private boolean prepareBattleship(String[][] gameBoard, Scanner scanner) {
        System.out.println("\nEnter the coordinates of the Battleship (4 cells):");
        int shipLength = 4;
        String positionA = scanner.next();
        String positionB = scanner.next();
        return placeShip(gameBoard, positionA, positionB, shipLength);
    }

    private boolean prepareSubmarine(String[][] gameBoard, Scanner scanner) {
        System.out.println("\nEnter the coordinates of the Submarine (3 cells):");
        int shipLength = 3;
        String positionA = scanner.next();
        String positionB = scanner.next();
        return placeShip(gameBoard, positionA, positionB, shipLength);
    }

    private boolean prepareCruiser(String[][] gameBoard, Scanner scanner) {
        System.out.println("\nEnter the coordinates of the Cruiser (3 cells):");
        int shipLength = 3;
        String positionA = scanner.next();
        String positionB = scanner.next();
        return placeShip(gameBoard, positionA, positionB, shipLength);
    }

    private boolean prepareDestroyer(String[][] gameBoard, Scanner scanner) {
        System.out.println("\nEnter the coordinates of the Destroyer (2 cells):");
        int shipLength = 2;
        String positionA = scanner.next();
        String positionB = scanner.next();
        return placeShip(gameBoard, positionA, positionB, shipLength);
    }


}

class ErrorMessages {
    public static String INDEX_OUT_OF_BOARD = "\nIndex Out Of Board";
    public static String TOO_CLOSE_TO_ANOTHER_ONE = "\nError! You placed it too close to another one. Try again:";
    public static String WRONG_SHIP_LOCATION = "\nError! Wrong ship location! Try again:";
    public static String WRONG_SHIP_LENGTH = "\nError! Wrong length of the Submarine! Try again:";

    public static String WRONG_SHOT_COORDINATE = "\nError! You entered the wrong coordinates! Try again:";

}

class Messages {
    public static String SHOT_MISSED = "\nYou missed!";
    public static String SHOT_HIT = "\nYou hit a ship! Try again:";
    public static String TAKE_A_SHOT = "\nTake a shot!";
    public static String SHIP_SANK = "\nYou sank a ship! Specify a new target:";
    public static String CONGRATULATIONS = "\nYou sank the last ship. You won. Congratulations!";
    public static String NEXT_PLAYER = "Press Enter and pass the move to another player";
    public static String NEXT_TURN = "\nPlayer %d, it's your turn:\n";
}

