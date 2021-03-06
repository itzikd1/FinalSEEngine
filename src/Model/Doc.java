package Model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Doc implements Serializable {

    private String docName;
    private String docNo;
    private String Date;
    private String T1;
    private String text;
    private String city;
    private String docLangauge;
    private int mostFrequentTermValue;
    private int numberOfDifferentWords;
    private int documentLength;
    private LinkedHashMap<String, Integer>[] docTerms;

    public Doc(String docName, String text, String DocNo) {
        this.docName = docName;
        this.text = text;
        this.docNo = DocNo;
        this.docTerms = new LinkedHashMap[27];
        this.city = "";
        this.documentLength = 0;
        this.docLangauge = "";


    }

    public void freeDocTerms() {
        for (int i = 0; i < docTerms.length; i++)
            docTerms[i].clear();
    }

    //<editor-fold desc="Getter and Setter">
    public int getDocumentLength() {
        return documentLength;
    }

    public LinkedHashMap<String, Integer>[] getDocTerms() {
        return docTerms;
    }

    public void setDocTerms(LinkedHashMap<String, Integer>[] fromDocTerms) {
        this.docTerms = fromDocTerms;
    }

    public String getCity() {
        return city;
    }

    public String getDocName() {
        return docName;
    }

    public String getDocNo() {
        return docNo;
    }

    public String getDate() {
        return Date;
    }

    public String getT1() {
        return T1;
    }

    public String getText() {
        return text;
    }

    public String getDocLangauge() {
        return docLangauge;
    }

    public void setDocLanguage(String docLangauge) {
        this.docLangauge = docLangauge;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setT1(String t1) {
        T1 = t1;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMostFrequentTermValue() {
        return mostFrequentTermValue;
    }

    public int getNumberOfDifferentWords() {
        return numberOfDifferentWords;
    }

    public void setDocumentLength() {
        int ans = 0;
        for (LinkedHashMap<String, Integer> x : docTerms) {
            for (Integer value : x.values())
                ans += value;
        }
        this.documentLength = ans;
    }

    public void setNumberOfDifferentWords() {
        int ans = 0;
        for (LinkedHashMap<String, Integer> x : docTerms) {
            ans += x.size();
        }
        this.numberOfDifferentWords = ans;
    }

    public void setDocCountry(String docCity) {
        this.city = docCity;
    }

    public void setMostFrequentTermValue() {
        int max = 0;
        for (LinkedHashMap<String, Integer> x : docTerms) {
            if (x.size() > 0) {
                Comparator<Map.Entry<String, Integer>> comparator =
                        new Comparator<Map.Entry<String, Integer>>() {
                            public int compare(
                                    Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                                return e1.getValue().compareTo(e2.getValue());
                            }
                        };
                max = Math.max(Collections.max(x.entrySet(), comparator).getValue(), max);
            }

        }
        this.mostFrequentTermValue = max;
    }
//</editor-fold>
}
