package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

public class Searcher {
    private Map<String, Integer> termsQuery;
    boolean stemmer = false;
    boolean finishedParsingQ;
    private int i;
    private static HashSet<String> stopWords;
    String queryText;
    String workpath;
    String savepath;

    public Searcher(String workpath, String savepath, boolean stemmer) {
        this.stemmer = stemmer;
        i = 0;
        finishedParsingQ = false;
        stopWords = new HashSet<>();
        this.workpath = workpath;
        this.savepath = savepath;

    }

    /**
     * load stop words function
     * @param workpath - where function file is
     */
    public void stopWordsFunc(String workpath) {
        try {
            Scanner textFile = new Scanner(new File(this.workpath + "\\corpus\\stop_words.txt"));
            while (textFile.hasNext()) {
                String newStopWord = textFile.next().trim();
                // now dictionary is not recreated each time
                stopWords.add(newStopWord);
                stopWords.add(newStopWord.substring(0, 1).toUpperCase() + newStopWord.substring(1));
            }
            textFile.close();
        } catch (FileNotFoundException e) {
        }
    }

    /**
     * parse query function
     * @param q
     * @param stemming
     * @return
     */
    public Query parse(Query q, boolean stemming) {
        this.stemmer = stemming;
        stopWordsFunc(this.workpath);
        this.queryText = q.getQueryTitle();
        termsQuery = new HashMap<>();
        String first = "";
        String second = "";
        String third = "";
        String fourth = "";
        String month = null;
        String length = null;
        String charToDel = "~`!@#^&*(){}|+=[]';:?";
        charToDel += '"';
        String pat = "[" + Pattern.quote(charToDel) + "]";
        String removeChars = queryText.replaceAll(pat, " ");
        String[] words = removeChars.split("\\s+");
        int count = 0;


        for (i = 0; i < words.length; i++) {

            while (words[i].length() > 0 && (!((words[i].charAt(0) >= '0' && words[i].charAt(0) <= '9') || (words[i].charAt(0) >= 'a' && words[i].charAt(0) <= 'z')
                    || (words[i].charAt(0) >= 'A' && words[i].charAt(0) <= 'Z') || words[i].charAt(0) == '$')))
                words[i] = words[i].substring(1);
            int idx = words[i].length() - 1;
            while (words[i].length() > 0 && (!((words[i].charAt(idx) >= '0' && words[i].charAt(idx) <= '9') || (words[i].charAt(idx) >= 'a' && words[i].charAt(idx) <= 'z')
                    || (words[i].charAt(idx) >= 'A' && words[i].charAt(idx) <= 'Z') || words[i].charAt(idx) == '%'))) {
                words[i] = words[i].substring(0, words[i].length() - 1);
                idx = words[i].length() - 1;
            }
        }
        for (i = 0; i < words.length + 4; i++) {
            try {
                fourth = words[i + 3];
            } catch (Exception e) {
                fourth = "";
            }
            try {
                third = words[i + 2];
            } catch (Exception e) {
                third = "";
            }
            try {
                second = words[i + 1];
            } catch (Exception e) {
                second = "";
            }
            try {
                first = words[i];
            } catch (Exception e) {
//                finishedParsingQ = false;
                q.setTerms(termsQuery);
                return q;
            }

            if (containDigit(words[i])) {
                if (first.charAt(0) == '$' ||
                        second.equals("dollars") || second.equals("Dollars") ||
                        third.equals("dollars") || third.equals("Dollars") ||
                        fourth.equals("dollars") || fourth.equals("Dollars")) {
                    parseToDollars(first, second, third, fourth);
                } else if (first.charAt(first.length() - 1) == '%' ||
                        (second.equals("percent") || second.equals("percentage"))) {
                    parseToPercents(first, second);
                } else if (isInteger(first)) {
                    String term = wordToNumber(second);
                    if (term != null) {
                        addToTerms(first + term);
                        i++;

                    }


                    //Dates

                    else if ((month = isMonth(second)) != null) {//14 may -> 05-14
                        if (first.length() < 3) {
                            addToTerms(month + "-" + first);
                            i++;

                        }
                    } else {
                        String temp = first.replaceAll(",", "");
                        try {
                            double x = (Double.parseDouble(temp));
                            if (x < 1000)
                                addToTerms(temp);
                            else if (x > 999 && x < 1000000)
                                addToTerms(x / 1000 + "K");
                            else if (x >= 1000000 && x <= 999999999)
                                addToTerms(x / 1000000 + "M");
                            else
                                addToTerms(x / 1000000000 + "M");
                        } catch (Exception e) {

                            addToTerms(first);
                        }
                    }

                } else if (isFloat(first)) {
                    first = changeFloatToTerm(first);
                    addToTerms(first);

                } else if ((length = isLength(first)) != null) {
                    addToTerms(length);

                } else {
                    addToTerms(first);
                }
            }//contain digit

            else if (((first.equals("between") || words[i].equals("Between")) &&
                    isInteger(second) && isInteger(fourth) && third.equals("and"))) {

                addToTerms(first + second + third + fourth);
                i += 3;


            } else if ((month = isMonth(first)) != null) {
                // MM YY
                if (Pattern.matches("[0-9]+", second) && second.length() > 2) {
                    addToTerms(second + "-" + month);
                    i++;

                }
                //MM DD
                else if (Pattern.matches("[0-9]+", second) && second.length() <= 2) {
                    addToTerms(month + "-" + second);
                    i++;
                }
            } else if (containsSlash(first) != null) {
                ArrayList<Integer> positions;
                positions = containsSlash(first);
                int begin = 0;
                int end = 0;
                for (int i = 0; i < positions.size(); i++) {
                    end = positions.get(i);
                    addToTerms(first.substring(begin, end));
                    begin = end + 1;
                }

                addToTerms(first.substring(begin));
                addToTerms(first);

            } else if (containsMakaf(first) != null) {
                ArrayList<Integer> positions;
                positions = containsMakaf(first);
                int begin = 0;
                int end = 0;
                for (int i = 0; i < positions.size(); i++) {
                    end = positions.get(i);
                    addToTerms(first.substring(begin, end));
                    begin = end + 1;
                }

                addToTerms(first.substring(begin));
                addToTerms(first);

            } else {
                addToTerms(first);
            }
        }
        return q;

    }

