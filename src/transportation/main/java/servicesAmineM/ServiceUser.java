package servicesAmineM;

import entitiesAmineM.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static utilsAmineM.DBConnection2.getConnection;

public class ServiceUser {

    public void add(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, user_type, phone, address, passport, profile_photo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            pstmt.setString(4, user.getUserType());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setString(7, user.getPassport());
            pstmt.setBytes(8, user.getProfilePhoto());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
        }
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, phone = ?, address = ?, passport = ?, profile_photo = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getAddress());
            pstmt.setString(4, user.getPassport());
            pstmt.setBytes(5, user.getProfilePhoto());
            pstmt.setInt(6, user.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No user found with id: " + user.getId());
            }
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = getConnection()) {
            // Check for bookings (if bookings table exists)
            String checkBookingsSql = "SELECT COUNT(*) FROM bookings WHERE user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkBookingsSql)) {
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cannot delete account with active bookings.");
                }
            } catch (SQLException e) {
                // If bookings table doesn't exist, skip this check
                if (!e.getMessage().contains("Table 'bookings' doesn't exist")) {
                    throw e;
                }
            }

            // Delete token
            String deleteTokenSql = "DELETE FROM tokens WHERE user_id = ?";
            try (PreparedStatement tokenStmt = conn.prepareStatement(deleteTokenSql)) {
                tokenStmt.setInt(1, id);
                tokenStmt.executeUpdate();
            }

            // Delete user
            String deleteUserSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(deleteUserSql)) {
                userStmt.setInt(1, id);
                int rows = userStmt.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("No user found with id: " + id);
                }
            }
        }
    }

    public void updatePassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("user_type"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("passport"),
                        null);
                user.setProfilePhoto(rs.getBytes("profile_photo"));
                users.add(user);
            }
        }
        return users;
    }

    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("user_type"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("passport"),
                        null);
                user.setProfilePhoto(rs.getBytes("profile_photo"));
                return user;
            }
        }
        return null;
    }

    public User verifyRememberMeToken(String email, String token) throws SQLException {
        String sql = "SELECT u.* FROM users u JOIN tokens t ON u.id = t.user_id WHERE u.email = ? AND t.token = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, token);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("user_type"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("passport"),
                        null);
                user.setProfilePhoto(rs.getBytes("profile_photo"));
                return user;
            }
        }
        return null;
    }

    public void storeRememberMeToken(int userId, String token) throws SQLException {
        String sql = "INSERT INTO tokens (user_id, token) VALUES (?, ?) ON DUPLICATE KEY UPDATE token = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, token);
            pstmt.setString(3, token);
            pstmt.executeUpdate();
        }
    }

    public void clearRememberMeToken(int userId) throws SQLException {
        String sql = "DELETE FROM tokens WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public void updateProfilePhoto(int id, File selectedFile) throws SQLException, IOException {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users SET profile_photo = ? WHERE user_id = ?"
            );
            if (selectedFile != null) {
                try (FileInputStream fis = new FileInputStream(selectedFile)) {
                    stmt.setBinaryStream(1, fis, (int) selectedFile.length());
                }
            } else {
                stmt.setNull(1, Types.BLOB);
            }
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public byte[] getProfilePhoto(int id) throws SQLException {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT profile_photo FROM users WHERE user_id = ?"
            );
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBytes("profile_photo");
            }
            return null;
        }
    }

    public String hashPassword(String newPassword) {
        return BCrypt.hashpw(newPassword, BCrypt.gensalt());
    }
}