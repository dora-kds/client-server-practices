package com.practice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProcessorTest {

    @Test
    void fullProcessorFlowTest() {
        Processor processor = new Processor(new SQLiteProdDB(":memory:"));
        Message create = new Message(3, 1, "Стіл;Меблі;10;1500");
        Message createRes = processor.process(create);
        assertTrue(createRes.message.startsWith("CREATED_ID:"));
        int id = Integer.parseInt(createRes.message.split(":")[1]);

        Message read = new Message(2, 1, String.valueOf(id));
        Message readRes = processor.process(read);
        assertTrue(readRes.message.contains("Стіл"));
        assertTrue(readRes.message.contains("Меблі"));

        Message update = new Message(4, 1, id + ";Стіл новий;Меблі;15;2000");
        Message updateRes = processor.process(update);
        assertEquals("UPDATED", updateRes.message);

        Message readUpdated = new Message(2, 1, String.valueOf(id));
        Message readUpdatedRes = processor.process(readUpdated);
        assertTrue(readUpdatedRes.message.contains("Стіл новий"));
        assertTrue(readUpdatedRes.message.contains("15"));

        Message filter = new Message(6, 1, "Стіл;Меблі;null;null;null;null;1;10");
        Message filterRes = processor.process(filter);
        assertTrue(filterRes.message.contains("SEARCH_RESULTS"));
        assertTrue(filterRes.message.contains("Стіл новий"));

        Message delete = new Message(5, 1, String.valueOf(id));
        Message deleteRes = processor.process(delete);
        assertEquals("DELETED", deleteRes.message);

        Message readAfterDelete = new Message(2, 1, String.valueOf(id));
        Message deletedResult = processor.process(readAfterDelete);
        assertEquals("ERROR:NOT_FOUND", deletedResult.message);
    }
}