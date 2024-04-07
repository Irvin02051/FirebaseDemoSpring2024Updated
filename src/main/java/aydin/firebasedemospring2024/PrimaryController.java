package aydin.firebasedemospring2024;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrimaryController {
    @FXML
    private TextField ageTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private Button readButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button switchSecondaryViewButton;

    @FXML
    private Button writeButton;

    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    void initialize() {

        AccessDataView accessDataViewModel = new AccessDataView();
        nameTextField.textProperty().bindBidirectional(accessDataViewModel.personNameProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }


    @FXML
    void readButtonClicked(ActionEvent event) {
        readFirebase();
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        registerUser();
    }


    @FXML
    void writeButtonClicked(ActionEvent event) {
        addData();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        DemoApp.setRoot("secondary");
    }
    public boolean readFirebase() {
        outputTextArea.clear();
        key = false;

        // Asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = DemoApp.fstore.collection("Person").get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (documents.size() > 0) {
                System.out.println("Getting (reading) data from firebase database....");
                listOfUsers.clear();
                for (QueryDocumentSnapshot document : documents) {
                    String name = String.valueOf(document.getData().get("Name"));
                    int age = Integer.parseInt(document.getData().get("Age").toString());
                    String phoneNumber = String.valueOf(document.getData().get("Phone number")); // Get phone number

                    outputTextArea.setText(outputTextArea.getText() + name + " , Age: " + age + ", Phone number: " + phoneNumber + "\n");

                    System.out.println(document.getId() + " => " + name);
                    Person person = new Person(name, age, phoneNumber); // Create Person object with phone number
                    listOfUsers.add(person);
                }
            } else {
                System.out.println("No data");
            }
            key = true;

        } catch (InterruptedException | ExecutionException | NumberFormatException ex) {
            ex.printStackTrace();
        }
        return key;
    }

    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail("user222@example.com")
                .setEmailVerified(false)
                .setPassword("secretPassword")
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
            + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error creating a new user in the firebase");
            return false;
        }

    }

    public void addData() {

        DocumentReference docRef = DemoApp.fstore.collection("Person").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameTextField.getText());
        data.put("Age", ageTextField.getText());
        data.put("Phone number", phoneNumberTextField.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
        nameTextField.clear();
        ageTextField.clear();
        phoneNumberTextField.clear();
        System.out.println("Wrote to the database");
    }
}