public class Hex {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Hex <hex>   (e.g., 7E3 or 0x7E3)");
            System.exit(1);
        }

        String input = args[0];

        if (input.startsWith("0x") || input.startsWith("0X")) {
            input = input.substring(2);
        }

        if (input.length() == 0) {
            System.err.println("Error: no digits after 0x.");
            System.exit(1);
        }

        long total = 0; 

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
                return;
            }

            total = total * 16 + value;
        }

        System.out.println(total);
    }
}