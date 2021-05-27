
import jdk.jshell.EvalException;

import javax.sql.rowset.JdbcRowSet;
import java.util.*;


public class Main {

    static HashMap<String, HashSet<String>> productions = new HashMap<>();
    static HashMap<String, HashSet<String>> first = new HashMap<>();
    static HashMap<String, HashSet<String>> follow = new HashMap<>();
    static HashMap<String, HashMap<String, String>> parsingTable = new HashMap<>();

    static HashSet<String> terminalSymbols = new HashSet<>();
    static HashSet<String> nonTerminalSymbols = new HashSet<>();
    static Iterator<Map.Entry<String, HashSet<String>>> itr;

    static Stack<String> stack = new Stack<>();
    static String toParse;
    static Character customSymbol = 'Z';

    static void getInput() {

        Scanner sc = new Scanner(System.in);

        String s;
        stack.push("S");

        System.out.println("\n\n---------------------------> After finishing to input type 'go' <--------------------------------\n\n");
        System.out.println("Input the terminal symbols: ");

        while(sc.hasNext()) {
            s = sc.next();
            if(s.equals("go"))
                break;
            terminalSymbols.add(s);
        }

        System.out.println("\n\nInput the non-terminal symbols: ");

        while(sc.hasNext()) {
            s = sc.next();

            if(s.equals("go"))
                break;
            nonTerminalSymbols.add(s);
        }


        System.out.println("\n\nInput the productions, if the result of a production is an empty string, type 'eps'\n");

        for(String str : nonTerminalSymbols) {
            productions.put(str, new HashSet<>());

            System.out.println("\nPrint the productions of '" + str + "' : ");
            while(sc.hasNext()) {
                s = sc.next();

                if(s.equals("go"))
                    break;

                productions.get(str).add(s);
            }
        }

        System.out.println("\n\nInput the string to be analyzed: ");
        toParse = sc.next() + "$";
    }

    static void findFirsts(String check) {

        for(String val : productions.get(check)) {
            if(val.equals("eps")) {
                first.get(check).add("eps");
                continue;
            }

            int noEps = 0;
            for(int i = 0; i < val.length(); i++) {
                String s = val.substring(i, i+1);

                if(val.charAt(i) >= 'A' && val.charAt(i) <= 'Z') {

                    if(first.get(s).isEmpty())
                        findFirsts(s);

                    first.get(check).addAll(first.get(s));

                    if(!productions.get(s).contains("eps")) {
                        noEps = 1;
                        break;
                    }


                } else {
                     first.get(check).add(s);
                     noEps = 1;
                     break;
                }
            }

            if(noEps == 1)
                first.get(check).remove("eps");
        }
    }

    static void findFollow(String check) {


        for(String key : productions.keySet()) {
            for(String val : productions.get(key)) {

                if(val.equals("eps"))
                    continue;

                for(int i = 0 ; i < val.length(); i++) {
                    String orig =  val.substring(i, i+1);
                    if(orig.equals(check)) {

                        if(i == val.length() - 1) {
                            if(follow.get(key).isEmpty())
                                findFollow(key);

                            follow.get(check).addAll(follow.get(key));
                        }

                        int j = i+1;
                        for(; j < val.length(); j++) {

                            String s = val.substring(j, j + 1);
                            if (val.charAt(j) >= 'A' && val.charAt(j) <= 'Z') {
                                follow.get(orig).addAll(first.get(s));

                            } else {
                                follow.get(orig).add(s);
                                break;
                            }

                            if(!productions.get(s).contains("eps"))
                                break;

                            if (j + 1 >= val.length()) {
                                if(follow.get(key).isEmpty())
                                    findFollow(key);

                                follow.get(check).addAll(follow.get(key));
                            }
                        }

                        i = j;
                    }
                }
            }

            follow.get(key).remove("eps");
        }
    }

