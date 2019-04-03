package capstone.project.curl.Models;

public class TextMessageModel {
    public final String phoneNumber;
    public final String textMessage;

    public TextMessageModel(String phoneNumber, String textMessage) {
        this.phoneNumber = phoneNumber;
        this.textMessage = textMessage;
    }
}
