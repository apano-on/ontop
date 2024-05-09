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
                                            (5, 'MultiPolygon 1', 'MULTIPOLYGON(((0 0, 5 0, 5 5, 0 5, 0 0)), ((10 10, 15 10, 15 15, 10 15, 10 10)))');