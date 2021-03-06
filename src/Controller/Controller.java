package Controller;

import Model.Excpetions.BadPathException;
import Model.Excpetions.SearcherException;
import Model.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Controller {
    private Model model;
    private static Controller singleton = null;

    private Controller() {
        this.model = Model.getInstance();
    }

    public static Controller getInstance() {
        if (singleton == null)
            singleton = new Controller();
        return singleton;
    }

    public List<String> getQResult() throws IOException, BadPathException {
        return model.getQResult();
    }

    public void mergePartialPosting(String workPath, String savePath) {
        model.mergePartialPosting(workPath, savePath);
    }

    public void parse(String workPath, String savePath, boolean checkbox_value) throws SearcherException, IOException {
        model.parse(workPath, savePath, checkbox_value);
    }

    public void resetButton(String savePath) throws SearcherException {
        model.resetButton(savePath);
    }

    public void showDic(String savePath, Boolean checkbox_stemming) throws IOException {
        model.showDic(savePath, checkbox_stemming);
    }

    public void loadDic(String savePath, boolean checkbox_stemming) throws SearcherException, IOException {
        model.loadDic(savePath, checkbox_stemming);
    }

    public void merg_func(String workPath, String savePath) {
        model.merg_func(workPath, savePath);
    }

    public void writeLastDocsToDisk(String savePath) {
        model.writeLastDocsToDisk(savePath);
    }

    public int getNumberOfIndexed() {
        return model.getNumberOfIndexed();
    }

    public int getDicSize() {
        return model.getDicSize();
    }

    public void runQueryFile(String queryText, String workPath, String savePath, Boolean checkbox_semantic, boolean checkbox_value, List<String> chosenCities, boolean tosave, String savefolder) throws IOException, BadPathException {
        model.runQueryFile(queryText, workPath, savePath, checkbox_semantic, checkbox_value, chosenCities, tosave, savefolder);
    }

    public HashMap<String, String> getCountryList() {
        return model.getCountrList();
    }

    public List<String> loadCity(String savePath) throws IOException {
        return model.loadCity(savePath);
    }

    public void runQueryString(String queryText, String workPath, String savePath, boolean checkbox_semantic, boolean checkbox_stemming, List<String> chosenCities, boolean tosave, String saveFolder) throws IOException, BadPathException {
        model.runQueryString(queryText, workPath, savePath, checkbox_semantic, checkbox_stemming, chosenCities, tosave, saveFolder);
    }

    public void writeEntitiesToDisk(String workPath, String savePath) {
        model.writeEntitiesToDisk(workPath, savePath);
    }

    public HashMap<String, String> getEntities(String savePath) throws IOException {
        return model.getEntities(savePath);
    }

    public HashMap<String, String> getlanguageDocList() {
        return model.getlanguageDocList();
    }

    public List<String> loadLang(String savepath) throws IOException {
        return model.loadLang(savepath);
    }
}