    private ArrayList<Integer> containsMakaf(String first) {
        ArrayList<Integer> positions = new ArrayList<>();
        for (int i = 1; i < first.length() - 1; i++) {
            if (first.charAt(i) == '-')
                positions.add(i);
        }
        if (positions.size() != 0)
            return positions;
        return null;
    }

    private ArrayList<Integer> containsSlash(String first) {
        ArrayList<Integer> positions = new ArrayList<>();
        for (int i = 1; i < first.length() - 1; i++) {
            if (first.charAt(i) == '/')
                positions.add(i);
        }
        if (positions.size() != 0)
            return positions;
        return null;
    }

    private String isLength(String first) {
        String term = first.replaceAll("[,]", "");
        if (Pattern.matches("[0-9]+-kilometers", term) || Pattern.matches("[0-9]+-Kilometers", term) ||
                Pattern.matches("[0-9]+-kilometer", term) || Pattern.matches("[0-9]+-Kilometer", term)) {
            for (int i = 0; i < first.length(); i++) {
                if (first.charAt(i) == '-')
                    return first.substring(0, i) + "-" + "km";
            }

        } else if (Pattern.matches("[0-9]+-centimeters", term) || Pattern.matches("[0-9]+-centimeter", term) ||
                Pattern.matches("[0-9]+-Centimeters", term) || Pattern.matches("[0-9]+-Centimeter", term)) {
            for (int i = 0; i < first.length(); i++) {
                if (first.charAt(i) == '-')
                    return first.substring(0, i) + "-" + "cm";
            }
        }
        return null;
    }

