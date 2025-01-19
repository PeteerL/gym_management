import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Test
void testSubscriptionDuration() {
    LocalDate startDate = LocalDate.of(2025, 1, 1);
    LocalDate endDate = LocalDate.of(2025, 12, 31);
    com.gymmanagement.models.Subscription subscription = new com.gymmanagement.models.Subscription(1, 1, "Annual", startDate, endDate, "Active");

    assertEquals(365, subscription.getDurationDays());
}

@Test
void testSubscriptionStatusUpdate() {
    com.gymmanagement.models.Subscription subscription = new com.gymmanagement.models.Subscription(1, 1, "Monthly", LocalDate.now(), LocalDate.now().plusMonths(1), "Active");
    subscription.setStatus("Inactive");
    assertEquals("Inactive", subscription.getStatus());
}
