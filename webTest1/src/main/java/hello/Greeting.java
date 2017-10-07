package hello;

public class Greeting {

  private long id;
  private String content;

  public Greeting(long id, String content) {
    this.id = id;
    this.content = content;
  }

  public Greeting() {
    super();
    // TODO Auto-generated constructor stub
  }

  public long getId() {
    return id;
  }

  public String getContent() {
    return content;
  }
}
