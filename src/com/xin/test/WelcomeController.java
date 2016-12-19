package com.xin.test;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Spring MVC测试
 * 1、环境搭建,helloWorld测试
 * 2、获取请求参数的方式
 * 3、向页面传递参数的方式
 * 4、2和3可以一起灵活运用
 * @author dell
 *
 */
@Controller
public class WelcomeController{

	//访问下面方法地址：http://localhost:8080/Spring-mvc/login/wuxin
	@RequestMapping(value = "/login/{user}", method = RequestMethod.GET)
	public ModelAndView myMethod(HttpServletRequest request,HttpServletResponse response, 
			@PathVariable("user") String user, ModelMap modelMap) throws Exception {
		modelMap.put("message", user);
		return new ModelAndView("/hello", modelMap);
	}
	
	//访问下面方法地址：http://localhost:8080/Spring-mvc/hello
	@RequestMapping(value="/hello1",method=RequestMethod.GET)
	public String test1(){
		return "index";
	}
	
	//以下是获取请求参数
	/**
	 * 通过@PathVariabl注解获取路径中传递参数
	 * 通过ModelMap向页面传值
	 * 访问下面方法地址：http://localhost:8080/Spring-mvc/hello.do/1/xin
	 * @param id
	 * @param userName
	 * @return
	 */
	@RequestMapping(value="/hello2.do/{id}/{userName}")
	public String test2(@PathVariable int id,@PathVariable String userName){
		System.out.println("id="+id+",userName="+userName);
		return "hello";
	}
	
	/**
	 * 直接用HttpServletRequest获取
	 * 访问下面方法地址：http://localhost:8080/Spring-mvc/hello3.do?id=2&userName=xin
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/hello3.do")
	public String test3(HttpServletRequest request,HttpServletResponse response){
		int id=Integer.parseInt(request.getParameter("id"));
		String userName=request.getParameter("userName");
		System.out.println("id="+id+",userName="+userName);
		return "hello";
	}
	
	/**
	 * 用注解@RequestParam绑定请求参数id,userName到变量id,userName
	 * 当请求参数userName不存在时会有异常发生,可以通过设置属性required=false解决,
	 * 例如: @RequestParam(value="userName",required=false)，请求参数属性为userName
	 * 		String userName:接收参数属性为userName
	 * 访问下面方法地址：http://localhost:8080/Spring-mvc/hello4.do?id=2&userName=xin
	 * @param id
	 * @param userName
	 * @return
	 */
	@RequestMapping(value="/hello4.do")
	public String test4(@RequestParam("id")int id,@RequestParam(value="userName",required=false)String userName){
		System.out.println("id="+id+",userName="+userName);
		return "hello";
	}
	
	/**
	 * 以下方式可使用于表单提交
	 * 自动注入Bean属性(用@ModelAttribute注解获取POST请求的FORM表单数据)
	 * 访问下面方法地址：http://localhost:8080/Spring-mvc/hello5.do?id=2&userName=xin
	 * 经测试发现，参数中没有@ModelAttribute("user")也能够进行自动注入
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/hello5.do")
	public String test5(@ModelAttribute("user")User user){
		System.out.println("id="+user.getId()+",userName="+user.getUserName());
		return "hello";
	}
	
	//以下是向页面传递参数
	/**
	 * ModelAndView数据会利用HttpServletRequest的Attribute传值到hello.jsp中
	 * 访问下面方法地址：http://localhost:8080/Spring-mvc/hello6.do
	 */
    @RequestMapping(value="/hello6.do")
	public ModelAndView test6(){
		Map<String,Object> data=new HashMap<String,Object>();
		data.put("message", "test6-->xin");
		return new ModelAndView("/hello", data);
	}
    
    /**
     * 使用ModelMap作为参数对象示例:
	   ModelMap数据会利用HttpServletRequest的Attribute传值到hello.jsp中
	        访问下面方法地址：http://localhost:8080/Spring-mvc/hello7.do
     * @return
     */
    @RequestMapping(value="/hello7.do")
    public String test7(ModelMap map){
    	//以下两种方式都可以
    	//map.addAttribute("message", "test7-->xin");
    	map.put("message", "test7-->xin");
    	return "hello";
    }
    
    /**
     * 使用@ModelAttribute示例
	         在Controller方法的参数部分或Bean属性方法上使用
	   @ModelAttribute数据会利用HttpServletRequest的Attribute传值到success.jsp中
	       访问下面方法地址：http://localhost:8080/Spring-mvc/hello8.do
     * @param user
     * @return
     */
    @RequestMapping(value="/hello8.do")
    public String test8(@ModelAttribute("user")User user){
    	user.setUserName("test8-->xin");
    	return "hello";
    }
    
    /**
     * Session存储：可以利用HttpServletReequest的getSession()方法
     * 访问下面方法地址：http://localhost:8080/Spring-mvc/hello9.do
     * @return
     */
    @RequestMapping(value="/hello9.do")
    public String test9(HttpServletRequest request){
    	String userName=request.getParameter("userName");
    	request.setAttribute("message", userName);
    	//request.getSession().setAttribute("message", "test9-->xin");
    	return "hello";
    }
    
    //测试重定向
    //Spring MVC 默认采用的是转发来定位视图，如果要使用重定向，可以如下操作:1，使用RedirectView  2，使用redirect:前缀
    @RequestMapping(value="/hello10.do")
	public ModelAndView test10(){
		RedirectView view=new RedirectView("hello9.do");
		return new ModelAndView(view);
	}
	
	//工作中常用的方法
    @RequestMapping(value="/hello11.do")
	public String test11(){
		return "redirect:hello9.do";  
	}
}
