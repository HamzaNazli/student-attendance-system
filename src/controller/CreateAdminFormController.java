package controller;

import db.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAdminFormController {

    public TextField txtName;
    public TextField txtUserName;
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public Button btnCreateAccount;
    public Rectangle rect1;
    public Rectangle rect2;
    public Rectangle rect3;

    public void initialize() {
        handlePasswordStrength();
    }

    private void handlePasswordStrength() {
        rect1.setFill(Color.TRANSPARENT);
        rect2.setFill(Color.TRANSPARENT);
        rect3.setFill(Color.TRANSPARENT);

        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            rect1.setFill(Color.TRANSPARENT);
            rect2.setFill(Color.TRANSPARENT);
            rect3.setFill(Color.TRANSPARENT);
            if (!newValue.trim().isEmpty() && newValue.trim().length() < 4) {
                rect1.setFill(Paint.valueOf("#ff1f1f"));
            } else if (newValue.trim().length() == 4) {
                rect1.setFill(Paint.valueOf("#ff1f1f"));
                rect2.setFill(Paint.valueOf("#ffef21"));
            } else if (newValue.trim().length() >= 4) {
                rect1.setFill(Paint.valueOf("#ff1f1f"));
                rect2.setFill(Paint.valueOf("#ffef21"));
                rect3.setFill(Paint.valueOf("#21ff56"));
            }
        });
    }

    public void btnCreateAccount_OnAction(ActionEvent event) {
        if (!isValidated()) {
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("INSERT INTO user (name, username, password, role) VALUES (?,?,?,?)");
            stm.setString(1, txtName.getText().trim());
            stm.setString(2, txtUserName.getText().trim());
            stm.setString(3, txtPassword.getText().trim());
            stm.setString(4, "ADMIN");
            stm.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION, "Your account has been created successfully").showAndWait();

            /* Let's redirect to the Login Form */
            AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
            Scene loginScene = new Scene(root);
            Stage primaryStage = new Stage();
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Student Attendance System: Log In");
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            Platform.runLater(() -> primaryStage.sizeToScene());
        } catch (SQLException | IOException e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong, please try again").show();
            e.printStackTrace();
        }
    }

    private boolean isValidated() {
        String name = txtName.getText().trim();
        String username = txtUserName.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        if (!name.matches("[A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "Please enter valid name").show();
            txtName.selectAll();
            txtName.requestFocus();
            return false;
        } else if (username.length() < 4) {
            new Alert(Alert.AlertType.ERROR, "Username should be at least 4 characters long").show();
            txtUserName.selectAll();
            txtUserName.requestFocus();
            return false;
        } else if (!username.matches("[A-Za-z0-9]+")) {
            new Alert(Alert.AlertType.ERROR, "Username can contain only characters and digits").show();
            txtUserName.selectAll();
            txtUserName.requestFocus();
            return false;
        } else if (password.length() < 4) {
            new Alert(Alert.AlertType.ERROR, "Password should be at least 4 characters long").show();
            txtPassword.selectAll();
            txtPassword.requestFocus();
            return false;
        } else if (!password.equals(confirmPassword)) {
            new Alert(Alert.AlertType.ERROR, "Password mismatch").show();
            txtConfirmPassword.selectAll();
            txtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

}
