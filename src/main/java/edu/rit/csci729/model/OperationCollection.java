package edu.rit.csci729.model;

import java.util.ArrayList;

public class OperationCollection extends ArrayList<Operation> {

	private static OperationCollection coll = null;

	private OperationCollection() {
		super();
	}

	public static OperationCollection get() {
		if (coll == null)
			synchronized (OperationCollection.class) {
				if (coll == null)
					coll = new OperationCollection();
			}
		return coll;
	}

}
