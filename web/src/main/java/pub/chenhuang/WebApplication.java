package pub.chenhuang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pub.chenhuang.pojo.User;
import pub.chenhuang.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@SpringBootApplication
@Controller
@ComponentScan(basePackages = {"pub.chenhuang.mapper", "pub.chenhuang.service","pub.chenhuang.web"})
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@Resource
	private UserService userService;

	@RequestMapping("/chen")
	@ResponseBody
	public User getUser(HttpServletRequest request){
		HttpSession session=request.getSession();


		return userService.getUserByName("admin");
	}
}
