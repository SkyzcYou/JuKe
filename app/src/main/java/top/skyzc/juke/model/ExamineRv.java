package top.skyzc.juke.model;

public class ExamineRv {
    private String username;
    private String phoneNumber;
    private String level;
    private int imageUrl;

    public ExamineRv(String username, String phoneNumber, String level, int imageUrl) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.level = level;
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
