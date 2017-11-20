package lab9_additional.src.filestat;/* Реализовать программу, которая подсчитывает статистику употребления слов в заданных текстовых файлах.
Программа получает список файлов в качестве параметров командной строки.
Каждый файл обрабатывается в отдельном потоке.
Для подсчета числа уникальных слов используется общий для всех потоков HashMap.
 */

public class CalculateFileStat {
    public static void main(String[] args) throws InterruptedException {
        WordStat wordStat = new WordStat();
        WordFinder[] threads = new WordFinder[args.length];

        for (int i = args.length - 1; i >= 0; i--) {
            threads[i] = new WordFinder(args[i], wordStat);
            threads[i].start();
        }

        try {
            for (WordFinder thread : threads)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(wordStat);

    }
}
