package code;

public class TurnOverResultBean {
	private String maxReactionTime;
	private String originalX;
	private String position;
	private String reaction;
	private String time;

	public TurnOverResultBean(String time, String position, String originalX) {
		super();
		this.originalX = originalX;
		this.position = position;
		this.time = time;
	}

	public String getMaxReactionTime() {
		return maxReactionTime;
	}

	public String getOriginalX() {
		return originalX;
	}

	public String getPosition() {
		return position;
	}

	public String getReaction() {
		return reaction;
	}

	public String getReactionMultiple30() {
		return reaction.compareTo("") == 0 ? "" : String.valueOf(Float
				.parseFloat(reaction) * 30);
	}

	public String getReactionCal() {
		return TurnOver.getReactionCal(reaction);
	}

	public String getTime() {
		return time;
	}

	public Integer getTimePlus1() {
		return Integer.parseInt(time) + 1;
	}

	public void setMaxReactionTime(String maxReactionTime) {
		this.maxReactionTime = maxReactionTime;
	}

	public Integer getMaxReactionTimePlus1() {
		return Integer.parseInt(maxReactionTime) + 1;
	}

	public void setOriginalX(String originalX) {
		this.originalX = originalX;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(System.getProperty("line.separator"));
		result.append(" position: " + position + ",");
		result.append(" time: " + time + ",");
		result.append(" originalX: " + originalX);

		return result.toString();
	}
}
