
import java.util.*;
import java.io.*;


public class Main {



    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String s;

        HashMap<Character, HashMap<Character, Character>> fa = new HashMap<>();

        System.out.println("\n\n       ---------------------------In order to finish printing a list type 'go' -------------------------------------\n\n\n");


        System.out.println("Print the derivations one by one(e.g S -> aP): ");

        while(true) {
            s = scanner.nextLine();

            if (s.equals("go"))
                break;

            String[] parts = s.split(" -> ");

            if(fa.containsKey(parts[0].charAt(0))) {
                if(parts[1].length() == 2) {
                    fa.get(parts[0].charAt(0)).put(parts[1].charAt(0), parts[1].charAt(1));
                } else {
                    fa.get(parts[0].charAt(0)).put(parts[1].charAt(0), '$');
                }

            } else {
                HashMap<Character, Character> sr = new HashMap<>();

                if(parts[1].length() == 2) {
                    sr.put(parts[1].charAt(0), parts[1].charAt(1));
                } else {
                    sr.put(parts[1].charAt(0), '$');
                }
                fa.put(parts[0].charAt(0), sr);
            }
        }

        System.out.println("\n\nPrint the string to be verified: ");
        s = scanner.nextLine();
        char next = 'S';
        StringBuilder route = new StringBuilder();
        route.append(" -> S");

        for(int i = 0; i < s.length(); i++) {
            if(fa.get(next).containsKey(s.charAt(i))) {
                next = fa.get(next).get(s.charAt(i));
                if (next != '$')
                    route.append(" -> " + next);
            }

            if(i == s.length() - 1) {
                if(next == '$')
                {
                    System.out.println("\nThe string '" + s + "' is accepted");
                    System.out.println("The traversed route to obtain the string is: " + route.toString());

                }
                else
                    System.out.println("The string '" + s + "' is not accepted");

            }
        }

    } //main
}

