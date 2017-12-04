package org.msg_board.web;

import org.msg_board.model.Message;
import org.msg_board.service.MessageService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class MessageBoardServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(MessageBoardServlet.class.getName());

    private static MessageService messageService = new MessageService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        response.setContentType("text/html; charset=UTF-8");
        session.setAttribute("msgService", messageService);

        if (session.getAttribute("user") == null) {
            logger.info("Guest view");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/guest/board.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException | IOException e) {
                logger.info(e.getMessage());
            }
        } else {
            logger.info("User view");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/user/board.jsp");

            try {
                dispatcher.forward(request, response);
            } catch (ServletException | IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            try {
                response.sendRedirect("new-message.jsp");
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        } else {
            String userName = (String) session.getAttribute("user");

            try {
                request.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.info(e.getMessage());
            }

            String title = request.getParameter("title");
            String text = request.getParameter("text");

            Message ad = new Message();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

            ad.setTitle(title);
            ad.setText(text);
            ad.setUserName(userName);
            ad.setTime(dateFormat.format(date));

            messageService.add(ad);

            try {
                response.sendRedirect("/board");
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }
}