    private String changeFloatToTerm(String first) {
        if (first.length() > 4) {
            if (first.charAt(0) == '.' || first.charAt(1) == '.' || first.charAt(2) == '.' || first.charAt(3) == '.') {
                return first;
            } else {
                if (first.length() > 5) {
                    int f = 0;
                    for (int i = 4; i < first.length(); i++) {
                        if (first.charAt(i) == '.') {
                            f = i;
                            break;
                        }
                    }
                    return first.substring(0, f - 3) + '.' + first.substring(f - 3, f) + first.substring(f + 1) + 'K';
                }
            }
        }
        return first;
    }

    private boolean isFloat(String first) {
        int countPoints = 0;
        for (int i = 0; i < first.length(); i++) {
            if (!((first.charAt(i) >= '0') && first.charAt(i) <= '9'))
                if (!(first.charAt(i) == '.')) {
                    return false;
                } else
                    countPoints++;
        }
        if (countPoints != 1)
            return false;
        return true;
    }

    private boolean parseToPercents(String first, String second) {
        if (first.charAt(first.length() - 1) == '%' && isInteger(first.substring(0, first.length() - 1))) {
            addToTerms(first);
            return true;
        } else if (isInteger(first) && (second.equals("percentage") || second.equals("percent"))) {
            addToTerms(first + '%');
            i++;
            return true;
        } else {
            addToTerms(first);
        }
        return false;
    }

    private boolean parseToDollars(String first, String second, String third, String fourth) {
        String num = null;
        if (first.charAt(0) != '$') {
            if (isInteger(first)) {
                if (second.equals("thousand") || second.equals("Thousand")) {
                    if (third.equals("U.S.") || third.equals("U.S")) {
                        if (fourth.equals("dollars") || fourth.equals("Dollars")) {
                            addToTerms(first + ",000" + " Dollars");
                            i += 3;
                            return true;
                        }
                    } else if (third.equals("dollars") || third.equals("Dollars")) {
                        addToTerms(first + ",000" + " Dollars");//
                        i += 2;
                        return true;
                    }
                } else if ((num = wordToMillions(second)) != null) {
                    if (third.equals("U.S.") || third.equals("U.S")) {
                        if (fourth.equals("dollars") || fourth.equals("Dollars")) {
                            addToTerms(first + num + "Dollars");
                            i += 3;
                            return true;
                        }
                    } else if (third.equals("dollars") || third.equals("Dollars")) {
                        addToTerms(first + num + "Dollars");
                        i += 2;
                        return true;
                    }
                    //like 150 million

                } else if (second.equals("dollars") || second.equals("Dollars"))
                    addToTerms(first + " Dollars");
                else {
                    addToTerms(first);
                    return true;
                }
                //number thousand.
            } else {
                addToTerms(first);
                return true;
            }
        }
        //first is $number
        else {
            first = first.replaceAll("-", " ");
            first = first.substring(1);
            if (second.equals("thousand") || second.equals("Thousand")) {
                addToTerms(first + " Dollars");
                i++;
                return true;
            } else if ((num = wordToMillions(second)) != null) {
                addToTerms(first + num + "Dollars");
                i++;
                return true;
            } else {
                addToTerms(first + "Dollars");
                return true;
            }
        }

        return false;
    }

    private String wordToNumber(String second) {
        if (second.equals("thousand") || second.equals("Thousand"))
            return "K";
        else if (second.equals("million") || second.equals("Million"))
            return "M";
        else if (second.equals("billion") || second.equals("Billion"))
            return "B";
        else if (second.equals("trillion") || second.equals("Trillion"))
            return "000B";
        return null;
    }


