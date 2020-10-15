package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    @FXML
    private TextField word;
    @FXML
    private ListView<String> listWord;
    @FXML
    private WebView definitionWord;
    private WebEngine webEngine;

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
    public void submit(ActionEvent event){
        String textSearch = word.getText();
        System.out.println(textSearch);
        Pattern pattern = Pattern.compile("\\b"+textSearch, Pattern.CASE_INSENSITIVE);
        for(String key : myTranslate.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if(matcher.find()){
                System.out.println(key);
                listWord.getItems().addAll(key);
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
    }
    public static void main(String[] args) {
        launch(args);
    }
}
