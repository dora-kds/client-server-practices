package com.practice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProcessorTest {

    @Test
    public void fullProcessorFlowTest() {
        Processor processor = new Processor();

        Message add = new Message(3, 1, "Гречка;20");
        processor.process(add);

        Message get = new Message(1, 1, "Гречка");
        Message response1 = processor.process(get);
        assertTrue(response1.message.contains("20"));

        Message remove = new Message(2, 1, "Гречка;5");
        processor.process(remove);
        Message response2 = processor.process(get);
        assertTrue(response2.message.contains("15"));

        Message addGroup = new Message(4, 1, "Крупи");
        processor.process(addGroup);

        Message addToGroup = new Message(5, 1, "Крупи;Гречка");
        processor.process(addToGroup);

        Message setPrice = new Message(6, 1, "Гречка;50.5");
        processor.process(setPrice);

        Message finalCheck = processor.process(get);

        assertNotNull(finalCheck);
        assertTrue(finalCheck.message.contains("15"));
    }
}