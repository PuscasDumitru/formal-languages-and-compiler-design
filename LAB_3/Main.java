import com.sun.source.tree.UsesTree;

import java.util.*;


public class Main {

    static HashMap<String, HashSet<String>> productions = new HashMap<>();
    static HashSet<String> terminalSymbols = new HashSet<>();
    static HashSet<String> nonTerminalSymbols = new HashSet<>();
    static Iterator<Map.Entry<String, HashSet<String>>> itr;
    static char customSymbol = 'Z';

    static void getInput() {

        Scanner sc = new Scanner(System.in);

        String s;

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
    }

    static String findEpsilon() {

            for (String set : productions.keySet()) {
                for (String str : productions.get(set)) {
                    if (str.equals("eps")) {
                        productions.get(set).remove(str);
                        return set;
                    }
                }
            }
        return "";
    }

    static void removeEpsilon() {

        HashMap<String, HashSet<String>> temp;

        while(true) {

            String key = findEpsilon();

            if(key.equals(""))
                return;

            itr = productions.entrySet().iterator();
            temp = new HashMap<>();
            while(itr.hasNext()) {
                Map.Entry<String, HashSet<String>> entry = itr.next();

                for(String str : entry.getValue()) {
                    if (str.contains(key)) {
                        if(!temp.containsKey(entry.getKey()))
                            temp.put(entry.getKey(), new HashSet<>());

                        temp.get(entry.getKey()).addAll(makeNewProductionsFromEpsilon(str, key));
                    }
                }
            }

            for(String set : temp.keySet())
                productions.get(set).addAll(temp.get(set));

        }
    }

