package com.example.p320_22.model;

/** A person that contributed to the production of a movie. Can either
 *  act in, produce, or direct a movie
 */
public class Contributor {
	private int id;
	private String name;

	public Contributor(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/** Getters */
	public int getId() { return this.id; }
	public String getName() { return this.name; }
}
