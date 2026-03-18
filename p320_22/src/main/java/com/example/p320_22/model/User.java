package com.example.p320_22.model;
import java.time.Instant;
import java.util.ArrayList;

public class User {

    private Instant creationDate;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Instant lastAccessDate;
    private ArrayList<Collection> collections;

    public User(Instant creationDate, String email, String username, String password, 
                    String firstName, String lastName, Instant lastAccessDate, ArrayList<Collection> collections) {
        this.creationDate = creationDate;
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastAccessDate = lastAccessDate;
        this.collections = collections;
    }

	/** Getters */
	public Instant getCreationDate() { return this.creationDate; }
	public String getEmail() { return this.email; }
	public String getUsername() { return this.username; }
	public String getPassword() { return this.password; }
	public String getFirstName() { return this.firstName; }
	public String getLastName() { return this.lastName; }
	public Instant getLastAccessDate() { return this.lastAccessDate; }
	public ArrayList<Collection> getCollections() { return this.collections; }

	public void setLastAccessDate(Instant lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	/** Add a collection to the user's list of collections */
	public void addCollection(Collection collection) {
		this.collections.add(collection);
	}

	/** Remove a collection from the user's list of collections */
	public void removeCollection(Collection collection) {
		this.collections.remove(collection);
	}
}
