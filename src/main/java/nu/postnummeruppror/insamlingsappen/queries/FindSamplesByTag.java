package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.*;

/**
 * @author kalle
 * @since 2017-12-12
 */
public class FindSamplesByTag implements Query<Root, Collection<LocationSample>> {

  private Map<String, String> mustTags = new HashMap<>();
  private Map<String, String> mustNotTags = new HashMap<>();

  public FindSamplesByTag() {
  }

  public FindSamplesByTag(String... mustKeyValues) {
    this(false, mustKeyValues);
  }

  public FindSamplesByTag(boolean includeDeprecated, String... mustKeyValues) {
    for (int i=0; i<mustKeyValues.length; i+=2) {
      mustTags.put(mustKeyValues[i], mustKeyValues[i+1]);
    }
    if (!includeDeprecated) {
      mustNotTags.put("deprecated", "true");
    }
  }

  @Override
  public Collection<LocationSample> query(Root root, Date date) throws Exception {
    Set<LocationSample> locationSamples = new HashSet<>();
    for (LocationSample locationSample : root.getLocationSamples().values()) {

      boolean match = true;
      if (!mustNotTags.isEmpty()) {
        for (Map.Entry<String, String> requiredMustNotTag : mustNotTags.entrySet()) {
          if (requiredMustNotTag.getValue().equals(locationSample.getTag(requiredMustNotTag.getKey()))) {
            match = false;
            break;
          }
        }
      }

      if (match) {
        for (Map.Entry<String, String> requiredTag : mustTags.entrySet()) {
          if (!requiredTag.getValue().equals(locationSample.getTag(requiredTag.getKey()))) {
            match = false;
            break;
          }
        }
      }

      if (match) {
        locationSamples.add(locationSample);
      }
    }

    return locationSamples;
  }

  public Map<String, String> getMustTags() {
    return mustTags;
  }

  public Map<String, String> getMustNotTags() {
    return mustNotTags;
  }
}
