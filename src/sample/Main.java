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
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private WebEngine webEngine;
    @FXML
    private TextField word;
    @FXML
    private ListView<String> listWord;
    @FXML
    private WebView definitionWord;
    @FXML
    private TextField addedWord;
    @FXML
    private TextArea meaningText;
    @FXML
    private TextField editWord;
    @FXML
    private TextArea editMeaning;

    static Map<String, String> myTranslate = new TreeMap<String, String>();
    static final String DATA_FILE_PATH = "D:\\clone\\WebDic\\src\\sample\\E_V.txt";
    static final String SPLITTING_CHARACTERS = "<html>";
    private ObservableList<String> observableList = FXCollections.observableList(new ArrayList<String>());
    private ObservableList<String> beginListView = FXCollections.observableList(new ArrayList<String>());

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * lấy dữ liệu từ điển từ file.
     */
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

    /**
     * tìm kiếm chính xác nghĩa của 1 từ tiếng Anh.
     */
    public String dictionaryLookup(String key) {
        if (myTranslate.containsKey(key))
            return myTranslate.get(key);
        return "Not Found";
    }

    /**
     * tìm kiếm gần đúng một từ.
     */
    public void submit(ActionEvent event) {
        if (!word.getText().isEmpty()) {
            String textSearch = word.getText();
            Pattern pattern = Pattern.compile("\\b" + textSearch, Pattern.CASE_INSENSITIVE);
            if (!observableList.isEmpty()) {
                observableList.clear();
            }
            for (String key : myTranslate.keySet()) {
                Matcher matcher = pattern.matcher(key);
                if (matcher.find()) {
                    System.out.println(key);
                    observableList.add(key);
                }
            }
            listWord.setItems(observableList);
        }
    }

    /**
     * xóa một từ tiếng Anh khỏi từ điển.
     */
    public void delete(ActionEvent event) {
        String key = listWord.getSelectionModel().getSelectedItem();
        if (!key.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete word.");
            alert.setHeaderText("Are you sure you want to remove this word?");
            alert.setContentText(key);

            Optional<ButtonType> optionalButtonType = alert.showAndWait();
            Alert child_alert = new Alert(Alert.AlertType.INFORMATION);
            if (optionalButtonType.get() == ButtonType.OK) {
                myTranslate.remove(key);

                reLoadListView(key);

                child_alert.setHeaderText("Delete Successfully!");
                child_alert.showAndWait();
            }
        }
    }

    /**
     * mở hành động sửa dữ liệu của 1 từ tiếng Anh.
     */
    public void edit(ActionEvent event) throws IOException {

        String key = listWord.getSelectionModel().getSelectedItem();
        if (!key.isEmpty()) {
            /**
             * tạo một cửa sổ mới để sửa từ. ***********************
             */
            Stage secondStage = new Stage();
            Parent root1 = FXMLLoader.load(getClass().getResource("editor.fxml"));
            Scene secondScene = new Scene(root1);
            secondStage.setScene(secondScene);
            secondStage.setTitle("Editor");

            secondStage.show();
            editWord = (TextField) secondScene.lookup("#editWord");
            editMeaning = (TextArea) secondScene.lookup("#editMeaning");
            /********************************************************/
            editWord.setText(key);
            editMeaning.setText(myTranslate.get(key));

            myTranslate.remove(key);
            reLoadListView(key);
        }
    }

    /**
     * lưu lại hành động sửa từ.
     */
    public void saveEdit(ActionEvent event) {
        if (!editWord.getText().isEmpty() && !editMeaning.getText().isEmpty()) {
            String k = editWord.getText();
            String v = editMeaning.getText();
            myTranslate.put(k, v);
            beginListView.add(k);

            Alert editingInfo = new Alert(Alert.AlertType.INFORMATION);
            editingInfo.setContentText("Edit successfully!");
            editingInfo.showAndWait();
        }
    }

    /**
     * mở hành động thêm một từ tiếng Anh vào từ điển.
     */
    public void addWord(ActionEvent event) throws IOException {
        /**
         * tạo 1 cửa sổ mới để thêm từ tiếng Anh.***************
         */
        Stage secondStage = new Stage();
        Parent root1 = FXMLLoader.load(getClass().getResource("child.fxml"));
        Scene secondScene = new Scene(root1);
        secondStage.setScene(secondScene);
        secondStage.setTitle("Add a word");
        secondStage.show();
        /**
         * *****************************************************
         */
    }

    /**
     * lưu hành động sửa.
     */
    public void saveAdd(ActionEvent event) {
        if (!addedWord.getText().isEmpty() && !meaningText.getText().isEmpty()) {
            String key = addedWord.getText();
            String value = meaningText.getText();
            myTranslate.put(key, value);
            observableList.add(key);

            Alert addingInfo = new Alert(Alert.AlertType.INFORMATION);
            addingInfo.setContentText("Add successfully!");
            addingInfo.showAndWait();
        }
    }

    /**
     * hiển thị toàn bộ danh sách từ điển.
     */
    public void showAllWords() {
        beginListView.setAll(myTranslate.keySet());
        listWord.setItems(beginListView);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Dictionary");
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
        insertFromFile();

        initComponents(scene);
        loadWordOnList();

    }

    public void initComponents(Scene scene) {
        definitionWord = (WebView) scene.lookup("#definitionWord");
        listWord = (ListView<String>) scene.lookup("#listWord");
    }

    /**
     * lấy nghĩa của từ khi click chuột vào từ.
     */
    public void loadWordOnList() {
        listWord.setOnMouseClicked(event1 -> {
            String key = listWord.getSelectionModel().getSelectedItem();
            if (!key.isEmpty()) {
                webEngine = definitionWord.getEngine();
                String content = dictionaryLookup(key);
                webEngine.loadContent(content);
            }

        });

    }

    /**
     * cập nhật lại list view khi xóa một từ.
     */
    public void reLoadListView(String key) {
        observableList.remove(key);
        beginListView.remove(key);
        webEngine = definitionWord.getEngine();
        webEngine.loadContent("");
    }
}
