package de.tum.imomesa.database.changes;

public class ReleaseChange extends Change {

	public ReleaseChange(double client, double key, long timestamp) {
		super(client, key, timestamp, "Object released.");
	}

}
