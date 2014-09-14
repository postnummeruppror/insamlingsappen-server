package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-09-14 09:56
 */
public class LocationSampleUpdate implements Serializable {

  private static final long serialVersionUID = 1l;

  /** The sample this is an update on */
  private LocationSample locationSample;

  /** Identity of this update */
  private Long identity;

  /** Author of this update */
  private Account account;

  /**
   * android, ios, webapp, etc
   */
  private String application;

  /** 0.0.1 or what not */
  private String applicationVersion;

  /**
   * gps, network, wifi, human, etc.
   */
  private String provider;

  private long timestamp;

  private double latitude;
  private double longitude;

  private Double accuracy;
  private Double altitude;

  private String postalCode;
  private String streetName;
  private String houseNumber;
  private String houseName;
  private String postalTown;


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LocationSample{");
    sb.append("identity=").append(identity);
    sb.append(", account.identity=").append(account.getIdentity());
    sb.append(", application='").append(application).append('\'');
    sb.append(", applicationVersion='").append(applicationVersion).append('\'');
    sb.append(", provider='").append(provider).append('\'');
    sb.append(", timestamp=").append(timestamp);
    sb.append(", latitude=").append(latitude);
    sb.append(", longitude=").append(longitude);
    sb.append(", accuracy=").append(accuracy);
    sb.append(", altitude=").append(altitude);
    sb.append(", postalCode='").append(postalCode).append('\'');
    sb.append(", streetName='").append(streetName).append('\'');
    sb.append(", houseNumber='").append(houseNumber).append('\'');
    sb.append(", houseName='").append(houseName).append('\'');
    sb.append(", postalTown='").append(postalTown).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
