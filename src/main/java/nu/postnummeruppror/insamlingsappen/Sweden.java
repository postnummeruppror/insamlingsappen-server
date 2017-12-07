package nu.postnummeruppror.insamlingsappen;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import se.kodapan.osm.domain.*;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2017-12-07
 */
public class Sweden {

  private GeometryFactory geometryFactory;

  private PojoRoot sweden;
  private List<LinearRing> swedenLinearRings;
  private MultiPolygon swedenMultipolygon;

  public Sweden(GeometryFactory geometryFactory) throws Exception {

    this.geometryFactory = geometryFactory;

    sweden = new PojoRoot();
    InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
    parser.setRoot(sweden);
    parser.parse(getClass().getResourceAsStream("/sverige-odbl.osm.xml"));

    swedenLinearRings = new ArrayList<>();

    Relation rootRelation = sweden.getRelation(52822);



    List<Node> nodes = new ArrayList<>();
    Node firstNode = null;
    for (RelationMembership membership : rootRelation.getMembers()) {

      if (!"outer".equalsIgnoreCase(membership.getRole())) {
        continue;
      }

      if (firstNode == null) {
        firstNode = membership.getObject().accept(new OsmObjectVisitor<Node>() {
          @Override
          public Node visit(Node node) {
            return node;
          }

          @Override
          public Node visit(Way way) {
            return way.getNodes().get(0);
          }

          @Override
          public Node visit(Relation relation) {
            return relation.accept(this);
          }
        });
      }

      nodes.addAll(membership.getObject().accept(new OsmObjectVisitor<List<Node>>() {
        @Override
        public List<Node> visit(Node node) {
          ArrayList<Node> nodes = new ArrayList<>(1);
          nodes.add(node);
          return nodes;
        }

        @Override
        public List<Node> visit(Way way) {
          return way.getNodes();
        }

        @Override
        public List<Node> visit(Relation relation) {
          List<Node> nodes = new ArrayList<>();
          for (RelationMembership membership : relation.getMembers()) {
            nodes.addAll(membership.getObject().accept(this));
          }
          return nodes;
        }
      }));

      if (nodes.get(nodes.size() - 1).equals(firstNode)) {
        Coordinate[] coordinates = new Coordinate[nodes.size() + 1];
        for (int i = 0; i < nodes.size(); i++) {
          Node node = nodes.get(i);
          coordinates[i] = new Coordinate(node.getX(), node.getY());
        }
        coordinates[coordinates.length - 1] = coordinates[0];
        swedenLinearRings.add(new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory));
        firstNode = null;
        nodes.clear();
      }
    }

    Polygon[] polygons = new Polygon[swedenLinearRings.size()];
    for (int i = 0; i < swedenLinearRings.size(); i++) {
      polygons[i] = new Polygon(swedenLinearRings.get(i), null, geometryFactory);
    }
    swedenMultipolygon = new MultiPolygon(polygons, geometryFactory);

  }

  public GeometryFactory getGeometryFactory() {
    return geometryFactory;
  }

  public PojoRoot getSweden() {
    return sweden;
  }

  public List<LinearRing> getSwedenLinearRings() {
    return swedenLinearRings;
  }

  public MultiPolygon getSwedenMultipolygon() {
    return swedenMultipolygon;
  }
}
