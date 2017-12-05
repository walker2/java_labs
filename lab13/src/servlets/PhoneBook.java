package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    Реализовать сервлет для работы с записной книжкой. В записной книжке для каждого человека хранится его имя и список
    телефонов (их может быть несколько). При старте сервлет загружает записную книжку из текстового файла.
    Сервлет должен позволять:
    Просматривать список записей
    Добавить нового пользователя
    Добавить новый телефон

    На главной странице сервлет находится список записей. Вверху страницы ссылки --- добавить.
    Каждая из ссылок ведет на отдельную страницу, где с помощью элементов <input type="text" name="username" />
    в форме вводятся необходимые данные.
    Для отправки данных сервлету есть кнопка submit.

    В качестве контейнера сервлетов рекомендуется использовать либо сервер Tomcat, либо сервер Jetty

    NB: Синхронизация при работе нескольких пользователей с одной записной книжкой.

    Доп:
    На сервере хранится набор аватарок. Добавить возможность при добавлении новой записи с помощью радиокнопки
    выбрать аватарку. Аватарки должны отображаться в списке.

*/

public class PhoneBook extends HttpServlet {
    private static Logger logger = Logger.getLogger(PhoneBook.class.getName());
    private static String exceptionLiteral = "Exception";

    private static TreeMap<String, ArrayList<String>> phoneBook = new TreeMap<>();
    private static TreeMap<String, String> avatars = new TreeMap<>();

    private static String dataPath = "/home/andrewshipilo/IdeaProjects/java_labs/lab13"; //TODO: to env variables

    @Override
    public void init() {
        try {
            load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }
    }

