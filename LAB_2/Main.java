
import java.util.*;
import java.io.*;


public class Main {

    static HashMap<String, HashMap<String, HashSet<String>>> NFA = new HashMap<>();
    static HashMap<HashSet<String>, HashMap<String, HashSet<String>>> DFA = new HashMap<>();

    static HashSet<String> terminal = new HashSet<>();
    static HashSet<String> transValues = new HashSet<>();
    static HashSet<String> finalStates = new HashSet<>();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String s, startState;
        HashSet<String> start = new HashSet<>();

        System.out.println("Input the start state: ");
        startState = sc.next();
        start.add("->" + startState);

        System.out.println("---------------------------> After finishing to input type 'go' <--------------------------------\n\n");
        System.out.println("Input the terminal symbols: ");

        while(sc.hasNext()) {
            s = sc.next();

            if(s.equals("go"))
                break;
            terminal.add(s);
        }

        System.out.println("Input the final states: ");

        while(sc.hasNext()) {
            s = sc.next();

            if(s.equals("go"))
                break;
            finalStates.add(s);
        }

        System.out.println("Input the transition values: ");

        while(sc.hasNext()) {
            s = sc.next();

            if(s.equals("go"))
                break;
            transValues.add(s);
        }


        System.out.println("Input the transitions of the NFA, if a transition leads to nothing, type 'empty'\n\n");

        for(String str : terminal) {
            NFA.put(str, new HashMap<>());

            for(String str1 : transValues) {
                System.out.println("State '" + str + "' with '" + str1 + "' goes to : ");
                NFA.get(str).put(str1, new HashSet<>());

                while(sc.hasNext()) {
                    s = sc.next();
                    if(s.equals("empty"))
                        continue;

                    if(s.equals("go"))
                        break;

                    NFA.get(str).get(str1).add(s);
                }
            }
        }


        for(String key : NFA.keySet()) {
            for(String val : transValues) {
                dfs(NFA.get(key).get(val));
            }
        }

        DFA.put(start, NFA.get(startState));

        System.out.println("----->NFA<----");
        for(String str : transValues) {
            System.out.print("\t" + str);
        }
        System.out.println();
        for(String str : terminal) {
            if(str.equals(startState))
                System.out.print("->" + str);

            else if(finalStates.contains(str))
                System.out.print("*" + str);
            else
                System.out.print(str);

            for(String val : transValues) {
                System.out.print(" ");
                if(NFA.get(str).get(val).isEmpty())
                    System.out.print("-");
                else {
                    for(String str1 : NFA.get(str).get(val))
                        System.out.print(str1);
                }
            }

            System.out.println();
        }
        System.out.println("\n\n");

        System.out.println("----->DFA<----");
        for(String str : transValues) {
            System.out.print("\t" + str);
        }

        System.out.println();
        for(HashSet<String> str : DFA.keySet()) {
            for(String fin : finalStates) {
                if(str.contains(fin)) {
                    System.out.print("*");
                    break;
                }
            }

            for(String str1 : str)
                System.out.print(str1);

            for(String val : transValues) {
                System.out.print(" ");
                if(DFA.get(str).get(val).isEmpty())
                    System.out.print("-");
                else {
                    for(String str1 : DFA.get(str).get(val))
                        System.out.print(str1);
                }
            }

            System.out.println();
        }
    }

    static void dfs(HashSet<String> orig) {

        if(DFA.containsKey(orig) || orig.isEmpty())
            return;

        DFA.put(orig, new HashMap<>());

        for(String val1 : transValues) {
            DFA.get(orig).put(val1, new HashSet<>());
            for(String temp : orig) {
                DFA.get(orig).get(val1).addAll(NFA.get(temp).get(val1));
            }

            dfs(DFA.get(orig).get(val1));
        }
    }
}

