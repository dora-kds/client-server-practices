package com.practice;

public class Processor {
    private final Warehouse
            warehouse = new Warehouse();

    public synchronized Message process(Message message) {
        String[] data = message.message.split(";");

        switch (message.cType) {
            case 1:
                int amount = warehouse.getAmount(data[0]);
                return new Message(100, 0, "Amount: " + amount);

            case 2:
                warehouse.removeAmount(data[0], Integer.parseInt(data[1]));
                break;

            case 3:
                warehouse.addAmount(data[0], Integer.parseInt(data[1]));
                break;

            case 4:
                warehouse.addGroup(data[0]);
                break;

            case 5:
                warehouse.addProductToGroup(data[0], data[1]);
                break;

            case 6:
                warehouse.setPrice(data[0], Double.parseDouble(data[1]));
                break;
        }
        return new Message(100, 0, "OK");
    }
}