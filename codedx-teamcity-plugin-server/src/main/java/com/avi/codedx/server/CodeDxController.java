package com.avi.codedx.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/codedx")
public class CodeDxController {

	public CodeDxController(){}

	@RequestMapping(method = RequestMethod.GET)
	public Object getProjects() {
		return "hello";
	}
}
