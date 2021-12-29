package mvcTest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/singers")
@ComponentScan
@Controller
public class SingerController {

    @Autowired
    private SingersService service;

    @RequestMapping(method = RequestMethod.GET)
    public String list(HttpSession httpSession, Model model, HttpServletRequest request) {
	List<Singer> res = service.findAll();
	model.addAttribute("list", res);
	try {
	    for (Cookie c : request.getCookies()) {
		System.err.println(c.getName() + " <-> " + c.getValue());
	    }
	} catch (Exception e) {
	    // TODO: handle exception
	}
	;
	return "singers/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable Long id, HttpSession httpSession, Model model) {
	Singer s = service.findById(id);
	model.addAttribute("singer", s);
	return "singers/show";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public String update(Singer singer, @PathVariable Long id, HttpSession httpSession,
	    @RequestPart("miltipartImage") MultipartFile image, HttpServletRequest request) {
	try {
	    if (!image.isEmpty()) {
		singer.setImage(java.util.Base64.getEncoder().encodeToString(image.getBytes()));
	    } else {
		// singer.setImage(service.findById(singer.getId()).getImage());
		singer.setImage(request.getParameter("oldImage"));
	    }
	    // singer.setImage(java.util.Base64.getEncoder().encodeToString(image.getBytes()));
	} catch (Exception e) {
	    e.printStackTrace();
	    return "singers/error";
	}
	service.merge(singer);
	return "redirect:/singers/" + singer.getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RolesAllowed("ADMIN")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String updateForm(@PathVariable Long id, HttpSession httpSession, Model model) {
	Singer s = service.findById(id);
	model.addAttribute("singer", s);
	return "singers/update";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(HttpSession httpSession, Singer singer, @RequestPart("miltipartImage") MultipartFile image,
	    Model model) {
	try {
	    if (singer.getImage() == "") {
		singer.setImage(java.util.Base64.getEncoder().encodeToString(image.getBytes()));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return "singers/error";
	}
	service.save(singer);
	return "redirect:/singers/" + singer.getId();
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String createForm(HttpSession httpSession, Model model) {
	Singer singer = new Singer();
	model.addAttribute("singer", singer);
	return "singers/update";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(HttpSession httpSession, @PathVariable Long id, Model model) {
	// System.err.println(id);
	service.delete(id);
	return "redirect:/singers";
    }

    @GetMapping(value = "/login")
    public String loginForm(HttpSession httpSession, Singer singer, Model model) {
	model.addAttribute("singer", singer);
	return "singers/login";
    }

    @GetMapping(path = "/error")
    public String error(HttpSession httpSession) {
	return "singers/error";
    }

    @GetMapping(path = "/websocket")
    // just GET request for getting page with websocket code
    public String webSocket() {
	return "singers/websocket";
    }

    @PostMapping
    public String showText() {
	System.err.println("TEXT");
	return "singers/list";
    }
}