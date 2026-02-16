package org.example.unishpere;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class showTutReqController {
    @FXML
    private Circle profile;
    @FXML
    private VBox vBox;  // VBox to hold the tutoring request cards

    @FXML
    public void goToHomePage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void openProfilePopup() {
        try {
            // Load the FXML file for the popup content
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
            AnchorPane popupContent = fxmlLoader.load();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            popupStage.setTitle("Profile");
            Scene popupScene = new Scene(popupContent);
            popupStage.setScene(popupScene);

            // Get the main window (parent stage) and set it as the owner of the popup
            Stage parentStage = (Stage) profile.getScene().getWindow();
            popupStage.initOwner(parentStage); // Set parent stage as the owner

            // Center the popup relative to the screen (adjust position as needed)
            popupStage.setX(1367);
            popupStage.setY(106);

            // Show the popup
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadProfilePhoto();
        loadTutoringRequests();
    }

    private void loadProfilePhoto() {
        String loggedInUserEmail = Session.getLoggedInUser();

        if (loggedInUserEmail == null) {
            System.out.println("No user is logged in.");
            return;
        }

        try (Connection connection = dbConnect.getconnection()) {
            // Query to fetch the user data
            String query = "SELECT profile_photo FROM users WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, loggedInUserEmail);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Retrieve and set the user's profile photo
                String profilePhotoPath = resultSet.getString("profile_photo");
                if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                    Image image = new Image(profilePhotoPath);
                    profile.setFill(new ImagePattern(image));
                } else {
                    File file = new File("src/main/resources/img/defaultPhoto.png");
                    if (file.exists()) {
                        String defaultPhotoPath = file.toURI().toString();
                        Image defaultImage = new Image(defaultPhotoPath);
                        profile.setFill(new ImagePattern(defaultImage)); // Assuming Circle
                    } else {
                        System.out.println("Default photo not found.");
                    }
                }
            } else {
                System.out.println("User not found in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTutoringRequests() {
        try (Connection connection = dbConnect.getconnection()) {
            if (connection == null) {
                System.out.println("Database connection failed.");
                return;
            }

            // Query to fetch tutoring requests in descending order by created_time
            String query = """
            SELECT pt.requester_id, pt.course_name, pt.problem_topic, pt.description, 
                   u.first_name, u.last_name, u.profile_photo
            FROM peertutoring pt
            JOIN users u ON pt.requester_id = u.id
            ORDER BY pt.created_time DESC
        """;

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Clear the VBox before adding new cards
            vBox.getChildren().clear();

            // Create a new HBox to hold up to 3 cards
            HBox hBox = new HBox();
            hBox.setSpacing(20); // Space between cards
            int cardCount = 0;

            while (resultSet.next()) {
                // Load the tutoring request card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("peerRequestCard.fxml"));
                Node cardNode = loader.load();

                // Get the controller of the card and set the data
                peerTutReqCardController cardController = loader.getController();
                cardController.setRequesterDetails(
                        resultSet.getString("first_name") + " " + resultSet.getString("last_name"),
                        resultSet.getString("course_name"),
                        resultSet.getString("problem_topic"),
                        resultSet.getString("description")
                );

                // Set the profile photo
                String profilePhotoPath = resultSet.getString("profile_photo");
                cardController.setProfilePhoto(profilePhotoPath);

                // Add the card to the current HBox
                hBox.getChildren().add(cardNode);
                cardCount++;

                // If 3 cards are added to the HBox, add it to the VBox and create a new HBox
                if (cardCount == 3) {
                    vBox.getChildren().add(hBox);
                    hBox = new HBox();
                    hBox.setSpacing(20);
                    cardCount = 0;
                }
            }

            // Add the last HBox if it contains any cards
            if (!hBox.getChildren().isEmpty()) {
                vBox.getChildren().add(hBox);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

}

