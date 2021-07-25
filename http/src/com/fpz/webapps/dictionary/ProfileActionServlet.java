package com.fpz.webapps.dictionary;

import com.fpz.standard.ServletException;
import com.fpz.standard.http.HttpServlet;
import com.fpz.standard.http.HttpServletRequest;
import com.fpz.standard.http.HttpServletResponse;
import com.fpz.standard.http.HttpSession;

import java.io.IOException;

public class ProfileActionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        //空session
        System.out.println(session.toString());
        //空user
        User user = (User) session.getAttribute("user");
        //System.out.println(user.toString());
        if (user == null) {
            resp.sendRedirect("login.html");
        } else {
            resp.setContentType("text/plain");
            resp.getWriter().println(user.toString());
        }
    }
}
