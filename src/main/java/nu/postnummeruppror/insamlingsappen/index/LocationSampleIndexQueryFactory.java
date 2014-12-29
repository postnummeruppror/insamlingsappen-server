package nu.postnummeruppror.insamlingsappen.index;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * @author kalle
 * @since 2014-12-29 09:54
 */
public class LocationSampleIndexQueryFactory {


  public Query postalCode(String postalCode) {
    return new TermQuery(new Term(LocationSampleIndexFields.postalCode, postalCode));
  }

  public Query postalTown(String postalTown) {
    return new TermQuery(new Term(LocationSampleIndexFields.postalTown, postalTown.toUpperCase()));
  }


}
