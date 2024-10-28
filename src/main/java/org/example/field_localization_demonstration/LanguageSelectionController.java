package org.example.field_localization_demonstration;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageSelectionController {

    @FXML
    private Label selectLanguageLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button closeButton;

    private ResourceBundle bundle;

    @FXML
    private void initialize() {
        languageComboBox.getItems().addAll("English", "Farsi", "Japanese");
        languageComboBox.setValue("English");
        updateLanguage();  // Load default language text
    }

    @FXML
    private void onLanguageChange() {
        updateLanguage();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    private String getLocalizedTableName() {
        String selectedLanguage = languageComboBox.getValue();
        switch (selectedLanguage) {
            case "Farsi":
                return "employee_fa";
            case "Japanese":
                return "employee_ja";
            default:
                return "employee_en";
        }
    }

    @FXML
    private void onSaveButtonClick() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showAlert(bundle.getString("error"), bundle.getString("error.fieldsRequired"), Alert.AlertType.ERROR);
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(bundle.getString("error"), bundle.getString("error.invalidEmail"), Alert.AlertType.ERROR);
            return;
        }

        String tableName = getLocalizedTableName();
        String sql = "INSERT INTO " + tableName + " (first_name, last_name, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.executeUpdate();

            showAlert(bundle.getString("success"), bundle.getString("successMessage"), Alert.AlertType.INFORMATION);

            // Clear text fields after successful save
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();

        } catch (SQLException e) {
            String failMessage = String.format(bundle.getString("error.saveFailed"), e.getMessage());
            showAlert(bundle.getString("error"), failMessage, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onResetButtonClick() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        languageComboBox.setValue("English");
        updateLanguage();
    }

    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateLanguage() {
        String selectedLanguage = languageComboBox.getValue();

        Locale locale;
        switch (selectedLanguage) {
            case "Farsi":
                locale = new Locale("fa");
                break;
            case "Japanese":
                locale = new Locale("ja");
                break;
            default:
                locale = new Locale("en");
                break;
        }
        bundle = ResourceBundle.getBundle("messages", locale);

        selectLanguageLabel.setText(bundle.getString("selectLanguage"));
        firstNameLabel.setText(bundle.getString("firstName"));
        lastNameLabel.setText(bundle.getString("lastName"));
        emailLabel.setText(bundle.getString("email"));
        saveButton.setText(bundle.getString("save"));
        resetButton.setText(bundle.getString("reset"));
        closeButton.setText(bundle.getString("close"));

        firstNameField.setPromptText(bundle.getString("placeholder.firstName"));
        lastNameField.setPromptText(bundle.getString("placeholder.lastName"));
        emailField.setPromptText(bundle.getString("placeholder.email"));
    }
}