    static HashSet<String> makeNewProductionsFromEpsilon(String s, String key) {


        StringBuilder sb;
        HashSet<String> set = new HashSet<>();

        int[] arr = new int[s.length() - s.replace(key, "").length()];
        int ind = 0;

        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == key.charAt(0))
                arr[ind++] = i;
        }

        for(int len = 2; len < arr.length; len++) {

            for(int i = 0; i < arr.length; i++) {

                if(i + len > arr.length)
                    break;

                int[] test = arr.clone();

                while(test[arr.length-1] != -1) {

                    sb = new StringBuilder(s);

                    sb.replace(test[i], test[i]+1, " ");
                    int cnt = 1;

                    for(int j = i+1; j < arr.length; j++) {

                        if(test[j] != -1) {
                            sb.replace(test[j], test[j] + 1, " ");
                            cnt++;
                        }

                        if(cnt == len) {
                            test[j] = -1;
                            break;
                        }
                    }

                    set.add(sb.toString().replace(" ", ""));
                }
            }
        }

        for(int i = 0; i < arr.length; i++) {
            sb = new StringBuilder(s);
            sb.replace(arr[i], arr[i]+1, "");
            set.add(sb.toString());
        }

        if(s.replace(key, "").length() == 0)
            set.add("eps");
        else
            set.add(s.replace(key, ""));

        return set;
    }

    static void removeRenamings() {

        HashMap<String, HashSet<String>> temp;

        while(true) {

            itr = productions.entrySet().iterator();
            temp = new HashMap<>();
            while(itr.hasNext()) {
                Map.Entry<String, HashSet<String>> entry = itr.next();

                for(String str : entry.getValue()) {
                    if (productions.containsKey(str)) {
                        if(!temp.containsKey(entry.getKey()))
                            temp.put(entry.getKey(), new HashSet<>());

                        temp.get(entry.getKey()).add(str);
                    }
                }
            }

            if(temp.size() == 0)
                return;

            for(String key : temp.keySet()) {
                for(String str : temp.get(key)) {
                    productions.get(key).addAll(productions.get(str));
                    productions.get(key).remove(str);
                }
            }

        }
    }

    static void removeInaccessible() {

        itr = productions.entrySet().iterator();
        boolean flag;

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();

            if(entry.getKey().equals("S"))
                continue;

            flag = findUsage(entry.getKey());

            if(!flag)
                itr.remove();
        }
    }

    static boolean findUsage(String s) {

        for(String key : productions.keySet()) {
            for(String str : productions.get(key)) {
                if(str.contains(s))
                    return true;
            }
        }
        return false;
    }

    static void removeNonProductive() {

        StringBuilder productiveSymbols = new StringBuilder();
        HashMap<String, HashSet<String>> temp = new HashMap<>();

        for(String str : terminalSymbols)
            productiveSymbols.append(str);

        itr = productions.entrySet().iterator();

        collectProductive(productiveSymbols);

        itr = productions.entrySet().iterator();

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();
            temp.put(entry.getKey(), new HashSet<>());
            for (String str : entry.getValue()) {
                if(!str.matches("[" + productiveSymbols + "]" + "+")) {
                    temp.get(entry.getKey()).add(str);
                }
            }
        }

        itr = temp.entrySet().iterator();

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();

            for (String str : entry.getValue()) {
                productions.get(entry.getKey()).remove(str);
            }
        }

        itr = productions.entrySet().iterator();

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();

            if(entry.getValue().isEmpty())
                itr.remove();
        }

    }

    static void collectProductive(StringBuilder productiveSymbols) {

        boolean flag;

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();

            if(productiveSymbols.indexOf(entry.getKey()) == -1) {
                for (String str : entry.getValue()) {

                    if (str.matches("[^[A-Z]]+")) { // consists only of terminal symbols
                        productiveSymbols.append(entry.getKey());
                        break;
                    }
                }
            }
        }

        itr = productions.entrySet().iterator();

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();
            flag = false;

            if(productiveSymbols.indexOf(entry.getKey()) == -1) {
                for (String str : entry.getValue()) {
                    if(str.matches("[" + productiveSymbols + "]" + "+")) {
                        flag = true;
                        break;
                    }
                }

                if(flag)
                    productiveSymbols.append(entry.getKey());
            }
        }
    }

    static void obtainChomsky() {

        HashMap<String, String> map = findTerminals();
        HashMap<String, HashSet<String>> temp = new HashMap<>();

        for(String str : productions.keySet())
            temp.put(str, new HashSet<>());

        for(String str : map.keySet()) {
            temp.put(map.get(str), new HashSet<>());
            temp.get(map.get(str)).add(str);
        }

        itr = productions.entrySet().iterator();
        replaceLowerChars(temp, map);

        map = new HashMap<>();
        itr = productions.entrySet().iterator();

        for(String str : productions.keySet())
            temp.put(str, new HashSet<>());

        replaceTooLong(temp, map);
    }

    static void replaceLowerChars(HashMap<String, HashSet<String>> temp, HashMap<String, String> map) {

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();
            HashSet<String> set = new HashSet<>();
            for(String str : entry.getValue()) {
                if(str.matches("[A-Z[^A-Z]]{2,}")) {
                    String s = str;
                    for(String str1 : terminalSymbols) {
                        s = s.replace(str1, map.get(str1));
                    }

                    set.add(str);
                    temp.get(entry.getKey()).add(s);
                }
            }

            for(String str : set) {
                productions.get(entry.getKey()).remove(str);
            }
        }

        itr = temp.entrySet().iterator();

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();

            if(!productions.containsKey(entry.getKey())) {
                productions.put(entry.getKey(), entry.getValue());
            } else {
                productions.get(entry.getKey()).addAll(entry.getValue());
            }
        }
    }

    static void replaceTooLong(HashMap<String, HashSet<String>> temp, HashMap<String, String> map) {

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();
            HashSet<String> set = new HashSet<>();
            String s;

            for(String str : entry.getValue()) {

                if(str.length() > 2) {
                    s = str;
                    String toReplace;

                    while(s.length() != 2) {
                        toReplace = s.substring(0, 2);

                        if(!map.containsKey(toReplace)) {
                            map.put(toReplace, "" + customSymbol--);
                        }

                        s = s.replace(toReplace, map.get(toReplace));
                    }

                    temp.get(entry.getKey()).add(s);
                    set.add(str);
                }
            }

            for(String str : set) {
                productions.get(entry.getKey()).remove(str);
            }
        }

        for(String str : map.keySet()) {
            productions.put(map.get(str), new HashSet<>());
            productions.get(map.get(str)).add(str);
        }

        itr = temp.entrySet().iterator();

        while(itr.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = itr.next();
            productions.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    static HashMap<String, String> findTerminals() {

        boolean flag;
        HashMap<String, String> map = new HashMap<>();

        for(String str : terminalSymbols) {
            itr = productions.entrySet().iterator();
            flag = false;

            while(itr.hasNext()) {
                Map.Entry<String, HashSet<String>> entry = itr.next();

                if(entry.getValue().size() == 1) {
                    for(String str1 : entry.getValue()) {
                        if(str1.equals(str)) {
                            flag = true;
                            map.put(str, entry.getKey());
                            break;
                        }
                    }
                }
            }

            if(!flag) {
                map.put(str, "" + customSymbol--);
            }
        }

        return map;
    }

    static void printProductions() {
        StringBuilder sb = new StringBuilder();
        for(String key : productions.keySet()) {
            sb.append("\n" + key).append(" -> ");
            for(String val : productions.get(key)) {
                if(val.length() > 0)
                   sb.append(val).append(" | ");
            }
            sb.replace(sb.length() - 2, sb.length(), "");
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {

        getInput();
        removeEpsilon();
        System.out.println("\n\n1) The production set after removing the epsilon productions: ");
        printProductions();

        removeRenamings();
        System.out.println("\n\n2) The production set after removing the renamings: ");
        printProductions();

        removeInaccessible();
        System.out.println("\n\n3) The production set after removing the inaccessible symbols: ");
        printProductions();

        removeNonProductive();
        System.out.println("\n\n4) The production set after removing the nonproductive productions: ");
        printProductions();

        obtainChomsky();
        System.out.println("\n\n5) The production set after removing the productions that violate the CNF: ");
        printProductions();

    }
}