    private static String isMonth(String word) {
        String[] months = new String[]{
                "Jan", "JAN", "January", "JANUARY",
                "Feb", "FEB", "February", "FEBRUARY",
                "Mar", "MAR", "March", "MARCH",
                "April", "APRIL", "APR", "Apr",
                "May", "MAY", "May", "MAY",
                "June", "JUNE", "Jun", "JUN",
                "July", "JULY", "JUL", "Jul",
                "August", "AUGUST", "AUG", "Aug",
                "September", "SEPTEMBER", "Sep", "SEP",
                "October", "OCTOBER", "OCT", "Oct",
                "November", "NOVEMBER", "NOV", "Nov",
                "December", "DECEMBER", "DEC", "Dec"
        };
        for (int i = 0; i < months.length; i++) {
            if (word.equals(months[i])) {
                if (i < 36)
                    return "0" + ((i / 4) + 1);
                else
                    return "" + ((i / 4) + 1);
            }
        }
        return null;
    }

    private void addToTerms(String term) {
        if (!stopWords.contains(term)) {
            if (!term.equals("")) {
                if (stemmer) {
                    String originalTerm = term;
                    boolean check = false;
                    Stemmer stemmer = new Stemmer();
                    if (term.charAt(0) >= 'A' && term.charAt(0) <= 'Z') {
                        term = term.toLowerCase();
                        check = true;
                    }
                    stemmer.add(term.toCharArray(), term.length());
                    stemmer.stem();
                    term = stemmer.toString();
                    if (check == true)
                        term = term.toUpperCase();
                }
            }
            int c = 0;
            if (term.charAt(0) >= 'A' && term.charAt(0) <= 'Z') {//check the lower / uppercase rule.
                int i = correctDictionaryCell(term);
                String lowerTerm = term.toLowerCase();
                if (LoadedDictionary.getDictionary()[i].get(lowerTerm) != null) {
                    if (termsQuery.get(lowerTerm) != null) {
                        c = termsQuery.get(lowerTerm);
                        termsQuery.put(lowerTerm, ++c);

                    } else {
                        termsQuery.put(lowerTerm, 1);
                    }
                } else {
                    if (termsQuery.get(term.toUpperCase()) != null) {
                        c = termsQuery.get(term.toUpperCase());
                        termsQuery.put(term.toUpperCase(), ++c);
                    } else {
                        termsQuery.put(term.toUpperCase(), 1);
                    }
                }
            } else if (term.charAt(0) >= 'a' && term.charAt(0) <= 'z') {
                if (termsQuery.get(term) == null) {
                    termsQuery.put(term, 1);
                } else {
                    c = termsQuery.get(term);
                    termsQuery.put(term, ++c);
                }
            } else {
                //number, special chars..

                if (termsQuery.get(term) != null) {
                    c = termsQuery.get(term);
                    termsQuery.put(term, ++c);
                } else {
                    termsQuery.put(term, 1);
                }
            }
        }
    }

    private static String wordToMillions(String word) {
        String[] bigNumbers = new String[]{"Thousand", "Million", "Billion", "Trillion", "thousand", "million", "billion", "trillion", "m", "b", "bn"};
        for (String number : bigNumbers) {
            if (word.equals(number)) {
                return translateWordToMillion(word);
            }
        }
        return null;
    }

    private static String translateWordToMillion(String word) {
        //if (word.equals("Thousand")||word.equals("thousand"))
        //    return ",000";
        if (word.equals("Million") || word.equals("million"))
            return " M ";
        else if (word.equals("Billion") || word.equals("billion"))
            return "000 M ";
        else if (word.equals("Trillion") || word.equals("trillion"))
            return "000000 M ";
        return null;
    }

    private static boolean containDigit(String w) {
        for (int i = 0; i < w.length(); i++) {
            if (w.charAt(i) >= '0' && w.charAt(i) <= '9')
                return true;
        }
        return false;
    }

    public static boolean isInteger(String word) {

        String ans = word.replaceAll("[,]", "");// remove ","

        if (Pattern.matches("[0-9]+", ans))
            return true;

        return false;
    }

    public static int correctDictionaryCell(String termToFind) {
        if (termToFind.charAt(0) >= 'a' && termToFind.charAt(0) <= 'z')
            return (int) termToFind.charAt(0) - 97;
        else if (termToFind.charAt(0) >= 'A' && termToFind.charAt(0) <= 'Z')
            return (int) termToFind.charAt(0) - 65;
        else
            return 26;
    }
}
