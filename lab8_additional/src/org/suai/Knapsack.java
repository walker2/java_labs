package org.suai;

import java.util.ArrayList;
import java.util.Iterator;

public class Knapsack {
    private ArrayList<Item> bestItems = null;
    private int maxWeight;
    private int bestCost;

    public Knapsack(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    private int calcWeight(ArrayList<Item> items) {
        int sum = 0;
        for (Iterator<Item> i = items.iterator(); i.hasNext(); ) {
            sum += i.next().getWeight();
        }
        return sum;
    }

    private int calcCost(ArrayList<Item> items) {
        int sum = 0;
        for (Iterator<Item> i = items.iterator(); i.hasNext(); ) {
            sum += i.next().getCost();
        }
        return sum;
    }

    private void compareWithBestList(ArrayList<Item> items) {
        if (bestItems == null) {
            if (calcWeight(items) <= maxWeight) {
                bestItems = items;
                bestCost = calcCost(items);
            }
        } else {
            if (calcWeight(items) <= maxWeight && calcCost(items) > bestCost) {
                bestItems = items;
                bestCost = calcCost(items);
            }
        }
    }

    public synchronized void findBestCombination(ArrayList<Item> items) {
        if (items.size() > 0) {
            compareWithBestList(items);
        }

        for (int i = 0; i < items.size(); i++) {
            ArrayList<Item> newList = new ArrayList<>(items);
            newList.remove(i);
            findBestCombination(newList);
        }
    }


    public void threadedFindBestCombination(ArrayList<Item> items, int threadCount) throws Exception {
        bestItems.clear();
        bestCost = 0;
        if (threadCount > items.size())
            throw new Exception("Thread count should be less or equal to number of items");

        compareWithBestList(items);
        Thread[] threads = new Thread[threadCount];
        int i = 0;
        for (int threadIndex = threadCount - 1; threadIndex >= 0; threadIndex--, i++) {
            ArrayList<Item> thItems = new ArrayList<>(items);
            thItems.remove(i);
            threads[threadIndex] = new Thread(new Runnable() {
                @Override
                public void run() {
                    findBestCombination(new ArrayList<Item>(thItems));
                }
            });
            threads[threadIndex].start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Item> getBestItems() {
        return bestItems;
    }

    public void printBestItems() {
        for (Iterator<Item> i = bestItems.iterator(); i.hasNext(); ) {
            Item item = i.next();
            System.out.println(item.getName() + " " + item.getCost() + " " + item.getWeight());
        }
        System.out.println();
    }
}
