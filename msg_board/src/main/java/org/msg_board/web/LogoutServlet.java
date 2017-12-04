package org.msg_board.web;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

public class LogoutServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(LogoutServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();

        try {
            response.sendRedirect("/board");
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }
}
