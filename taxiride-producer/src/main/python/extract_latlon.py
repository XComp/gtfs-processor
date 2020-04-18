import json
import sys


def extract_information(data):
    for feature in data['features']:
        zone_id = feature['properties']['objectid']
        zone_name = feature['properties']['zone']
        borough = feature['properties']['borough']

        latitudes = []
        longitudes = []
        for coord in feature['geometry']['coordinates']:
            for l0 in coord:
                for lon, lat in l0:
                    latitudes.append(float(lat))
                    longitudes.append(float(lon))

        latitude_ctr = (max(latitudes) + min(latitudes)) / 2.0
        longitude_ctr = (max(longitudes) + min(longitudes)) / 2.0

        yield zone_id, zone_name, borough, str(latitude_ctr), str(longitude_ctr)


if __name__ == '__main__':
    with open(sys.argv[1]) as f:
        for t in extract_information(json.load(f)):
            print(','.join(t))
