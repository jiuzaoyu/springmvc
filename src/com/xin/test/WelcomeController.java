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
 * Spring MVC����
 * 1�������,helloWorld����
 * 2����ȡ��������ķ�ʽ
 * 3����ҳ�洫�ݲ����ķ�ʽ
 * 4��2��3����һ���������
 * @author dell
 *
 */
@Controller
public class WelcomeController{

	//�������淽����ַ��http://localhost:8080/Spring-mvc/login/wuxin
	@RequestMapping(value = "/login/{user}", method = RequestMethod.GET)
	public ModelAndView myMethod(HttpServletRequest request,HttpServletResponse response, 
			@PathVariable("user") String user, ModelMap modelMap) throws Exception {
		modelMap.put("message", user);
		return new ModelAndView("/hello", modelMap);
	}
	
	//�������淽����ַ��http://localhost:8080/Spring-mvc/hello
	@RequestMapping(value="/hello1",method=RequestMethod.GET)
	public String test1(){
		return "index";
	}
	
	//�����ǻ�ȡ�������
	/**
	 * ͨ��@PathVariablע���ȡ·���д��ݲ���
	 * ͨ��ModelMap��ҳ�洫ֵ
	 * �������淽����ַ��http://localhost:8080/Spring-mvc/hello.do/1/xin
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
	 * ֱ����HttpServletRequest��ȡ
	 * �������淽����ַ��http://localhost:8080/Spring-mvc/hello3.do?id=2&userName=xin
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
	 * ��ע��@RequestParam���������id,userName������id,userName
	 * ���������userName������ʱ�����쳣����,����ͨ����������required=false���,
	 * ����: @RequestParam(value="userName",required=false)�������������ΪuserName
	 * 		String userName:���ղ�������ΪuserName
	 * �������淽����ַ��http://localhost:8080/Spring-mvc/hello4.do?id=2&userName=xin
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
	 * ���·�ʽ��ʹ���ڱ��ύ
	 * �Զ�ע��Bean����(��@ModelAttributeע���ȡPOST�����FORM������)
	 * �������淽����ַ��http://localhost:8080/Spring-mvc/hello5.do?id=2&userName=xin
	 * �����Է��֣�������û��@ModelAttribute("user")Ҳ�ܹ������Զ�ע��
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/hello5.do")
	public String test5(@ModelAttribute("user")User user){
		System.out.println("id="+user.getId()+",userName="+user.getUserName());
		return "hello";
	}
	
	//��������ҳ�洫�ݲ���
	/**
	 * ModelAndView���ݻ�����HttpServletRequest��Attribute��ֵ��hello.jsp��
	 * �������淽����ַ��http://localhost:8080/Spring-mvc/hello6.do
	 */
    @RequestMapping(value="/hello6.do")
	public ModelAndView test6(){
		Map<String,Object> data=new HashMap<String,Object>();
		data.put("message", "test6-->xin");
		return new ModelAndView("/hello", data);
	}
    
    /**
     * ʹ��ModelMap��Ϊ��������ʾ��:
	   ModelMap���ݻ�����HttpServletRequest��Attribute��ֵ��hello.jsp��
	        �������淽����ַ��http://localhost:8080/Spring-mvc/hello7.do
     * @return
     */
    @RequestMapping(value="/hello7.do")
    public String test7(ModelMap map){
    	//�������ַ�ʽ������
    	//map.addAttribute("message", "test7-->xin");
    	map.put("message", "test7-->xin");
    	return "hello";
    }
    
    /**
     * ʹ��@ModelAttributeʾ��
	         ��Controller�����Ĳ������ֻ�Bean���Է�����ʹ��
	   @ModelAttribute���ݻ�����HttpServletRequest��Attribute��ֵ��success.jsp��
	       �������淽����ַ��http://localhost:8080/Spring-mvc/hello8.do
     * @param user
     * @return
     */
    @RequestMapping(value="/hello8.do")
    public String test8(@ModelAttribute("user")User user){
    	user.setUserName("test8-->xin");
    	return "hello";
    }
    
    /**
     * Session�洢����������HttpServletReequest��getSession()����
     * �������淽����ַ��http://localhost:8080/Spring-mvc/hello9.do
     * @return
     */
    @RequestMapping(value="/hello9.do")
    public String test9(HttpServletRequest request){
    	String userName=request.getParameter("userName");
    	request.setAttribute("message", userName);
    	//request.getSession().setAttribute("message", "test9-->xin");
    	return "hello";
    }
    
    //�����ض���
    //Spring MVC Ĭ�ϲ��õ���ת������λ��ͼ�����Ҫʹ���ض��򣬿������²���:1��ʹ��RedirectView  2��ʹ��redirect:ǰ׺
    @RequestMapping(value="/hello10.do")
	public ModelAndView test10(){
		RedirectView view=new RedirectView("hello9.do");
		return new ModelAndView(view);
	}
	
	//�����г��õķ���
    @RequestMapping(value="/hello11.do")
	public String test11(){
		return "redirect:hello9.do";  
	}
}
