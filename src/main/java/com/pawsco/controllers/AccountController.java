package com.pawsco.controllers;

import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pawsco.business.User;
import com.pawsco.data.UserDB;

@Controller
//@RequestMapping(value = "/register")
public class AccountController {
	
	@Autowired
	public User user;
	@Autowired
	public UserDB userDB;
	
	
	@GetMapping("/myAccount")
	public String myAccount() {
		return "myAccount";
	}
	
	@GetMapping(value="/register")
	public String handleGetRegistration(Model model) {
		model.addAttribute("register", new User());
		return "register";
	}
	
	@PostMapping(value="register")
	public String handlePostRegistration(HttpServletRequest request, 
			HttpServletResponse response, @Valid @ModelAttribute("register") User user) throws SQLException {
		
		return registerUser(request, response);
	}
	
	
	private String registerUser(HttpServletRequest request,
            HttpServletResponse response) throws SQLException {

		String url = null;
        // get the user data
       String email = request.getParameter("email");
       String firstName = request.getParameter("firstName");
       String lastName = request.getParameter("lastName");
       String password = request.getParameter("password");

       // store the data in a User object
       User user = new User();
       user.setEmail(email);
       user.setFirstName(firstName);
       user.setLastName(lastName);
       user.setPassword(password);

       // store the User object as a session attribute
       HttpSession session = request.getSession();
       session.setAttribute("user", user);

       // add a cookie that stores the user's email to browser
       Cookie c = new Cookie("userEmail", email);
       c.setMaxAge(60 * 60 * 24 * 365 * 3); // set age to 3 years
       c.setPath("/"); // allow entire app to access it
       response.addCookie(c);
        String message;
		//check that email address doesn't already exist
        if (UserDB.emailExists(email)) {
            message = "This email address already exists. <br>"
                    + "Please enter another email address.";
            request.setAttribute("message", message);
            url = "/register.jsp";
        } else {
            UserDB.insert(user);
            message = "user created";
            request.setAttribute("message", message);
            url = "/home";
        }
        return url;
   }
}
