package nu.postnummeruppror.insamlingsappen.index;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author kalle
 * @since 2014-09-07 03:33
 */
public class LocationSampleIndex {

  private static Logger log = LoggerFactory.getLogger(LocationSampleIndex.class);

  private Directory directory;
  private IndexWriter indexWriter;

  private SearcherManager searcherManager;

  private Analyzer analyzer = new KeywordAnalyzer();

  private Thread autocommitThread;

  public void open() throws Exception {
    File path = new File("data/index/location_samples");
    directory = FSDirectory.open(path);
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_45, analyzer);
    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    indexWriter = new IndexWriter(directory, indexWriterConfig);

    searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory());

    autocommitThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(1000 * 60);
          } catch (InterruptedException e) {
            log.warn("Caught interruption in autocommit thread", e);
            break;
          }
          try {
            commit();
          } catch (Exception e) {
            log.error("Error autocommitting", e);
          }
        }
      }
    });
    autocommitThread.setDaemon(true);
    autocommitThread.setName("Index autocommit");
    autocommitThread.start();

  }

  public void close() throws Exception {

    searcherManager.close();
    indexWriter.close();
    directory.close();
  }

  public void reconstruct() throws Exception {
    reconstruct(10);
  }

  public void commit() throws Exception {
    if (indexWriter.hasUncommittedChanges()) {
      log.info("Committing index...");
      getIndexWriter().commit();
      log.info("Committed!");
      getSearcherManager().maybeRefresh();
    }
  }

  public void reconstruct(int numberOfQueueUpdaterThreads) throws Exception {

    log.info("Reconstructing index using " + numberOfQueueUpdaterThreads + " threads.");

    final ConcurrentLinkedQueue<LocationSample> queue = new ConcurrentLinkedQueue<>();
    queue.addAll(Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values());

    Thread[] threads = new Thread[numberOfQueueUpdaterThreads];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          LocationSample locationSample;
          while ((locationSample = queue.poll()) != null) {
            try {
              update(locationSample);
            } catch (Exception e) {
              log.error("Exception while indexing " + locationSample, e);
            }
          }
        }
      });
      threads[i].start();
    }
    for (Thread thread : threads) {
      thread.join();
    }

    log.info("Index reconstructed!");


    commit();

  }


  public void update(LocationSample locationSample) throws Exception {
    indexWriter.deleteDocuments(new Term("identity[indexed]", String.valueOf(locationSample.getIdentity())));
    indexWriter.addDocument(documentFactory(locationSample));
  }

  public Document documentFactory(LocationSample locationSample) throws Exception {

    Document document = new Document();

    document.add(new StringField(LocationSampleIndexFields.identity_indexed, String.valueOf(locationSample.getIdentity()), Field.Store.NO));
    document.add(new NumericDocValuesField(LocationSampleIndexFields.identity_docValue, locationSample.getIdentity()));

    addField(document, LocationSampleIndexFields.timestamp, locationSample.getTimestamp(), null);


    if (locationSample.getCoordinate() != null) {
      addField(document, LocationSampleIndexFields.latitude, locationSample.getCoordinate().getLatitude(), null);
      addField(document, LocationSampleIndexFields.longitude, locationSample.getCoordinate().getLongitude(), null);
    }

    if (locationSample.getPostalAddress() != null) {
      addField(document, LocationSampleIndexFields.postalCode, locationSample.getPostalAddress().getPostalCode());
    }

    return document;
  }


  public String nullValue = "[null]";

  private void addField(Document document, String field, Enum value) {
    if (value != null) {
      document.add(new StringField(field, value.name(), Field.Store.NO));
    } else {
      document.add(new StringField(field, nullValue, Field.Store.NO));
    }
  }

  private void addField(Document document, String field, String value) {
    if (value != null) {
      document.add(new StringField(field, value, Field.Store.NO));
    } else {
      document.add(new StringField(field, nullValue, Field.Store.NO));
    }
  }

  private void addField(Document document, String field, Double value, Double nullValue) {
    if (value != null) {
      document.add(new DoubleField(field, value, Field.Store.NO));
    } else if (nullValue != null) {
      document.add(new DoubleField(field, nullValue, Field.Store.NO));
    } else {
      document.add(new StringField(field, this.nullValue, Field.Store.NO));
    }
  }

  private void addField(Document document, String field, Float value, Float nullValue) {
    if (value != null) {
      document.add(new FloatField(field, value, Field.Store.NO));
    } else if (nullValue != null) {
      document.add(new FloatField(field, nullValue, Field.Store.NO));
    } else {
      document.add(new StringField(field, this.nullValue, Field.Store.NO));
    }
  }

  private void addField(Document document, String field, Long value, Long nullValue) {
    if (value != null) {
      document.add(new LongField(field, value, Field.Store.NO));
    } else if (nullValue != null) {
      document.add(new LongField(field, nullValue, Field.Store.NO));
    } else {
      document.add(new StringField(field, this.nullValue, Field.Store.NO));
    }
  }

  private void addField(Document document, String field, Integer value, Integer nullValue) {
    if (value != null) {
      document.add(new IntField(field, value, Field.Store.NO));
    } else if (nullValue != null) {
      document.add(new IntField(field, nullValue, Field.Store.NO));
    } else {
      document.add(new StringField(field, this.nullValue, Field.Store.NO));
    }
  }

  public Map<LocationSample, Float> search(Query query) throws Exception {
    return search(query, false);
  }

  public Map<LocationSample, Float> search(Query query, final boolean doScore) throws Exception {

    long started = System.currentTimeMillis();

    final Map<LocationSample, Float> results = new HashMap<>();

    IndexSearcher searcher = getSearcherManager().acquire();
    try {

      searcher.search(query, new Collector() {
        private Scorer scorer;
        private AtomicReaderContext context;
        private NumericDocValues identities;

        @Override
        public void setScorer(Scorer scorer) throws IOException {
          this.scorer = scorer;
        }

        @Override
        public void collect(int doc) throws IOException {
          if (identities == null) {
            identities = context.reader().getNumericDocValues(LocationSampleIndexFields.identity_docValue);
          }
          long identity = identities.get(doc);
          LocationSample locationSample = Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().get(identity);
          results.put(locationSample, doScore ? scorer.score() : null);
        }

        @Override
        public void setNextReader(AtomicReaderContext context) throws IOException {
          this.context = context;
          this.identities = null;
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
          return true;
        }
      });

    } finally {
      getSearcherManager().release(searcher);
    }

    long millisecondsSpent = System.currentTimeMillis() - started;

    log.debug("Query executed in " + millisecondsSpent + " milliseconds and returned " + results.size() + " location samples.");

    return results;
  }

  public Directory getDirectory() {
    return directory;
  }

  public IndexWriter getIndexWriter() {
    return indexWriter;
  }

  public SearcherManager getSearcherManager() {
    return searcherManager;
  }

  public Analyzer getAnalyzer() {
    return analyzer;
  }


}
