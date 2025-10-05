// Hex.java
// Manually convert a hex string to decimal (beginner-level).
// Usage examples:
//   java Hex 7e3    -> 2019
//   java Hex 0xFF   -> 255
//   java Hex 7G3    -> error (invalid digit)

public class Hex {
    public static void main(String[] args) {
        // need exactly one argument
        if (args.length != 1) {
            System.err.println("Usage: java Hex <hex>   (e.g., 7E3 or 0x7E3)");
            System.exit(1);
        }

        String input = args[0];

        // allow optional 0x / 0X prefix
        if (input.startsWith("0x") || input.startsWith("0X")) {
            input = input.substring(2);
        }

        // must have at least one digit
        if (input.length() == 0) {
            System.err.println("Error: no digits after 0x.");
            System.exit(1);
        }

        long total = 0; // simple type; very huge hex may overflow (okay for this lab)

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            int value;

            if (ch >= '0' && ch <= '9') {
                value = ch - '0';
            } else if (ch >= 'A' && ch <= 'F') {
                value = 10 + (ch - 'A');
            } else if (ch >= 'a' && ch <= 'f') {
                value = 10 + (ch - 'a');
            } else {
                System.err.println("Error: invalid hex digit '" + ch + "' at position " + i + ".");
                System.exit(1);
                return; // keeps compiler happy after System.exit
            }

            total = total * 16 + value; // manual accumulate
        }

        System.out.println(total);
    }
}
