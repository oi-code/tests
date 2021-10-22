package mvcTest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequestMapping("/singers")
@Controller
@ComponentScan
public class SingerController {

    @Autowired
    private SingersService service;

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model) {
	List<Singer> res = service.findAll();
	res.stream().forEach(e -> {
	    setB64code(e);
	});
	model.addAttribute("list", res);
	return "singers/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable Long id, Model model) {
	Singer s = service.findById(id);
	setB64code(s);
	model.addAttribute("singer", s);
	return "singers/show";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public String update(Singer singer, BindingResult bindingResult, HttpServletRequest request,
	    RedirectAttributes attributes, Model model, Part part) {
	model.asMap().clear();
	setBytesFromRequest(singer, request);
	service.save(singer);
	return "redirect:/singers/" + singer.getId();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String updateForm(@PathVariable Long id, Model model) {
	Singer s = service.findById(id);
	if (s == null) {
	    s = new Singer();
	} else {
	    setB64code(s);
	}
	model.addAttribute("singer", s);
	return "singers/update";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(Singer singer, BindingResult bindingResult, HttpServletRequest request,
	    RedirectAttributes attributes, Model model) {
	setBytesFromRequest(singer, request);
	service.save(singer);
	return "redirect:/singers/" + singer.getId();
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String createForm(Model model) {
	Singer singer = new Singer();
	model.addAttribute("singer", singer);
	return "singers/update";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(@PathVariable Long id, Model model) {
	System.err.println(id);
	service.delete(id);
	return "redirect:/singers";
    }
    
    @GetMapping(value = "/login")
    public String loginForm(Singer singer, Model model) {
	model.addAttribute("singer", singer);
	return "singers/login";
    }
    
    @GetMapping(path = "/error")
    public String error() {
	return "singers/error";
    }    
    
    @GetMapping(path = "/websocket")
    //just GET request for getting page with websocket code
    public String webSocket() {
	return "singers/websocket";
    }

    private void setB64code(Singer e) {
	byte b[] = e.getImage();
	if (b == null) {
	    return;
	}
	String result = Base64.getEncoder().encodeToString(b);
	e.setB64i(result);
    }

    private void setBytesFromRequest(Singer singer, HttpServletRequest request) {
	try {
	    Part part = request.getPart("image");
	    InputStream content = part.getInputStream();
	    BufferedImage img = ImageIO.read(content);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(img, "png", baos);
	    // Files.write(Paths.get(System.getProperty("user.home")+"\\desktop\\file.png"),baos.toByteArray());
	    singer.setImage(baos.toByteArray());
	} catch (Exception e) {
	    // e.printStackTrace();
	    System.err.println("no image");
	}
    }
}