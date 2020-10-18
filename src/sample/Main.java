package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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
    static final String DATA_FILE_PATH = "D:\\clone\\WebDic\\src\\sample\\E_V.txt";
    static final String SPLITTING_CHARACTERS = "<html>";
    private ObservableList<String> observableList = FXCollections.observableList(new ArrayList<String>());

    public static void main(String[] args) {
        launch(args);
    }

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
        if (!word.getText().isEmpty()){
            String textSearch = word.getText();
            Pattern pattern = Pattern.compile("\\b"+textSearch, Pattern.CASE_INSENSITIVE);
            if(!observableList.isEmpty()) {
                observableList.clear();
            }
            for(String key : myTranslate.keySet()) {
                Matcher matcher = pattern.matcher(key);
                if(matcher.find()){
                    System.out.println(key);
                    observableList.add(key);
                    listWord.setItems(observableList);
                }
            }
        }
    }

    public void delete(ActionEvent event) {
            String key = listWord.getSelectionModel().getSelectedItem();
            if (!key.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete word.");
                alert.setHeaderText("Are you sure you want to remove this word?");
                alert.setContentText(key);

                Optional<ButtonType> optionalButtonType = alert.showAndWait();
                Alert child_alert = new Alert(Alert.AlertType.INFORMATION);
                if (optionalButtonType.get() == ButtonType.OK) {
                    myTranslate.remove(key);
                    observableList.remove(key);
                    webEngine = definitionWord.getEngine();
                    webEngine.loadContent("");

                    child_alert.setHeaderText("Delete Successfully!");
                    child_alert.showAndWait();
                }
            }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Dictionary");
        Scene scene = new Scene(root, 1077, 552);
        primaryStage.setScene(scene);
        primaryStage.show();
        initComponents(scene);
        insertFromFile();

        loadWordOnList();

    }

    public void initComponents(Scene scene) {
        definitionWord = (WebView) scene.lookup("#definitionWord");
        listWord = (ListView<String>) scene.lookup("#listWord");
    }
    
    public void loadWordOnList() {
            listWord.setOnMouseClicked(event1 -> {
                String key = listWord.getSelectionModel().getSelectedItem();
                if (!key.isEmpty()){
                    webEngine = definitionWord.getEngine();
                    String content = dictionaryLookup(key);
                    webEngine.loadContent(content);
                }

            });

    }
}
