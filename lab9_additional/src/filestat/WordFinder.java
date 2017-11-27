package filestat;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class WordFinder extends Thread {
    private final String filePath;
    private final WordStat stat;

    public WordFinder(String filePath, WordStat stat) {
        this.filePath = filePath;
        this.stat = stat;
    }

    @Override
    public void run() {
        System.out.println("Scanning file " + filePath + " with " + this.getName());
        File file = new File(filePath);
        try (Scanner sc = new Scanner(new FileInputStream(file))) {
            synchronized (stat) {
                while (sc.hasNext()) {
                    String next = sc.next();
                    stat.add(next);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(this.getName() + " is done scanning");
    }
}
