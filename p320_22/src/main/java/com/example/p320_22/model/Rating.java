package com.example.p320_22.model;

/** Defines a rating for a movie */
public enum Rating {
	G("G"),
	PG("PG"),
	PG13("PG-13"),
	R("R"),
	X("X"),
	NR("NR"),
	NC17("NC-17"),;

	private final String value;

    Rating(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

	@Override
	public String toString() {
		return value;
	}

	public static Rating fromString(String s) {
		for (Rating rating : Rating.values()) {
            if (rating.value.equalsIgnoreCase(s)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Unknown rating: " + s);
	}
}
