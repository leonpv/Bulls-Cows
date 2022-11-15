import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final char[] arrayOfPossibleChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the secret code's length:");

        byte lengthSecretNumber;
        try {
            lengthSecretNumber = scanner.nextByte();
        } catch (Exception e) {
            System.out.println("Error: Input isn't a valid number.");
            return;
        }
        if (isCheckLengthSecretNumberFail(lengthSecretNumber)) return;

        System.out.println("Please, enter the number of possible symbols in the code:");

        byte countPossibleSymbols;
        try {
            countPossibleSymbols = scanner.nextByte();
        } catch (Exception e) {
            System.out.println("Error: Input isn't a valid number.");
            return;
        }
        if (isCheckCountPossibleSymbolsFail(countPossibleSymbols, lengthSecretNumber)) return;

        printStart(lengthSecretNumber, countPossibleSymbols);
        if (countPossibleSymbols <= 10) {
            gameV1(scanner, lengthSecretNumber);
        }
        if (countPossibleSymbols > 10) {
            gameV2(scanner, lengthSecretNumber, countPossibleSymbols);
        }
    }

    private static boolean isCheckCountPossibleSymbolsFail(final byte countPossibleSymbols, final byte lengthSecretNumber) {
        if (countPossibleSymbols < lengthSecretNumber || countPossibleSymbols > 36) {
            System.out.println("Error: number of possible characters less than length of secret number or greater than 36");
            return true;
        }
        return false;
    }

    private static boolean isCheckLengthSecretNumberFail(final byte lengthSecretNumber) {
        if (lengthSecretNumber > 36 || lengthSecretNumber < 1) {
            System.out.println("Error: can't generate a secret number with a length of " + lengthSecretNumber + " because there aren't enough unique digits.");
            return true;
        }
        return false;
    }

    private static void printStart(byte lengthSecretNumber, byte countPossibleSymbols) {
        System.out.print("The secret is prepared: ");
        StringBuilder sb = new StringBuilder();
        while (lengthSecretNumber-- != 0) {
            sb.append("*");
        }
        System.out.print(sb + " ");
        if (countPossibleSymbols <= 10) {
            System.out.println("(" + arrayOfPossibleChars[0] + "-" + arrayOfPossibleChars[countPossibleSymbols - 1] + ").");
        }
        if (countPossibleSymbols > 10) {
            System.out.println("(0-9, " + arrayOfPossibleChars[10] + "-" + arrayOfPossibleChars[countPossibleSymbols - 1] + ").");
        }
        System.out.println("Okay, let's start a game!");
    }

    private static void gameV2(Scanner scanner, final byte lengthSecretNumber, final byte countPossibleSymbols) {
        String secretStr = genSecretString(lengthSecretNumber, countPossibleSymbols);
        byte turn = 0;
        boolean isGameOver = false;
        String inputStr;

        while (!isGameOver) {
            System.out.println("Turn " + turn++ + ":");
            inputStr = scanner.next();
            if (inputStr.length() > lengthSecretNumber) {
                System.out.println("Error: Input is greater than length of secret number");
                continue;
            }
            byte[] bc = gradeV2(inputStr, secretStr);
            isGameOver = game(bc, lengthSecretNumber);
        }
    }

    private static byte[] gradeV2(String inputStr, String secretStr) {
        byte numberLength = (byte) inputStr.length();
        byte[] bc = new byte[]{0, 0};
        char[] chars = new char[numberLength];
        int temp = numberLength - 1;

        for (int i = 1; i <= numberLength; i++) {
            chars[temp--] = inputStr.charAt(numberLength - i);
        }

        char tempCH;
        for (int i = numberLength - 1; i >= 0; i--) {
            tempCH = secretStr.charAt(secretStr.length() - 1);
            secretStr = secretStr.substring(0, secretStr.length() - 1);
            if (chars[i] == tempCH) {
                bc[0]++;
                continue;
            }
            for (int j = numberLength - 1; j >= 0; j--) {
                if (chars[j] == tempCH) {
                    bc[1]++;
                    break;
                }
            }
        }

        return bc;
    }

    private static String genSecretString(final byte lengthSecretNumber, final byte countPossibleSymbols) {
        Random random = new Random();
        int upper = countPossibleSymbols - 1;
        int lower = 0;
        int r;
        StringBuilder rand = new StringBuilder();

        for (int i = 0; i < lengthSecretNumber; i++) {
            r = random.nextInt(upper - lower + 1) + lower;
            rand.append(arrayOfPossibleChars[r]);
        }

        if (isRepeatedDigitInString(rand.toString())) {
            rand.delete(0, rand.length());
            rand.append(genSecretString(lengthSecretNumber, countPossibleSymbols));
        }
        return rand.toString();
    }

    private static void gameV1(Scanner scanner, byte genSN) {
        long secretNumber = genSecretNumber(genSN);
        String strNumber;
        byte turn = 0;
        boolean isGameOver = false;

        while (!isGameOver) {
            System.out.println("Turn " + turn++ + ":");
            strNumber = scanner.next();
            if (!strNumber.chars().allMatch( Character::isDigit )) {
                System.out.println("Error: Input isn't a valid number.");
                continue;
            }
            byte[] bc = grade(strNumber, secretNumber);
            isGameOver = game(bc, genSN);
        }
    }

    private static boolean game(byte[] bc, short genSN) {
        if (Arrays.equals(bc, new byte[]{0, 0})) {
            System.out.print("Grade: None.\n");
        } else if (bc[0] == 0 && bc[1] > 0) {
            System.out.printf("Grade: %d cow(s).\n", bc[1]);
        } else if (bc[0] > 0 && bc[1] == 0) {
            System.out.printf("Grade: %d bull(s).\n", bc[0]);
            if (bc[0] == genSN) {
                System.out.print("Congratulations! You guessed the secret code.\n");
                return true;
            }
        } else {
            System.out.printf("Grade: %d bull(s) and %d cow(s).\n", bc[0], bc[1]);
        }
        return false;
    }

    private static long genSecretNumber(final byte genSN) {
        Random random = new Random();
        long upper = 1;
        long lower = 1;
        upper = upper * (long) Math.pow(10, genSN);
        lower = lower * (long) Math.pow(10, genSN);
        upper--;
        lower = lower / 10;
        var rand = random.nextLong(upper - lower + 1) + lower;
        if (isRepeatedDigitInNumber(rand)) {
            rand = genSecretNumber(genSN);
        }
        return rand;
    }

    private static byte[] grade(String strNumber, long secretNumber) {
        byte numberLength = (byte) strNumber.length();
        byte[] bc = new byte[]{0, 0};
        byte[] numbers = new byte[numberLength];
        int temp = numberLength - 1;

        for (int i = 1; i <= numberLength; i++) {
            numbers[temp--] = (byte) (Integer.parseInt(String.valueOf(strNumber.charAt(numberLength - i))));
        }

        byte tempNumber;
        for (int i = numberLength - 1; i >= 0; i--) {
            tempNumber = (byte) (secretNumber % 10);
            secretNumber = secretNumber / 10;
            if (numbers[i] == tempNumber) {
                bc[0]++;
                continue;
            }
            for (int j = numberLength - 1; j >= 0; j--) {
                if (numbers[j] == tempNumber) {
                    bc[1]++;
                    break;
                }
            }
        }
        return bc;
    }

    static boolean isRepeatedDigitInNumber(long n) {
        HashSet<Long> a = new HashSet<>();
        long d;

        // Traversing through each digit
        while (n != 0) {
            d = n % 10;

            // if the digit is present more than once in the number
            if (a.contains(d))

                // return true if the number has repeated digit
                return true;
            a.add(d);
            n /= 10;
        }

        // return false if the number has no repeated digit
        return false;
    }

    static boolean isRepeatedDigitInString(String str) {
        HashSet<Character> a = new HashSet<>();
        char d;
        byte counter = 0;

        // Traversing through each digit
        while (counter != str.length()) {
            //d = n % 10;
            d = str.charAt(counter);

            // if the digit is present more than once in the number
            if (a.contains(d))

                // return true if the number has repeated digit
                return true;
            a.add(d);
            //n /= 10;
            counter++;
        }

        // return false if the number has no repeated digit
        return false;
    }
}