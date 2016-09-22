package code;
public class TurnOverDataSrcBean {
	private Float reaction;

	private Integer time;

	private Float x;

	private Float y;

	public Float getReaction() {
		return reaction;
	}

	public Float getReactionMultiple30() {
		return reaction * 30;
	}

	public Float getReactionCal() {
		if (TurnOver.CalMode.compareTo("M")==0) {
			return reaction * TurnOver.CalVal;
		}
		if (TurnOver.CalMode.compareTo("D")==0) {
			return reaction / TurnOver.CalVal;
		}
		return null;
	}

	public Integer getTime() {
		return time;
	}

	public Float getX() {
		return x;
	}

	public Float getY() {
		return y;
	}

	public void setReaction(Float reaction) {
		this.reaction = reaction;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public void setX(Float x) {
		this.x = x;
	}

	public void setY(Float y) {
		this.y = y;
	}

}
