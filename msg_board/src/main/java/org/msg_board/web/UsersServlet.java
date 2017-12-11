package org.msg_board.web;

import org.msg_board.service.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

public class UsersServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(LoginServlet.class.getName());

    private static UserService userService = LoginServlet.userService;

    private static String dataPath = "/home/andrew/git/java_labs/msg_board/data/users";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String delName = request.getParameter("delete");
        if (delName != null && !delName.equals(""))
        {
            logger.info("Deleting " + delName);
            userService.deleteByName(delName);
            userService.saveToFile(dataPath);
        }

        HttpSession session = request.getSession();
        session.setAttribute("usrService", userService);

        if (session.getAttribute("user").equals("admin")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("users.jsp");

            try {
                dispatcher.forward(request, response);
            } catch (IOException | ServletException e) {
                logger.info(e.getMessage());
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        if (name != null && password != null) {
            userService.addUser(name, password);
            userService.saveToFile(dataPath);
            try {
                response.sendRedirect("/users");
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }
}
