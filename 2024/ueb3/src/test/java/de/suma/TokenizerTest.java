package de.suma;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTest {

    @Test
    public void testGetTokensWithEmptyText() {
         String[] tokens = new Tokenizer().getTokens("");
         assertEquals(0, tokens.length);
    }

    @Test
    public void testGetTokensWithOneToken() {
        String[] tokens = new Tokenizer().getTokens(" token ");
        assertEquals(1, tokens.length);
        assertEquals("token", tokens[0]);
    }

    @Test
    public void testGetTokensWithMultipleTokens() {
        String[] tokens = new Tokenizer().getTokens(" Token1, token2   token3. ");
        assertEquals(3, tokens.length);
        assertEquals("token1,", tokens[0]);
        assertEquals("token2", tokens[1]);
        assertEquals("token3.", tokens[2]);
    }

    @Test
    @Disabled("Test ist deaktiviert, weil noch keine Implementierung für getTokensImproved vorliegt")
    public void testGetTokensImprovedWithMultipleTokens() {
        String[] tokens = new Tokenizer().getTokensImproved(" Token1, token2   token3. ");
        assertEquals(3, tokens.length);
        assertEquals("token1", tokens[0]);
        assertEquals("token2", tokens[1]);
        assertEquals("token3", tokens[2]);
    }

    @Test
    @Disabled("Test ist deaktiviert, weil noch keine Implementierung für getTokensImproved vorliegt")
    public void testGetTokensImprovedWithMultipleTokensAndParentheses() {
        String[] tokens = new Tokenizer().getTokensImproved(" (Token1; token2):   token3! token4? token5-");
        assertEquals(5, tokens.length);
        assertEquals("token1", tokens[0]);
        assertEquals("token2", tokens[1]);
        assertEquals("token3", tokens[2]);
        assertEquals("token4", tokens[3]);
        assertEquals("token5", tokens[4]);
    }

    @Test
    @Disabled("Test ist deaktiviert, weil noch keine Implementierung für getTokensImproved vorliegt")
    public void testGetTokensImprovedWithApostropheS() {
        String[] tokens = new Tokenizer().getTokensImproved(" (Token1's, token2's),   token3's. ");
        assertEquals(3, tokens.length);
        assertEquals("token1", tokens[0]);
        assertEquals("token2", tokens[1]);
        assertEquals("token3", tokens[2]);
    }

}