    @Override
    public void destroy() {
        try {
            save();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>" +
                    "  <html lang=\"en\">\n" +
                    "  <head>" +
                    "  <title>Phonebook</title>\n" +
                    "  <meta charset=\"utf-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n" +
                    "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" +
                    "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>" +
                    "  </head>" +
                    "  <body>\n " +
                    "  <div class=\"container\">");
            out.println("\n<br>\n");
            String name;
            String numbers;
            String avatar;
            String[] nums;

            switch (uri) {
                case "/phonebook":
                    out.println(generateList());
                    break;
                case "/phonebook/form":
                    out.println(generateForm());
                    break;
                case "/phonebook/add":
                    name = request.getParameter("name");
                    numbers = request.getParameter("numbers");
                    avatar = request.getParameter("avatar");

                    if (numbers.contains(":")) {
                        nums = numbers.split(":");
                    } else {
                        nums = new String[1];
                        nums[0] = numbers;
                    }

                    boolean allNums = true;

                    for (String i : nums) {
                        if (!isNum(i)) {
                            allNums = false;
                            break;
                        }
                    }

                    if (!allNums || name.equals("")) {
                        out.println("Error in input<br>");
                        out.println("<a href=\"/phonebook\"> Back to list</a>\n");
                    } else {
                        add(name, nums);
                        avatars.put(name, avatar);
                        out.println(generateList());
                    }

                    break;
                default:
                    out.println("Nothing here!");
                    out.println("<a href=\"/phonebook/form\" class=\"btn btn-primary\" role=\"button\">" +
                            "<span class=\"glyphicon glyphicon-plus\"></span> Add</a>\n");
            }

            out.println("</div> </body>\n</html>");
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }
    }

    private String generateList() {
        StringBuilder list = new StringBuilder();

        list.append("<h2>Contacts</h2> <table class=\"table table-hover\"> " +
                "    <thead>\n" +
                "      <tr>\n" +
                "        <th>Avatar</th>\n" +
                "        <th>Name</th>\n" +
                "        <th>Numbers</th>\n" +
                "      </tr>\n" +
                "    </thead>");
        list.append("<tbody>");

        for (Map.Entry<String, ArrayList<String>> entry : phoneBook.entrySet()) {
            String name = entry.getKey();
            ArrayList<String> numbers = entry.getValue();
            String firstLetter = Character.toString(name.charAt(0)).toLowerCase();
            list.append("<tr>");

            list.append("<td><img src=\"http://localhost:9090/data/").append(avatars.get(name))
                    .append(".jpg\" ").append("width=\"50\" height = \"50\">");
            list.append("<td><img src=\"http://cdn.mysitemyway.com/icons-watermarks/simple-black/alphanum" +
                    "/alphanum_uppercase-letter-").append(firstLetter).append(
                    "/alphanum_uppercase-letter-").append(firstLetter).append(
                    "_simple-black_128x128.png\"").append("width=\"24\" height = \"24\">")
                    .append(name.subSequence(1, name.length())).append("</td>");

            list.append("<td>");
            for (String number : numbers) {
                list.append(number).append("; <br>");
            } //TODO: Multiple nubmers in td's
            list.append("</td>");

            list.append("</tr>");
        }
        list.append("</tbody>\n" +
                "  </table>");
        list.append("<a href=\"/phonebook/form\" class=\"btn btn-primary\" role=\"button\"><span class=\"glyphicon glyphicon-plus\"></span> Add</a>\n");
        return list.toString();
    }

    private String generateForm() {
        return  "<div class=\"col-sm-6\">" +
                "<a href=\"/phonebook\" class=\"btn btn-primary\"> " +
                "<span class=\"glyphicon glyphicon-arrow-left\"></span> Back</a>" +
                "<h2>Add new contact</h2>" +
                "<form method=\"GET\" action=\"/phonebook/add\">" +
                "Your name: <input type=\"text\" name=\"name\" class=\"form-control\"  placeholder=\"Enter name\"><br>" +
                "Phone number: <input type=\"text\" name=\"numbers\" class=\"form-control\"  placeholder=\"Enter number\">" +
                "<br>You can add multiple numbers by separating them with \':\' symbol <br>" +
                "<input name=\"avatar\" type=\"radio\" value=\"b_obama\">" +
                "<img src=\"http://localhost:9090/data/b_obama.jpg\" width=\"100\" height = \"100\">" +
                "<input name=\"avatar\" type=\"radio\" value=\"girl_with_cigarette\">" +
                "<img src=\"http://localhost:9090/data/girl_with_cigarette.jpg\" width=\"100\" height = \"100\">" +
                "<input name=\"avatar\" type=\"radio\" value=\"hidden_cat\">" +
                "<img src=\"http://localhost:9090/data/hidden_cat.jpg\" width=\"100\" height = \"100\">" +
                "<input name=\"avatar\" type=\"radio\" value=\"luciano_pavarotti\">" +
                "<img src=\"http://localhost:9090/data/luciano_pavarotti.jpg\" width=\"100\" height = \"100\"><br>" +
                "<input type=\"submit\" class=\"btn btn-primary btn-block\" value=\"Submit\">" +
                "</form>\n" +
                "</div>";

    }

    private synchronized void add(String name, String[] nums) {
        ArrayList<String> numbers;
        ArrayList<String> tmp = new ArrayList<>(Arrays.asList(nums));

        if (phoneBook.containsKey(name)) {
            numbers = phoneBook.get(name);
        } else {
            numbers = new ArrayList<>();
        }

        numbers.addAll(tmp);

        phoneBook.put(name, numbers);
    }

    private void load() throws IOException {
        File data = new File(dataPath + "/data/data.txt");
        if (!data.exists()) {
            logger.log(Level.SEVERE, exceptionLiteral, "Data not found");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(data))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(":");
                String name = tokens[0];
                avatars.put(name, tokens[1]);
                String[] nums = tokens[2].split(";");

                add(name, nums);
            }
        }
    }

    private void save() throws IOException {
        File data = new File(dataPath + "/data/data.txt");

        if (!data.exists()) {
            boolean ok = data.createNewFile();

            if (!ok) {
                throw new IOException("Can't create file");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(data))) {

            for (Map.Entry<String, ArrayList<String>> entry : phoneBook.entrySet()) {
                String name = entry.getKey();
                ArrayList<String> numbers = entry.getValue();

                writer.write(name + ":" + avatars.get(name) + ":");

                for (String i : numbers) {
                    writer.write(i + ";");
                }

                writer.write("\n");
            }
        }
    }

    private boolean isNum(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return false;
            }
        }

        return true;
    }
}