package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-09-15 10:28
 */
public class Coordinate implements Serializable {

  private static final long serialVersionUID = 1l;

  /**
   * gps, network, wifi, human, etc.
   */
  private String provider;

  private Double latitude;
  private Double longitude;

  /** Accuracy in meters */
  private Double accuracy;

  /** Altitude in meters */
  private Double altitude;

  @Override
  public String toString() {
    return "Coordinate{" +
        "provider='" + provider + '\'' +
        ", latitude=" + latitude +
        ", longitude=" + longitude +
        ", accuracy=" + accuracy +
        ", altitude=" + altitude +
        '}';
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Double accuracy) {
    this.accuracy = accuracy;
  }

  public Double getAltitude() {
    return altitude;
  }

  public void setAltitude(Double altitude) {
    this.altitude = altitude;
  }
}
