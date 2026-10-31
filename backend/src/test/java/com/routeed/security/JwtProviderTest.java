package com.routeed.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.routeed.organization.Organization;
import com.routeed.user.User;
import com.routeed.user.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtProviderTest {
    @Test
    void generatesAndReadsAValidAccessToken() {
        Organization organization = new Organization("QA Org", "qa-org");
        ReflectionTestUtils.setField(organization, "id", UUID.randomUUID());
        User user = new User(organization, "QA User", "qa@example.test", "hash", UserRole.LEARNER);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        JwtProvider provider = new JwtProvider("this-is-a-test-secret-with-at-least-thirty-two-bytes", 15);

        String token = provider.generateAccessToken(user);

        assertTrue(provider.isValid(token));
        assertEquals("qa@example.test", provider.extractEmail(token));
    }
}
