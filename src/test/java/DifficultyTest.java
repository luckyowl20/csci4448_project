import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DifficultyTest {

    @Test
    void testEasyDifficulty() {
        IDifficulty easy = new EasyDifficulty();
        assertEquals(9, easy.getRows());
        assertEquals(9, easy.getCols());
        assertEquals(10, easy.getMineCount());
        assertEquals("Easy", easy.getName());
    }

    @Test
    void testMediumDifficulty() {
        IDifficulty medium = new MediumDifficulty();
        assertEquals(16, medium.getRows());
        assertEquals(16, medium.getCols());
        assertEquals(40, medium.getMineCount());
        assertEquals("Medium", medium.getName());
    }

    @Test
    void testHardDifficulty() {
        IDifficulty hard = new HardDifficulty();
        assertEquals(16, hard.getRows());
        assertEquals(30, hard.getCols());
        assertEquals(99, hard.getMineCount());
        assertEquals("Hard", hard.getName());
    }
}
