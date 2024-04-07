package aydin.firebasedemospring2024;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import static aydin.firebasedemospring2024.DemoApp.fauth;


public class SecondaryController {
    @FXML
    void registerButtonClicked(ActionEvent event) {
        registerUser();
    }
    @FXML
    Button registerButton;
    @FXML
    TextField textFieldUsername;

    @FXML TextField textFieldPassword;

    public static Map<String, Object> retrieveUserData(String test, String collectionName) throws InterruptedException, ExecutionException {
        if (test == null || test.isEmpty()) {
            System.out.println("Person is not provided.");
            return null; // Or handle the scenario appropriately
        }

        // Reference to the document for the specified employeeID in the "employees" collection
        CollectionReference collection = DemoApp.fstore.collection(collectionName);
        DocumentReference docRef = collection.document(test);

        // Retrieve the document snapshot asynchronously
        ApiFuture<DocumentSnapshot> future = docRef.get();


        try {
            // Wait for the asynchronous task to complete
            DocumentSnapshot document = future.get();

            // Check if the document exists
            if (document.exists()) {
                // Extract data from the document
                return document.getData();
            } else {
                // Document not found
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            // Handle exceptions
            System.out.println("Error executing search: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }
    }
    public void showDialogCorrect () {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Correct Input");
        alert.setTitle("Logged in");
        alert.setContentText("You are signed in");
        Optional<ButtonType> result = alert.showAndWait();
    }
    public void showDialogIncorrect () {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Incorrect input");
        alert.setTitle("Incorrect Password");
        alert.setContentText("Fix password");
        Optional<ButtonType> result = alert.showAndWait();
    }
    public void showDialogUserDoesNotExist () {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("User does not exist");
        alert.setTitle("User does not exist");
        alert.setContentText("User does not exist");
        Optional<ButtonType> result = alert.showAndWait();
    }
    public void showDialogEmpty () {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Please fill out username and password");
        alert.setTitle("Username or password is empty");
        alert.setContentText("Input text please");
        Optional<ButtonType> result = alert.showAndWait();
    }

    public void loginUser() throws ExecutionException, InterruptedException, IOException {


        if(textFieldUsername.getText().isEmpty() || textFieldPassword.getText().isEmpty()) {
            showDialogEmpty();
        }
        // Retrieve user data based on the provided username
        Map<String, Object> userData = SecondaryController.retrieveUserData(textFieldUsername.getText(),"Person");
        if (userData != null) {
            // Check if the provided username and password match the stored username and password
            String storedUsername = (String) userData.get("Username");
            String storedPassword = (String) userData.get("Password");
            if (storedPassword.equals(textFieldPassword.getText())) {
                // Password matches
                System.out.println("Logged in");
                showDialogCorrect();
                DemoApp.setRoot("primary");
            } else {
                // Password does not match dialog
                showDialogIncorrect();
            }
        } else {
            // User does not exist in the database
            //dialog
            showDialogUserDoesNotExist ();
        }
        textFieldUsername.clear();
        textFieldPassword.clear();
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


}
