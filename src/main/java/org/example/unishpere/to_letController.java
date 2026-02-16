package org.example.unishpere;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class to_letController implements Initializable {
    @FXML
    private VBox vBox;
    @FXML
    private Button openPopupButton;
    @FXML
    private Circle profile;
    @FXML
    private Circle writePostPhoto;

    @FXML
    public void goToHomePage(ActionEvent event) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void openPopup(ActionEvent event) {
        try {
            // Load the FXML file for the popup content
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("writePost.fxml")); // Update path as needed
            AnchorPane popupContent = fxmlLoader.load();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            popupStage.setTitle("Write post");
            Scene popupScene = new Scene(popupContent);
            popupStage.setScene(popupScene);

            // Get the main window (parent stage) to center the popup
            Stage parentStage = (Stage) openPopupButton.getScene().getWindow();

            // Show the popup initially to calculate its dimensions
            popupStage.show();

            // Center the popup relative to the parent window
            popupStage.setX(parentStage.getX() + (parentStage.getWidth() - popupStage.getWidth()) / 2);
            popupStage.setY(parentStage.getY() + (parentStage.getHeight() - popupStage.getHeight()) / 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadProfilePhoto();
        loadWriteCommentProfilePhoto();
        try {
            VBox postsContainer = getVBox();

            // Create an instance of PostDAO
            PostDAO postDAO = new PostDAO();
            List<Post> posts = postDAO.getPostsFromDatabase();

            HBox row = null; // Row container (horizontal box)
            int postCount = 0; // Counter for posts added

            for (Post post : posts) {
                if (postCount % 2 == 0) {
                    // Create a new row (HBox) for every two posts
                    row = new HBox();
                    row.setSpacing(20); // Space between posts in a row
                    row.setAlignment(Pos.CENTER);

                    // Add style to the HBox (row)
                    row.setStyle("-fx-spacing: 20; -fx-alignment: center;");
                    postsContainer.getChildren().add(row);
                }

                // Load the post.fxml dynamically
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("posts.fxml"));
                AnchorPane postAnchorPane = fxmlLoader.load();

                // Configure the post content via its controller (optional)
                postController controller = fxmlLoader.getController();
                controller.setDescriptions(post.getCaption()); // Set the post description from DB
                controller.setImage(post.getPhotoUrl()); // Set the image URL from DB

                // Add style to each post (AnchorPane) with a colorful border
                postAnchorPane.setStyle(
                        "-fx-background-color: #ffffff;" +
                                "-fx-border-width: 3;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 10, 0, 0, 5);" +
                                "-fx-padding: 15;" +
                                "-fx-min-width: 400;" + // Width of each post
                                "-fx-min-height: 250;"  // Height of each post
                );

                // Add the post to the current row
                row.getChildren().add(postAnchorPane);

                postCount++;
            }

            // Set the VBox with rows as the content of the ScrollPane
            ScrollPane scrollPane = new ScrollPane(postsContainer);
            scrollPane.setFitToWidth(true); // Ensure rows fit the width of the ScrollPane

            // Add style to the ScrollPane
            scrollPane.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: #dcdcdc;" +
                            "-fx-border-radius: 10;" +
                            "-fx-padding: 10;" +
                            "-fx-border-width: 2;"
            );

            // Replace the existing VBox in the FXML with the populated ScrollPane
            vBox.getChildren().clear();
            vBox.getChildren().add(scrollPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private VBox getVBox() {
        VBox postsContainer = new VBox(); // Vertical container for rows
        postsContainer.setSpacing(20);   // Space between rows
        postsContainer.setAlignment(Pos.CENTER); // Center-align rows

        // Add style to the VBox (postsContainer) with a border
        postsContainer.setStyle(
                "-fx-padding: 20;" +
                        "-fx-background-color: #f9f9f9;" +
                        "-fx-border-color: linear-gradient(to right, #ffafbd, #ffc3a0);" + // Gradient border for the VBox
                        "-fx-border-width: 4;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;"
        );
        return postsContainer;
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

//    @FXML
//    public void initialize() {
//        loadProfilePhoto();
//        loadWriteCommentProfilePhoto();
//    }

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
//                    profilePhoto.setImage(image);
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


    private void loadWriteCommentProfilePhoto() {
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
//                    profilePhoto.setImage(image);
                    writePostPhoto.setFill(new ImagePattern(image));
                } else {
                    File file = new File("src/main/resources/img/defaultPhoto.png");
                    if (file.exists()) {
                        String defaultPhotoPath = file.toURI().toString();
                        Image defaultImage = new Image(defaultPhotoPath);
                        writePostPhoto.setFill(new ImagePattern(defaultImage)); // Assuming Circle
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
}