    static void makeParsingTable() {

        for(String key : productions.keySet()) {
            for(String val : productions.get(key)) {
                if(val.equals("eps")) {
                    for(String temp : follow.get(key))
                        parsingTable.get(key).put(temp, "eps");

                    continue;
                }

                for(String term : first.get(key)) {
                    for(int i = 0; i < val.length(); i++) {
                        String s = val.substring(i, i+1);

                        if(val.charAt(i) >= 'A' && val.charAt(i) <= 'Z') {

                            if(first.get(s).contains(term)) {
                                parsingTable.get(key).put(term, val);
                                break;
                            }

                            if(!productions.get(s).contains("eps"))
                                break;

                        } else {
                            if(s.equals(term)) {
                                parsingTable.get(key).put(term, val);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    static void parseLL1() {

        int r = 0;

        while(!stack.isEmpty()) {

            String currToken = toParse.substring(r, r+1);
            String top = stack.peek();
            if(terminalSymbols.contains(top) || top.equals("$")) {
                if(top.equals(currToken)) {
                    stack.pop();
                    r++;
                } else {
                    System.out.println("\nString not accepted!");
                    return;
                }
            }

            else if(nonTerminalSymbols.contains(top)) {

                if(!parsingTable.get(top).get(currToken).isEmpty()) {
                    String prod = parsingTable.get(top).get(currToken);
                    stack.pop();

                    if(prod.equals("eps"))
                        continue;

                    for(int i = prod.length() - 1; i >= 0; i--) {
                        stack.push(prod.substring(i, i+1));
                    }
                }

                else {
                    System.out.println("\nString not accepted!");
                    return;
                }
            }
        }

        System.out.println("\nYour string is accepted!");
    }

    static void eliminateLeftRecursion() {

        HashMap<String, HashSet<String>> updated = new HashMap<>();

        for(String key : productions.keySet()) {
            updated.put(key, new HashSet<>());

            for(String val : productions.get(key)) {
                updated.get(key).add(val);
            }
        }

        for(String key : productions.keySet()) {
            for(String val : productions.get(key)) {
                if(val.charAt(0) == key.charAt(0)) {
                    String newSymbol = customSymbol.toString();
                    nonTerminalSymbols.add(newSymbol);
                    customSymbol--;

                    updated.get(key).remove(val);
                    updated.put(newSymbol, new HashSet<>());
                    updated.get(newSymbol).add(val.substring(1) + newSymbol);
                    updated.get(newSymbol).add("eps");

                    HashSet<String> temp = new HashSet<>();

                    for(String str : updated.get(key))
                        temp.add(str + newSymbol);

                    updated.get(key).clear();
                    updated.get(key).addAll(temp);
                }
            }
        }

        productions = updated;

    }

    static void leftFactor() {

        HashMap<String, HashSet<String>> updated = new HashMap<>();

        for(String key : productions.keySet()) {
            updated.put(key, new HashSet<>());

            for(String val : productions.get(key)) {
                updated.get(key).add(val);
            }
        }

        for(String key : productions.keySet()) {
            HashSet<String> test = new HashSet<>(productions.get(key));
            HashSet<String> visited = new HashSet<>();
            for(String val : productions.get(key)) {
                if(val.equals("eps"))
                    continue;

                for(String str : test) {
                    if(str.equals("eps"))
                        continue;

                    if (!visited.contains(val) && !visited.contains(str) &&
                            val.charAt(0) == str.charAt(0) && !val.equals(str)) {
                        int j = 0;

                        visited.add(val);
                        visited.add(str);

                        while(j < val.length() && j < str.length() &&
                                val.charAt(j) == str.charAt(j)
                                 ) {

                            j++;
                        }

                        String commonPart = str.substring(0, j);
                        String newSymbol = customSymbol.toString();
                        nonTerminalSymbols.add(newSymbol);
                        customSymbol--;

                        updated.put(newSymbol, new HashSet<>());
                        updated.get(key).remove(str);
                        updated.get(key).remove(val);

                        if(j < val.length()) {
                            updated.get(newSymbol).add(val.substring(j));
                        } else {
                            updated.get(newSymbol).add("eps");
                        }

                        if(j < str.length()) {
                            updated.get(newSymbol).add(str.substring(j));
                        } else {
                            updated.get(newSymbol).add("eps");
                        }

                        updated.get(key).add(commonPart + newSymbol);
                    }
                }
            }
        }

        productions = updated;
    }

    static void initializeUtils() {


        for(String str : nonTerminalSymbols) {
            first.put(str, new HashSet<>());
            follow.put(str, new HashSet<>());
        }

        for(String str : nonTerminalSymbols) {
            HashMap<String, String> map = new HashMap<>();

            for(String val : terminalSymbols)
                map.put(val, "");

            map.put("$", "");

            parsingTable.put(str, map);
        }
    }

    public static void main(String[] args) {

        getInput();
        eliminateLeftRecursion();
        leftFactor();
        initializeUtils();

        for(String str : nonTerminalSymbols) {
            if(first.get(str).isEmpty())
                findFirsts(str);
        }

        follow.get("S").add("$");
        for(String str : nonTerminalSymbols) {
                findFollow(str);
        }

        makeParsingTable();
        parseLL1();
    }
}