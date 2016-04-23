package edu.rit.csci729.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.rit.csci729.Engine;
import edu.rit.csci729.model.FieldConnection;
import edu.rit.csci729.model.MappingSource;
import edu.rit.csci729.model.NoMappingFound;
import edu.rit.csci729.model.Operation;
import edu.rit.csci729.model.OperationCollection;

@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping(method = RequestMethod.GET)
	public String index() throws NoMappingFound {
		return "index";
	}

	public String operationBreakDown(Model model, String input, Double threshold) {
		if (input == null)
			return "redirect:/";
		if (threshold == null)
			threshold = 0.9d;
		// starting the process
		String[] values = removeDups(input.split(","));
		Map<MappingSource, String> startingPoint = new HashMap<MappingSource, String>();
		for (String v : values) {
			String[] name_type = v.split(":");
			startingPoint.put(new MappingSource("Initial", name_type[0]), name_type[1]);
		}
		model.addAttribute("result",
				generateResult(startingPoint, new ArrayList<List<Operation>>(), new HashSet<Operation>(), threshold));
		return "breakdown";
	}

	private List<List<Operation>> generateResult(Map<MappingSource, String> holding,
			List<List<Operation>> execution, Set<Operation> used, double threshold) {
		HashMap<MappingSource,String> toAdd = new HashMap<MappingSource, String>();
		execution.add(new ArrayList<Operation>());
		boolean added = false;
		for(Operation oper : OperationCollection.get()){
			if(!used.contains(oper)){
				try{
					Engine eng = new Engine(null, oper.getServiceName());
					Map<MappingSource, String> operMap = oper.getInputMap();
					List<FieldConnection> result = eng.mapGeneratService(holding, operMap, threshold,true);
					for(FieldConnection fc : result){
						MappingSource ms = new MappingSource(fc.toConnection, fc.toConnectionName);
						toAdd.put(ms, operMap.get(ms));
					}
					execution.get(execution.size()-1).add(oper);
					added = true;
				}catch(NoMappingFound e){
					
				}
			}
		}
		if(!added){
			execution.remove(execution.size()-1);
		}else{
			addTo(holding, toAdd);
			return generateResult(holding, execution, used, threshold);
		}
		return execution;
	}

	private String[] removeDups(String[] split) {
		HashSet<String> set = new HashSet<String>();
		for (String s : split)
			set.add(s.trim());
		return set.toArray(new String[set.size()]);
	}

	private Map<MappingSource, String> addTo(Map<MappingSource, String> current, Map<MappingSource, String> toAdd) {
		for (Entry<MappingSource, String> ent : toAdd.entrySet()) {
			current.put(ent.getKey(), ent.getValue());
		}
		return current;
	}

}
