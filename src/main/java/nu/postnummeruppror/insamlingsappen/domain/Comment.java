package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author kalle
 * @since 2014-09-14 17:22
 */
public class Comment  implements Serializable {

  private static final long serialVersionUID = 1l;



  private Long identity;

  private Long timestamp;

  private Account author;

  private String comment;

  private List<Comment> responses;

  public Account getAuthor() {
    return author;
  }

  public void setAuthor(Account author) {
    this.author = author;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public List<Comment> getResponses() {
    return responses;
  }

  public void setResponses(List<Comment> responses) {
    this.responses = responses;
  }

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }
}
