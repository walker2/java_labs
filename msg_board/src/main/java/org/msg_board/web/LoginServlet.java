package org.msg_board.web;

import org.msg_board.service.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

public class LoginServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(LoginServlet.class.getName());

    private static UserService userService = new UserService();

    @Override
    public void init(ServletConfig config) {
        userService.loadFromFile("/home/andrew/git/msg_board/data/users");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");

        try {
            dispatcher.forward(request, response);
        } catch (IOException | ServletException e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        if (userService.verify(name, password)) {
            HttpSession session = request.getSession();

            session.setAttribute("user", name);
            try {
                response.sendRedirect("/board");
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        } else {
            request.setAttribute("error", "Invalid name or password");

            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");

            try {
                dispatcher.forward(request, response);
            } catch (IOException | ServletException e) {
                logger.info(e.getMessage());
            }
        }
    }
}
