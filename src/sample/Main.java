package sample;

import com.sun.speech.freetts.VoiceManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private static WebEngine webEngine;
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

    private static Map<String, String> myTranslate = new TreeMap<String, String>();
    private static Map<String, String> exportListWords = new HashMap<String, String>();
    private static ObservableList<String> observableList = FXCollections.observableList(new ArrayList<String>());
    private static ObservableList<String> beginListView = FXCollections.observableList(new ArrayList<String>());
    private ObservableList<String> searchList = FXCollections.observableList(new ArrayList<String>());

    private static final String SPLITTING_CHARACTERS = "<html>";
    private static final String DATA_FILE_PATH = "D:\\clone_du_phong\\WebDic\\src\\file\\E_V.txt";
    private static final String EXPORT_FILE_PATH = "D:\\clone_du_phong\\WebDic\\src\\file\\download.txt";

    private static Stage editStage = new Stage();
    private static Stage addStage = new Stage();
    private static String keyRemove;

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
     * xuất dữ liệu ra file.
     */
    public void exportToFile(ActionEvent event) throws IOException {
        Alert exportQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        exportQuestion.setHeaderText("you have been searched " + exportListWords.size() + " words.\n"
                + "Do you want to export your recent searched list?");
        Optional<ButtonType> optionalButtonType = exportQuestion.showAndWait();
        if (optionalButtonType.get() == ButtonType.OK) {
            try {
                FileWriter writer = new FileWriter(EXPORT_FILE_PATH);
                for (String key : exportListWords.keySet()) {
                    writer.write(key + "\n" + exportListWords.get(key) + "\n");
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("an error occured");
                e.printStackTrace();
            }

            Alert exportInfo = new Alert(Alert.AlertType.INFORMATION);
            exportInfo.setHeaderText("Export successfully!");
            exportInfo.setContentText("you have been searched " + exportListWords.size() + " words.");
            exportInfo.showAndWait();
        }
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
     * tìm kiếm một từ.
     */
    public void submit(ActionEvent event) {
        Alert notFound = new Alert(Alert.AlertType.INFORMATION);
        if (!word.getText().isEmpty()) {
            String textSearch = word.getText();
            notFound.setHeaderText("[ " + textSearch + " ]" + " not found.");
            if (!observableList.isEmpty()) {
                observableList.clear();
            }
            if (myTranslate.containsKey(textSearch)) {
                observableList.add(textSearch);
                // TODO: add to list to export to file.
                exportListWords.put(textSearch, myTranslate.get(textSearch));
            } else {
                notFound.showAndWait();
            }
            listWord.setItems(observableList);
        } else {
            notFound.setHeaderText("Word has not been entered.");
            notFound.showAndWait();
        }
    }

    /**
     * search when press keyboard.
     */
    public void setOnKey(KeyEvent event) {
        word.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!observableList.isEmpty()) {
                observableList.clear();
            }
            String textSearch = newValue;
            System.out.println("TU CAN TIM DAY: "+textSearch+"\n Old value is: "+oldValue);
            if (textSearch == null || textSearch.isEmpty()) {
                System.out.println("null"+" " + searchList.size());
                if (!searchList.isEmpty()) {
                    searchList.clear();
                    System.out.println("sau khi clear o doan null: "+searchList.size());
                }
                return;
            }
            Pattern pattern = Pattern.compile("\\b" + textSearch, Pattern.CASE_INSENSITIVE);
            Matcher matcher;
            if (textSearch.length() == 1) {
                System.out.println("neus text search chi co mot chu cai: " + searchList.size());
                if (searchList.isEmpty()) {
                    System.out.println("-.- Dang load tu dau search list.");
                    for (String key : myTranslate.keySet()) {
                        matcher = pattern.matcher(key);
                        if (key.charAt(0)==textSearch.charAt(0) && matcher.find()) {
                            //System.out.println(key);
                            observableList.add(key);
                            searchList.add(key);
                        }
                    }
                }
                System.out.println(">0 kiem tra size cua list khi co 1 chu cai: "+searchList.size());
            } else if (textSearch.length() >= 2) {
                System.out.println("===Dang tim day, co du hon 2 chu cai roi.");
                for (String key : searchList) {
                    matcher = pattern.matcher(key);
                    if (key.charAt(0)==textSearch.charAt(0) && matcher.find()) {
                        //System.out.println(key);
                        observableList.add(key);
                    }
                }
            }
        });
        listWord.setItems(observableList);
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
                removeKeyAndReLoadListView(key);
                child_alert.setHeaderText("Delete Successfully!");
                child_alert.showAndWait();
            }
        }
    }

    /**
     * mở hành động sửa dữ liệu của 1 từ tiếng Anh.
     */
    public void edit(ActionEvent event) throws IOException {
        keyRemove = listWord.getSelectionModel().getSelectedItem();
        if (!keyRemove.isEmpty()) {
            /**
             * tạo một cửa sổ mới để sửa từ. ***********************
             */
            Parent root1 = FXMLLoader.load(getClass().getResource("editor.fxml"));
            Scene secondScene = new Scene(root1);
            editStage.setScene(secondScene);
            editStage.setTitle("Editor");
            editStage.show();
            editWord = (TextField) secondScene.lookup("#editWord");
            editMeaning = (TextArea) secondScene.lookup("#editMeaning");

            editWord.setText(keyRemove);
            //editMeaning.setText(myTranslate.get(keyRemove));
        }
    }

    // TODO: đóng cửa sổ edit.
    public void closeEdit(ActionEvent event) {
        editStage.close();
    }

    /**
     * lưu lại hành động sửa từ.
     */
    public void saveEdit(ActionEvent event) {
        if (!editWord.getText().isEmpty() && !editMeaning.getText().isEmpty()) {
            String k = editWord.getText();
            String v = editMeaning.getText();

            removeKeyAndReLoadListView(keyRemove);

            myTranslate.put(k, v);
            observableList.add(k);
            beginListView.setAll(myTranslate.keySet());

            Alert editingInfo = new Alert(Alert.AlertType.INFORMATION);
            editingInfo.setContentText("Edit successfully!");
            Optional<ButtonType> optionalButtonType = editingInfo.showAndWait();
            if (optionalButtonType.get() == ButtonType.OK) {
                editStage.close();
            }
        }
    }

    /**
     * mở hành động thêm một từ tiếng Anh vào từ điển.
     */
    public void addWord(ActionEvent event) throws IOException {
        /**
         * tạo 1 cửa sổ mới để thêm từ tiếng Anh.***************
         */
        Parent root1 = FXMLLoader.load(getClass().getResource("addWindow.fxml"));
        Scene secondScene = new Scene(root1);
        addStage.setScene(secondScene);
        addStage.setTitle("Add a word");
        addStage.show();
    }

    // TODO: đóng cửa sổ add.
    public void closeAdd(ActionEvent event) {
        addStage.close();
    }

    /**
     * lưu hành động thêm từ.
     */
    public void saveAdd(ActionEvent event) {
        if (!addedWord.getText().isEmpty() && !meaningText.getText().isEmpty()) {
            String key = addedWord.getText();
            String value = meaningText.getText();
            myTranslate.put(key, value);
            observableList.add(key);
            beginListView.setAll(myTranslate.keySet());

            Alert addingInfo = new Alert(Alert.AlertType.INFORMATION);
            addingInfo.setContentText("Add successfully!");
            Optional<ButtonType> optionalButtonType = addingInfo.showAndWait();
            if (optionalButtonType.get() == ButtonType.OK) {
                addStage.close();
            }
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
        insertFromFile();
        initComponents(scene);
        showAllWords();
        primaryStage.show();
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
    public void removeKeyAndReLoadListView(String key) {
        myTranslate.remove(key);
        observableList.remove(key);
        beginListView.remove(key);
        webEngine.loadContent("");
    }

    /**
     * phát âm.
     */
    public void Pronounce(MouseEvent mouseEvent){
        String  text = listWord.getSelectionModel().getSelectedItem();
        if(text == null || text.isEmpty()) {
            System.out.println("NULL");
            return;
        }
        System.out.println(text);
        VoiceManager voiceManager = VoiceManager.getInstance();
        com.sun.speech.freetts.Voice synthersizer = voiceManager.getVoice("kevin16");
        synthersizer.allocate();
        synthersizer.speak(text);
        synthersizer.deallocate();
    }
}
