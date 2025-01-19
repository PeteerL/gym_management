import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Test
void testUserCreation() {
    com.gymmanagement.models.User user = new com.gymmanagement.models.User(1, "John Doe", "john.doe@example.com", "password123", "Client");
    assertEquals(1, user.getId());
    assertEquals("John Doe", user.getName());
    assertEquals("john.doe@example.com", user.getEmail());
    assertEquals("Client", user.getRole());
}

@Test
void testPasswordUpdate() {
    com.gymmanagement.models.User user = new com.gymmanagement.models.User(1, "John Doe", "john.doe@example.com", "password123", "Client");
    user.setPassword("newPassword456");
    assertEquals("newPassword456", user.getPassword());
}
