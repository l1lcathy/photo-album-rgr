package com.photoalbum.repository;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PhotoRepository {

    private Connection connection;

    public PhotoRepository() {

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/photoalbum_db",
                    "postgres",
                    "katty"
            );

            System.out.println("DATABASE CONNECTED");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String filename, String path, int userId)
            throws SQLException {

        String sql =
                "INSERT INTO photos(filename, path, user_id) VALUES (?, ?, ?)";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, filename);
        ps.setString(2, path);
        ps.setInt(3, userId);

        ps.executeUpdate();
    }

    public List<String> findAllPaths()
            throws SQLException {

        List<String> photos =
                new ArrayList<>();

        String sql =
                "SELECT path FROM photos ORDER BY id DESC";

        Statement st =
                connection.createStatement();

        ResultSet rs =
                st.executeQuery(sql);

        while (rs.next()) {

            photos.add(
                    rs.getString("path")
            );
        }

        return photos;
    }
}