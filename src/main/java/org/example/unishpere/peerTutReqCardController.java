package org.example.unishpere;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class peerTutReqCardController {

    @FXML
    private Circle photo;
    @FXML
    private Label name;
    @FXML
    private Label course;
    @FXML
    private Label topic;
    @FXML
    private Label description;

    private String requesterEmail; // This will be passed to fetch data for the sender

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
        loadTutoringRequestDetails();
    }

    public void setRequesterDetails(String requesterName, String courseName, String problemTopic, String descriptionText) {
        name.setText(requesterName); // Set the name label
        course.setText(courseName); // Set the course label
        topic.setText(problemTopic); // Set the topic label
        description.setText(descriptionText); // Set the description label
    }

    void setProfilePhoto(String profilePhotoPath) {
        if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
            try {
                Image image = new Image(profilePhotoPath, false);
                photo.setFill(new ImagePattern(image)); // Setting the profile photo in Circle
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid image path: " + profilePhotoPath);
                setDefaultProfilePhoto();
            }
        } else {
            setDefaultProfilePhoto();
        }
    }

    private void setDefaultProfilePhoto() {
        File file = new File("src/main/resources/img/defaultPhoto.png");
        if (file.exists()) {
            Image defaultImage = new Image(file.toURI().toString());
            photo.setFill(new ImagePattern(defaultImage));
        } else {
            System.out.println("Default photo not found.");
        }
    }

    private void loadTutoringRequestDetails() {
        if (requesterEmail == null) {
            System.out.println("Requester email is null.");
            return;
        }

        try (Connection connection = dbConnect.getconnection()) {
            // Query to fetch the user details (first name, last name, profile photo) from the users table
            String userQuery = "SELECT first_name, last_name, profile_photo FROM users WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(userQuery);
            preparedStatement.setString(1, requesterEmail);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Set the sender's name (first name + last name)
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                name.setText(firstName + " " + lastName);

                // Set the sender's profile photo
                String profilePhotoPath = resultSet.getString("profile_photo");
                setProfilePhoto(profilePhotoPath);
            } else {
                System.out.println("User not found in the database.");
            }

            // Query to fetch the tutoring request details (course name, problem topic, description)
            String requestQuery = "SELECT course_name, problem_topic, description FROM peertutoring WHERE requester_id = (SELECT id FROM users WHERE email = ?)";
            preparedStatement = connection.prepareStatement(requestQuery);
            preparedStatement.setString(1, requesterEmail);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Set the course name, topic, and description
                course.setText(resultSet.getString("course_name"));
                topic.setText(resultSet.getString("problem_topic"));
                description.setText(resultSet.getString("description"));
            } else {
                System.out.println("Tutoring request details not found in the database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
