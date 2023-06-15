CREATE DATABASE geospatial;

\connect geospatial

CREATE EXTENSION postgis;

CREATE TABLE "GEOMS" ("id" INT PRIMARY KEY, "geom" GEOMETRY, "name" TEXT);
INSERT INTO "GEOMS" VALUES (1, 'POLYGON((2 2, 7 2, 7 5, 2 5, 2 2))', 'small rectangle'),
                           (2, 'POLYGON((1 1, 8 1, 8 7, 1 7, 1 1))', 'large rectangle'),
                           (3, 'POINT(2.2945 48.8584)', 'Eiffel Tower'),
                           (4, 'POINT(-0.0754 51.5055)', 'Tower Bridge'),
                           (5, 'LINESTRING(1 2, 2 2, 3 2)', 'short horizontal line'),
                           (6, 'LINESTRING(1 2, 10 2)', 'long horizontal line');

CREATE TABLE "GEOGS" ("id" INT PRIMARY KEY, "geog" GEOGRAPHY, "name" TEXT);
INSERT INTO "GEOGS" VALUES (1, 'POLYGON((2 2, 7 2, 7 5, 2 5, 2 2))', 'small rectangle'),
    (2, 'POLYGON((1 1, 8 1, 8 7, 1 7, 1 1))', 'large rectangle'),
    (3, 'POINT(2.2945 48.8584)', 'Eiffel Tower'),
    (4, 'POINT(-0.0754 51.5055)', 'Tower Bridge'),
    (5, 'LINESTRING(1 2, 2 2, 3 2)', 'short horizontal line'),
    (6, 'LINESTRING(1 2, 10 2)', 'long horizontal line');

CREATE TABLE "GEOMS_2" ("id" INT PRIMARY KEY, "geom" GEOMETRY, "name" TEXT);
INSERT INTO "GEOMS_2" VALUES (11, 'POLYGON((1 1, 7 1, 7 2, 1 2, 1 1))', 'small rectangle');

-- CREATE TABLE "FEATURES" (id INT PRIMARY KEY, gid TEXT, the_geom geometry, name TEXT);
-- INSERT INTO "FEATURES" VALUES (1, 'FRANCE1', 'POINT(2.2945 48.8584)', 'Eiffel Tower');
-- INSERT INTO "FEATURES" VALUES (2, 'UK1', 'POINT(-0.0754 51.5055)', 'Tower Bridge');

-- CREATE TABLE "POINTS" (id INT PRIMARY KEY , longitude float, latitude float, name TEXT);
-- INSERT INTO "POINTS" VALUES (3, 2.2945, 48.8584, 'Eiffel Tower');
-- INSERT INTO "POINTS" VALUES (4, -0.0754, 51.5055, 'Tower Bridge');
-- INSERT INTO "POINTS" VALUES (21, 668682.853, 5122639.964, 'a point in BZ with SRID <http://www.opengis.net/def/crs/EPSG/0/3044>');
-- INSERT INTO "POINTS" VALUES (26, 668683.853, 5122640.964, 'a point in BZ with SRID <http://www.opengis.net/def/crs/EPSG/0/3044>');
--
--
-- CREATE TABLE "FEATURES" (id INT PRIMARY KEY, gid TEXT, the_geom geometry, name TEXT);
-- INSERT INTO "FEATURES" VALUES (1, 'FRANCE1', 'POINT(2.2945 48.8584)', 'Eiffel Tower');
-- INSERT INTO "FEATURES" VALUES (2, 'UK1', 'POINT(-0.0754 51.5055)', 'Tower Bridge');
--
--
-- CREATE TABLE "RIVERS" (id INT, lat float, lon float);
-- INSERT INTO "RIVERS" VALUES (1, 2.2945, 48.8584);
-- INSERT INTO "RIVERS" VALUES (2, -0.0754, 51.5055);
--
--
-- CREATE TABLE "LAKES" (id INT, lat float, lon float);
-- INSERT INTO "LAKES" VALUES (1, 2.3945, 49.8584);
-- INSERT INTO "LAKES" VALUES (2, -0.1754, 52.5055);
--
-- CREATE TABLE "OSM1" (id INT, geom GEOMETRY, name TEXT);
-- INSERT INTO "OSM1" VALUES (1, 'POINT(2.2945 48.8584)', 'Eiffel Tower');
-- INSERT INTO "OSM1" VALUES (2, 'POINT(-0.0754 51.5055)', 'Tower Bridge');
-- CREATE TABLE "OSM2" (id INT, geom GEOMETRY, name TEXT);
-- INSERT INTO "OSM2" VALUES (1, 'POINT(2.2945 48.8584)', 'Eiffel Tower');
-- INSERT INTO "OSM2" VALUES (2, 'POINT(-0.0754 51.5055)', 'Tower Bridge');