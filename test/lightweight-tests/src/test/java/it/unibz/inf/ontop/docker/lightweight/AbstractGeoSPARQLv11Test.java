package it.unibz.inf.ontop.docker.lightweight;

import com.google.common.collect.ImmutableList;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class AbstractGeoSPARQLv11Test extends AbstractDockerRDF4JTest {

    protected static final String OWL_FILE = "/geospatial/geospatial.owl";
    protected static final String OBDA_FILE = "/geospatial/geospatialv1_1.obda";

    @Test
    public void testBoundingCircle() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:boundingCircle(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((5 12.072014486508248,5.231389827700864 12.068228041384849," +
                "5.462531877378542 12.056872760634926,5.693178636336764 12.037960803777528,5.923083122248965 12.011512422209748," +
                "6.15199914763315 11.977555937521032,6.379681583475618 11.9361277111657,6.605886621721245 11.887272105526197," +
                "6.830372036349258 11.831041436408741,7.052897442754929 11.767495917022249,7.273224555159423 11.69670359350052," +
                "7.491117441772188 11.618740272036735,7.706342777432614 11.533689437708288,7.91867009346047 11.441642165078868," +
                "8.127872024447536 11.34269702067354,8.33372455172617 11.236959957431237,8.536007243254124 11.124544201247705," +
                "8.734503489658675 11.005570129730373,8.92900073618739 10.880165143294995,9.119290710317056 10.748463528742104," +
                "9.305169644777134 10.610606315459323,9.486438495748844 10.466741124403583,9.662903156006276 10.3170220100249," +
                "9.834374662771268 10.161609295301004,10.000669400059483 10.000669400059483,10.161609295301002 9.834374662771268," +
                "10.3170220100249 9.662903156006276,10.466741124403583 9.486438495748846,10.610606315459323 9.305169644777134," +
                "10.748463528742104 9.119290710317058,10.880165143294995 8.92900073618739,11.005570129730373 8.734503489658678," +
                "11.124544201247705 8.536007243254126,11.236959957431235 8.333724551726172,11.342697020673539 8.127872024447537," +
                "11.441642165078868 7.918670093460472,11.533689437708288 7.706342777432614,11.618740272036735 7.491117441772189," +
                "11.69670359350052 7.273224555159425,11.767495917022249 7.05289744275493,11.83104143640874 6.83037203634926," +
                "11.887272105526197 6.605886621721247,11.9361277111657 6.379681583475619,11.977555937521032 6.151999147633151," +
                "12.011512422209748 5.923083122248966,12.037960803777526 5.693178636336766,12.056872760634926 5.462531877378544," +
                "12.068228041384849 5.231389827700866,12.072014486508248 5,12.068228041384849 4.768610172299136,12.056872760634926 4.537468122621458," +
                "12.037960803777528 4.306821363663237,12.01151242220975 4.076916877751037,11.977555937521032 3.848000852366852," +
                "11.9361277111657 3.620318416524384,11.887272105526197 3.394113378278756,11.831041436408741 3.169627963650742," +
                "11.767495917022249 2.947102557245072,11.69670359350052 2.726775444840577,11.618740272036735 2.508882558227814," +
                "11.53368943770829 2.293657222567388,11.441642165078868 2.08132990653953,11.34269702067354 1.872127975552465," +
                "11.236959957431239 1.666275448273831,11.124544201247705 1.463992756745878,11.005570129730373 1.265496510341324," +
                "10.880165143294997 1.070999263812612,10.748463528742104 0.880709289682944,10.610606315459325 0.694830355222868," +
                "10.466741124403585 0.513561504251156,10.317022010024903 0.337096843993726,10.161609295301005 0.165625337228733," +
                "10.000669400059483 -0.000669400059482,9.83437466277127 -0.161609295301001,9.662903156006276 -0.3170220100249," +
                "9.486438495748846 -0.466741124403582,9.305169644777138 -0.610606315459322,9.119290710317056 -0.748463528742103," +
                "8.929000736187392 -0.880165143294995,8.734503489658678 -1.005570129730372,8.536007243254126 -1.124544201247704," +
                "8.333724551726172 -1.236959957431236,8.127872024447539 -1.342697020673539,7.918670093460473 -1.441642165078867," +
                "7.706342777432615 -1.533689437708288,7.49111744177219 -1.618740272036733,7.273224555159425 -1.696703593500518," +
                "7.052897442754932 -1.767495917022247,6.83037203634926 -1.83104143640874,6.605886621721249 -1.887272105526196," +
                "6.37968158347562 -1.9361277111657,6.151999147633152 -1.977555937521032,5.923083122248968 -2.011512422209749," +
                "5.693178636336766 -2.037960803777527,5.462531877378546 -2.056872760634926,5.231389827700866 -2.068228041384848," +
                "5.000000000000001 -2.072014486508248,4.768610172299138 -2.068228041384848,4.537468122621458 -2.056872760634926," +
                "4.306821363663238 -2.037960803777528,4.076916877751037 -2.01151242220975,3.848000852366853 -1.977555937521033," +
                "3.620318416524384 -1.9361277111657,3.394113378278757 -1.887272105526196,3.169627963650744 -1.831041436408742," +
                "2.947102557245072 -1.767495917022248,2.726775444840579 -1.696703593500519,2.508882558227814 -1.618740272036735," +
                "2.29365722256739 -1.533689437708289,2.081329906539532 -1.44164216507887,1.872127975552465 -1.34269702067354," +
                "1.666275448273831 -1.236959957431239,1.463992756745878 -1.124544201247707,1.265496510341327 -1.005570129730375," +
                "1.070999263812612 -0.880165143294998,0.880709289682947 -0.748463528742106,0.694830355222868 -0.610606315459324," +
                "0.513561504251157 -0.466741124403585,0.337096843993726 -0.317022010024902,0.165625337228734 -0.161609295301004," +
                "-0.000669400059479 -0.000669400059484,-0.161609295301001 0.165625337228731,-0.3170220100249 0.337096843993723," +
                "-0.466741124403582 0.513561504251152,-0.610606315459322 0.694830355222863,-0.748463528742103 0.880709289682942," +
                "-0.880165143294994 1.070999263812605,-1.005570129730371 1.26549651034132,-1.124544201247704 1.463992756745873," +
                "-1.236959957431236 1.666275448273827,-1.342697020673539 1.872127975552464,-1.441642165078865 2.081329906539524," +
                "-1.533689437708286 2.293657222567382,-1.618740272036733 2.508882558227809,-1.696703593500518 2.726775444840574," +
                "-1.767495917022248 2.94710255724507,-1.831041436408739 3.169627963650736,-1.887272105526196 3.394113378278751," +
                "-1.936127711165699 3.620318416524379,-1.977555937521032 3.848000852366848,-2.011512422209748 4.076916877751029," +
                "-2.037960803777527 4.30682136366323,-2.056872760634926 4.537468122621453,-2.068228041384848 4.768610172299133," +
                "-2.072014486508248 4.999999999999999,-2.068228041384848 5.231389827700858,-2.056872760634927 5.462531877378538," +
                "-2.037960803777528 5.693178636336761,-2.01151242220975 5.923083122248963,-1.977555937521032 6.151999147633149," +
                "-1.936127711165701 6.379681583475612,-1.887272105526198 6.60588662172124,-1.831041436408742 6.830372036349255," +
                "-1.767495917022248 7.052897442754928,-1.696703593500518 7.273224555159423,-1.618740272036737 7.491117441772182," +
                "-1.533689437708289 7.70634277743261,-1.44164216507887 7.918670093460468,-1.34269702067354 8.127872024447534," +
                "-1.236959957431241 8.333724551726165,-1.124544201247708 8.536007243254119,-1.005570129730375 8.734503489658673," +
                "-0.880165143294998 8.929000736187387,-0.748463528742104 9.119290710317056,-0.610606315459327 9.30516964477713," +
                "-0.466741124403588 9.48643849574884,-0.317022010024902 9.662903156006273,-0.161609295301004 9.834374662771266," +
                "-0.000669400059484 10.000669400059481,0.165625337228728 10.161609295300998,0.33709684399372 10.317022010024896," +
                "0.513561504251152 10.466741124403582,0.694830355222863 10.610606315459322,0.880709289682942 10.748463528742104," +
                "1.070999263812605 10.880165143294992,1.26549651034132 11.005570129730371,1.463992756745873 11.124544201247703," +
                "1.666275448273827 11.236959957431235,1.872127975552458 11.342697020673537,2.081329906539524 11.441642165078864," +
                "2.293657222567382 11.533689437708286,2.508882558227809 11.618740272036733,2.726775444840574 11.69670359350052," +
                "2.947102557245064 11.767495917022245,3.169627963650736 11.83104143640874,3.39411337827875 11.887272105526197," +
                "3.620318416524379 11.9361277111657,3.848000852366848 11.977555937521032,4.076916877751028 12.011512422209748," +
                "4.30682136366323 12.037960803777526,4.537468122621453 12.056872760634926,4.768610172299133 12.068228041384849," +
                "4.999999999999998 12.072014486508248))\"^^geo:wktLiteral"));
    }

    @Test
    public void testMetricBuffer() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricBuffer(?xWkt, 1) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((-0.00000898054534 -0.000000153417746," +
                "-0.000009183768035 9.999999736438603,-0.000009035580125 10.000001500063481," +
                "-0.000008537021531 10.000003205133346,-0.000007707424744 10.00000478553108," +
                "-0.000006578958848 10.000006179973946,-0.000005195382108 10.000007334389938," +
                "-0.000003610345176 10.000008204014527,-0.000001885310691 10.000008755126462," +
                "-0.000000087169914 10.000008966355395,10.000000223234656 10.00000902928204," +
                "10.000001964323957 10.000008826065915,10.000003632775899 10.000008296107476," +
                "10.000005166893967 10.000007459003683,10.000006509949088 10.000006345709211," +
                "10.000007612277367 10.000004997391787,10.000008433116585 10.00000346390991," +
                "10.000008942113512 10.000001801969116,10.00000912044631 10.000000073025156," +
                "10.000008915672328 0.000000050008457,10.000008729485392 -0.000001753948305," +
                "10.000008198146011 -0.000003488556233,10.00000734266265 -0.000005085231214," +
                "10.00000619686001 -0.000006480842798,10.000004806041632 -0.000007620210328," +
                "10.000003225198654 -0.000008458284704,10.000001516835535 -0.000008961929572," +
                "9.999999748498734 -0.000009111231466,-0.000000147805821 -0.00000904884515," +
                "-0.000001897375156 -0.000008905448475,-0.000003573997228 -0.000008419669295," +
                "-0.000005113211946 -0.000007610184076,-0.000006455842025 -0.00000650811461," +
                "-0.000007550268181 -0.000005155831522,-0.000008354413632 -0.000003605325224," +
                "-0.000008837361895 -0.000001916207125,-0.00000898054534 -0.000000153417746))\"^^geo:wktLiteral"));
    }

    @Test
    public void testCentroid() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:centroid(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(5 5)\"^^geo:wktLiteral"));
    }

    @Test
    public void testConcaveHull() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:concaveHull(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((0 0,10 0,10 10,0 10,0 0))\"^^geo:wktLiteral"));
    }

    @Test
    public void testCoordinateDimension() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:coordinateDimension(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"2\"^^xsd:integer"));
    }

    @Test
    public void testDimension() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:dimension(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"2\"^^xsd:integer"));
    }

    @Test
    public void testMetricDistance() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":6 a :Geom; geo:asWKT ?xWkt.\n" +
                ":7 a :Geom; geo:asWKT ?yWkt.\n" +
                "BIND (geof:metricDistance(?xWkt, ?yWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"339241.2973924\"^^xsd:double"));
    }

    @Test
    public void testGeometryType() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:geometryType(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("<http://www.opengis.net/ont/sf#Polygon>"));
    }

    @Test
    public void testis3DFalse() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:is3D(?xWkt) AS ?v)\n" +
                "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"false\"^^xsd:boolean"));
    }

    @Test
    public void testis3DTrue() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "<http://ex.org/geomz/1> a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:is3D(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"true\"^^xsd:boolean"));
    }

    @Test
    public void testisMeasured() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND(geof:isMeasured(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"false\"^^xsd:boolean"));
    }

    @Test
    public void testisEmpty() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:isEmpty(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"false\"^^xsd:boolean"));
    }

    @Test
    public void testisSimple() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:isSimple(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"true\"^^xsd:boolean"));
    }

    @Test
    public void testSpatialDimension() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:spatialDimension(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"2\"^^xsd:integer"));
    }

    @Test
    public void testTransformEPSG3044() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/EPSG/0/3044>) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(-280405.62793313444 222731.06606055028)\"^^geo:wktLiteral"));
    }

    @Test
    public void testTransformCRS84() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/OGC/1.3/CRS/84>) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(2 2)\"^^geo:wktLiteral"));
    }

    @Test
    public void testTransformIncorrectSRID() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/EPSG/0/32439523>) AS ?v)\n" +
                "}\n";
        var error = assertThrows(QueryEvaluationException.class, () -> this.runQuery(query));
        Assertions.assertEquals("it.unibz.inf.ontop.exception.OntopReformulationException: java.lang.IllegalArgumentException: " +
                "Unknown SRID: http://www.opengis.net/def/crs/EPSG/0/32439523", error.getMessage());
    }

    @Test
    public void testGeometryN() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":5 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:geometryN(?xWkt, 2) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((10 10,15 10,15 15,10 15,10 10))\"^^geo:wktLiteral"));
    }

    @Test
    public void testArea() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:area(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"100\"^^xsd:double"));
    }

    @Test
    public void testMetricArea() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricArea(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"100\"^^xsd:double"));
    }

    @Test
    public void testPerimeter() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:perimeter(?xWkt, uom:metre) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"40\"^^xsd:double"));
    }

    @Test
    public void testMetricPerimeter() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricPerimeter(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"40\"^^xsd:double"));
    }

    @Test
    public void testLength() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":3 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:length(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"14.142135623730951\"^^xsd:double"));
    }

    @Test
    public void testMetricLength() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":3 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricLength(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"14.142135623730951\"^^xsd:double"));
    }

    @Test
    public void testMaxX() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxX(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"10\"^^xsd:double"));
    }

    @Test
    public void testMaxY() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxY(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"10\"^^xsd:double"));
    }

    @Test
    public void testMaxZ() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "<http://ex.org/geomz/2> a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxZ(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"2\"^^xsd:double"));
    }

    @Test
    public void testMinX() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minX(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"0\"^^xsd:double"));
    }

    @Test
    public void testMinY() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minY(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"0\"^^xsd:double"));
    }

    @Test
    public void testMinZ() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "<http://ex.org/geomz/2> a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minZ(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"0\"^^xsd:double"));
    }

    @Test
    public void testNumGeometries() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:numGeometries(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"1\"^^xsd:integer"));
    }

    @Test
    public void testAggBoundingBox() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggBoundingBox(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((0 0,0 15,15 15,15 0,0 0))\"^^geo:wktLiteral"));
    }

    @Test
    public void testAggBoundingCircle() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggBoundingCircle(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((0 0,0 15,15 15,15 0,0 0))\"^^geo:wktLiteral"));
    }

    @Test
    public void testAggCentroid() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggCentroid(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(7.5 7.5)\"^^geo:wktLiteral"));
    }

    @Test
    public void testAggConcaveHull() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT (geof:aggConcaveHull(?xWkt) AS ?v) WHERE {\n" +
                "{\n" +
                "    :3 a :Geom; geo:asWKT ?xWkt.\n" +
                "}\n" +
                "UNION\n" +
                "{\n" +
                "    :4 a :Geom; geo:asWKT ?xWkt.\n" +
                "}\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"LINESTRING(0 0,10 10)\"^^geo:wktLiteral"));
    }

    @Test
    public void testAggConvexHull() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggConvexHull(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON Z ((0 0 0,0 10 1,5 15 2,15 15 2,15 5 2,10 0 0," +
                "0 0 0))\"^^geo:wktLiteral"));
    }

    @Test
    public void testAggUnion() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggUnion(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"GEOMETRYCOLLECTION(POINT(-0.0754 51.5055)," +
                "POINT(2.2945 48.8584)," +
                "POLYGON((0 10,5 10,5 15,10 15,15 15,15 10,15 5,10 5,10 0,5 0,0 0,0 5,0 10)))\"^^geo:wktLiteral"));
    }

    @Disabled("Not supported yet")
    @Test
    public void testAggUnionMultipleArg() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "?gz a :GeomZ; geo:asWKT ?yWkt.\n" +
                "BIND (geof:aggUnion(?xWkt, ?yWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"GEOMETRYCOLLECTION (POINT (-0.0754 51.5055), " +
                "POINT (2.2945 48.8584), " +
                "POLYGON ((0 5,0 10,5 10,5 15,10 15,15 15,15 10,15 5,10 5,10 0,5 0,0 0,0 5)))\"^^geo:wktLiteral"));
    }

    @Test
    public void testAggUnionWithGroupBy() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT (geof:aggUnion(?xWkt) AS ?v) ?label WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "?g rdfs:label ?label .\n" +
                "}\n" +
                "GROUP BY ?label\n" +
                "ORDER BY ?label\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(2.2945 48.8584)\"^^geo:wktLiteral",
                "\"LINESTRING(0 0,5 5,10 10)\"^^geo:wktLiteral",
                "\"MULTIPOLYGON(((0 5,5 5,5 0,0 0,0 5)),((10 15,15 15,15 10,10 10,10 15)))\"^^geo:wktLiteral",
                "\"POINT(2 2)\"^^geo:wktLiteral",
                "\"POLYGON((0 0,10 0,10 10,0 10,0 0))\"^^geo:wktLiteral",
                "\"POLYGON((5 5,15 5,15 15,5 15,5 5))\"^^geo:wktLiteral",
                "\"POINT(-0.0754 51.5055)\"^^geo:wktLiteral"));
    }

    @Disabled("Not supported yet")
    @Test
    public void testAggUnionWithValues() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "VALUES (?geom) {\n" +
                "(\"POINT(1 1)\"^^geo:wktLiteral)\n" +
                "(\"POINT(2 2)\"^^geo:wktLiteral)\n" +
                "(\"POINT(3 3)\"^^geo:wktLiteral)\n" +
                "}\n" +
                "BIND(geof:aggUnion(?geom) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"MULTIPOINT((1 1),(2 2),(3 3))\"^^geo:wktLiteral"));
    }
}
