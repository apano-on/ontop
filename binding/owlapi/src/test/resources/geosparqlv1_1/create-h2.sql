-- Create the geometries table
CREATE TABLE GEOMS (
                            id INTEGER PRIMARY KEY,
                            name VARCHAR(255),
                            geom GEOMETRY
);

-- Insert some sample geometries
INSERT INTO GEOMS (id, name, geom) VALUES
                                            (1, 'Polygon 1', 'POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))'),
                                            (2, 'Polygon 2', 'POLYGON((5 5, 15 5, 15 15, 5 15, 5 5))'),
                                            (3, 'LineString 1', 'LINESTRING(0 0, 5 5, 10 10)'),
                                            (4, 'Point 1', 'POINT(2 2)'),
                                            (5, 'MultiPolygon 1', 'MULTIPOLYGON(((0 0, 5 0, 5 5, 0 5, 0 0)), ((10 10, 15 10, 15 15, 10 15, 10 10)))'),
                                            (6, 'Eiffel Tower', 'POINT(2.2945 48.8584)'),
                                            (7, 'Tower Bridge', 'POINT(-0.0754 51.5055)');

-- Create the 3D geometries table
CREATE TABLE GEOMSZ (
                       id INTEGER PRIMARY KEY,
                       name VARCHAR(255),
                       geomz GEOMETRY(POLYGONZ)
);


-- Insert some sample geometries
INSERT INTO GEOMSZ (id, name, geomz) VALUES
                                       (1, 'PolygonZ 1', 'POLYGONZ((0 0 0, 10 0 0, 10 10 0, 0 10 1, 0 0 1))'),
                                       (2, 'PolygonZ 2', 'POLYGONZ((5 5 0, 15 5 2, 15 15 2, 5 15 2, 5 5 0))');