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
import edu.rit.csci729.model.Tuple;

@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping(method = RequestMethod.GET)
	public String index() throws NoMappingFound {
		return "index";
	}
	
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	public String addOperation(String inputs, String outputs, String name){
		String[] in = inputs.split(",");
		Operation oper = new Operation();
		HashMap<String,String> inp = new HashMap<String, String>();
		for(String s : in){
			String[] nt = s.split(":");
			inp.put(nt[0], nt[1]);
		}
		String[] out = inputs.split(",");
		HashMap<String,String> outp = new HashMap<String, String>();
		for(String s : out){
			String[] nt = s.split(":");
			outp.put(nt[0], nt[1]);
		}
		oper.setOperationName(name);
		oper.setInput(inp);
		oper.setOutput(outp);
		OperationCollection.get().add(oper);		
		return "redirect:/";
	}

	@RequestMapping(value = "/gen", method = RequestMethod.GET)
	public String operationBreakDown(Model model, String input, Double threshold) {
		if (input == null)
			return "redirect:/";
		if (threshold == null)
			threshold = 0.9d;
		// starting the process
		model.addAttribute("starting", input);
		String[] values = removeDups(input.split(","));
		Map<MappingSource, String> startingPoint = new HashMap<MappingSource, String>();
		for (String v : values) {
			String[] name_type = v.split(":");
			startingPoint.put(new MappingSource("Initial." + name_type[0], name_type[1]), name_type[0]);
		}
		model.addAttribute("result", generateResult(startingPoint,
				new ArrayList<List<Tuple<List<FieldConnection>, Operation>>>(), new HashSet<Operation>(), threshold));
		return "breakdown";
	}

	private List<List<Tuple<List<FieldConnection>, Operation>>> generateResult(Map<MappingSource, String> holding,
			List<List<Tuple<List<FieldConnection>, Operation>>> execution, Set<Operation> used, double threshold) {
		HashMap<MappingSource, String> toAdd = new HashMap<MappingSource, String>();
		execution.add(new ArrayList<Tuple<List<FieldConnection>, Operation>>());
		boolean added = false;
		for (Operation oper : OperationCollection.get()) {
			if (!used.contains(oper)) {
				Engine eng = new Engine(null, oper.getServiceName());
				Map<MappingSource, String> operMap = oper.getInputMap();
				try {
					printMap(holding);
					List<FieldConnection> result = eng.mapGeneratService(holding, operMap, threshold, true);
					for (FieldConnection fc : result) {
						System.out.println(":: " + fc.fromConnectionName + " <==> " + fc.toConnectionName + " === "
								+ fc.qualityOfConnection);
					}
					for (Entry<MappingSource, String> ent : oper.getOutputMap().entrySet()) {
						toAdd.put(ent.getKey(), ent.getValue());
					}
					System.out.println("Adding: " + oper.getOperationName());
					execution.get(execution.size() - 1).add(new Tuple<List<FieldConnection>, Operation>(result, oper));
					used.add(oper);
					added = true;
				} catch (NoMappingFound e) {
					System.out.println("Failed to Match: " + oper.getOperationName());
					try {
						List<FieldConnection> result = eng.mapGeneratService(holding, operMap, threshold, false);
						for (FieldConnection fc : result) {
							System.out.println(":: " + fc.fromConnectionName + " <==> " + fc.toConnectionName + " === "
									+ fc.qualityOfConnection);
						}
					} catch (NoMappingFound e1) {
					}
				}
			}
			System.out.println("[][][][][][][][][][]");
		}
		System.out.println("===================");
		if (!added) {
			execution.remove(execution.size() - 1);
		} else {
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

	private void printMap(Map<MappingSource, String> map) {
		System.out.println("------------------");
		for (Entry<MappingSource, String> ent : map.entrySet()) {
			System.out.println(ent.getKey().source + " : " + ent.getKey().type + " : " + ent.getValue());
		}
		System.out.println("------------------");
	}

}
