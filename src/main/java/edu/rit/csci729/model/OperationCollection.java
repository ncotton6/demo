package edu.rit.csci729.model;

import java.util.ArrayList;
import java.util.HashMap;

public class OperationCollection extends ArrayList<Operation> {

	private static OperationCollection coll = null;

	private OperationCollection() {
		super();
	}

	public static OperationCollection get() {
		if (coll == null)
			synchronized (OperationCollection.class) {
				if (coll == null){
					coll = new OperationCollection();
					setup();
				}
			}
		return coll;
	}

	private static void setup() {
		OperationCollection oc = coll;
		// Job service
		Operation job = new Operation(new HashMap<String, String>() {
			{
				put("salary", "double");
				put("zip", "string");
			}
		}, new HashMap<String, String>() {
			{
				put("job", "string");
				put("salary", "double");
				put("company", "title");
			}
		});
		job.setOperationName("JobSearch");
		oc.add(job);
		// company rating
		Operation company = new Operation(new HashMap<String, String>() {
			{
				put("organization", "string");
			}
		}, new HashMap<String, String>() {
			{
				put("companyRating", "double");
				put("ceoApproval", "double");
				put("ceo", "string");
			}
		});
		company.setOperationName("OrganizationSearch");
		oc.add(company);
		// job info
		Operation jobInfo = new Operation(new HashMap<String, String>() {
			{
				put("profession", "string");
				put("postcode", "string");
			}
		}, new HashMap<String, String>() {
			{
				put("average_salary", "double");
				put("happiness", "double");
			}
		});
		jobInfo.setOperationName("JobInfo");
		oc.add(jobInfo);
	}

}
