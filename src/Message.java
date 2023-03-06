public class Message {
    private User user;
    private String content;

    public Message(User u, String c)
    {
        this.user = u;
        this.content = c;
    }

    public User getUser() { return user; }

    public String getMessageContents() { return content; }

    @Override
    public String toString() {
        return String.format("%s: %s\n", user.getUsername(), content);
    }
}
