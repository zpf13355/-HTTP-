package com.fpz.webapps.dictionary;

import com.fpz.standard.ServletException;
import com.fpz.standard.http.HttpServlet;
import com.fpz.standard.http.HttpServletRequest;
import com.fpz.standard.http.HttpServletResponse;
import com.fpz.standard.http.HttpSession;
import com.fpz.tomcat.http.HttpSessionImpl;
import com.fpz.tomcat.http.Request;

import java.io.IOException;

public class LoginActionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");

        String password = req.getParameter("password");

        //System.out.println(username);
        //System.out.println(password);

        if (username.equals("zpf") && password.equals("123")) {
            User user = new User(username, password);
            System.out.println(user.toString());

            HttpSession session =req.getSession();

            System.out.println(session.toString());
            //System.out.println(req.toString());

            session.setAttribute("user", user);


            System.out.println(session.toString());
            //req.saveToRequest(session);
            System.out.println(req.getSession());
            resp.sendRedirect("profile-action");
            //resp.sendRedirect("index.html");
        } else {
            resp.sendRedirect("login.html");
        }
    }
}
