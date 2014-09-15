package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-09-15 10:30
 */
public class PostalAddress implements Serializable {

  private static final long serialVersionUID = 1l;

  private String streetName;
  private String houseNumber;
  private String houseName;
  private String postalCode;
  private String postalTown;

  @Override
  public String toString() {
    return "PostalAddress{" +
        "streetName='" + streetName + '\'' +
        ", houseNumber='" + houseNumber + '\'' +
        ", houseName='" + houseName + '\'' +
        ", postalCode='" + postalCode + '\'' +
        ", postalTown='" + postalTown + '\'' +
        '}';
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  public String getHouseName() {
    return houseName;
  }

  public void setHouseName(String houseName) {
    this.houseName = houseName;
  }

  public String getPostalTown() {
    return postalTown;
  }

  public void setPostalTown(String postalTown) {
    this.postalTown = postalTown;
  }
}
