package models;

public class Hitted {
	private HIT_TYPE hitType;
	private String id;

	public Hitted(HIT_TYPE hitType, String id) {
		super();
		this.hitType = hitType;
		this.id = id;
	}

	public HIT_TYPE getHitType() {
		return hitType;
	}

	public void setHitType(HIT_TYPE hitType) {
		this.hitType = hitType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
