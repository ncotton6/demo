package edu.rit.csci729.demo.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.rit.csci729.Engine;
import edu.rit.csci729.model.FieldConnection;
import edu.rit.csci729.model.NoMappingFound;
import edu.rit.csci729.model.Operation;

@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping(method = RequestMethod.GET)
	public String index() throws NoMappingFound {
		//
		//Test cases
		Engine eng = new Engine(null,null);
		Operation from = new Operation();
		from.setOutput(new HashMap<String,String>(){{put("zip", "string");}});
		Operation to = new Operation();
		to.setInput(new HashMap<String, String>(){{put("zip","string");}});
		List<FieldConnection> result = eng.generateMapping(from, to, 0.9d, true);
		System.out.println(result.get(0).fromConnection);		
		//
		return "index";
	}

}
