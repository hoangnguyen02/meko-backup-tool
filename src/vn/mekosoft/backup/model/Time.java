package vn.mekosoft.backup.model;

public enum Time {
	SECOND(1),
	MINUTE(60),
	HOUR(3600);
	

    private int seconds;

    Time(int seconds) {
        this.seconds = seconds;
    }

    public int toSeconds() {
        return this.seconds;
    }

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	

}
