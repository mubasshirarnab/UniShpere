package org.example.unishpere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public List<Post> getPostsFromDatabase() {
        List<Post> posts = new ArrayList<>();

        try (Connection conn = dbConnect.getconnection()) { // Your DB connection method
            String query = "SELECT * FROM posts ORDER BY created_at DESC"; // Query to fetch posts
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                // Extract post data from the result set
                int userId = rs.getInt("user_id");
                String caption = rs.getString("caption");
                String photoUrl = rs.getString("photo_url");

                // Create Post objects to hold this data
                posts.add(new Post(userId, caption, photoUrl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }
}
