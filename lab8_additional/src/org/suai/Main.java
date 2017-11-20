package org.suai;

import utils.TimeWatch;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Item> items = new ArrayList<>();
        TimeWatch timeWatch;
        long passedTime;

        items.add(new Item(2, 5000, "Shoes"));
        items.add(new Item(4, 1500, "Jeans"));
        items.add(new Item(2, 40000, "Notebook"));
        items.add(new Item(1, 500, "Lunch"));
        items.add(new Item(2, 4000, "Coat"));
        items.add(new Item(1, 1000, "Scarf"));
        items.add(new Item(1, 100, "Apple"));
        items.add(new Item(1, 300, "Book"));

        Knapsack ks = new Knapsack(9);
        timeWatch = TimeWatch.start();
        ks.findBestCombination(items);
        passedTime = timeWatch.time();
        System.out.println("Ordinary result with time " + passedTime + " ms \n");
        ks.printBestItems();

        timeWatch = TimeWatch.start();
        try {
            ks.threadedFindBestCombination(items, 2);
        } catch (Exception e) {
        }
        passedTime = timeWatch.time();
        System.out.println("Threaded result with time " + passedTime + " ms \n");
        ks.printBestItems();
    }
}
