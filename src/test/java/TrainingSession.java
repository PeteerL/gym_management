import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Test
void testAddParticipant() {
    com.gymmanagement.models.TrainingSession session = new com.gymmanagement.models.TrainingSession(1, 1, LocalDateTime.now(), 60, 10);
    assertTrue(session.addParticipant(1)); // Adauga participant cu ID 1
    assertEquals(1, session.getParticipantCount());
}

@Test
void testMaxCapacity() {
    com.gymmanagement.models.TrainingSession session = new com.gymmanagement.models.TrainingSession(1, 1, LocalDateTime.now(), 60, 2);
    session.addParticipant(1);
    session.addParticipant(2);
    assertFalse(session.addParticipant(3));
    assertEquals(2, session.getParticipantCount());
}
