package sample;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    @FXML
    private TextField word;
    @FXML
    private ListView<String> listWord;
    private ListView<String> lisCheck;
    @FXML
    private WebView definitionWord;
    private WebEngine webEngine;
    @FXML
    private Button search;
    static Map<String, String> myTranslate = new TreeMap<String, String>();
    static final String DATA_FILE_PATH = "C:\\Users\\ASUS\\IdeaProjects\\dictionaryyyyy\\src\\sample\\E_V.txt";
    static final String SPLITTING_CHARACTERS = "<html>";

    public void insertFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE_PATH));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            String[] words = currentLine.split(SPLITTING_CHARACTERS);
            String new_word = words[0];
            String word_explain = SPLITTING_CHARACTERS + words[1];
            myTranslate.put(new_word, word_explain);
        }
        reader.close();
    }
    public String dictionaryLookup(String key) {
        if (myTranslate.containsKey(key))
            return myTranslate.get(key);
        return "Not Found";
    }
    @FXML
    private ObservableList<String> observableList = FXCollections.observableList(new ArrayList<String>());
    public void submit(ActionEvent event){
        String textSearch = word.getText();
        Pattern pattern = Pattern.compile("\\b"+textSearch, Pattern.CASE_INSENSITIVE);
        int check = 0;
        for(String key : myTranslate.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if(matcher.find()){
                System.out.println(key);
                if(!observableList.isEmpty() && check == 0) {
                    observableList.clear();
                    check =1;
                }
                observableList.add(key);
                listWord.setItems(observableList);
            }
        }
        listWord.setOnMouseClicked(event1 -> {
            webEngine = definitionWord.getEngine();
            String html ="<html><h1>Hello</h1><html>";
            String key = listWord.getSelectionModel().getSelectedItem();
            String content = dictionaryLookup(key);
            webEngine.loadContent(content);
        });
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Dictionary");
        primaryStage.setScene(new Scene(root, 1077, 552));
        primaryStage.show();
        insertFromFile();
        //search.setOnAction(event1 -> {
        //    System.out.println(word.getText());
        //});
    }
    public static void main(String[] args) {
        launch(args);
    }
}
