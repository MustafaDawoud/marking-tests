package ca.uwo.eng.se2205b.lab7.marking;

import ca.uwo.eng.se2205.lab7.travel.Airport;
import ca.uwo.eng.se2205.lab7.travel.Flight;
import ca.uwo.eng.se2205.lab7.travel.FlightShopper;
import ca.uwo.eng.se2205.lab7.travel.Itinerary;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the Flight Shopper, requires the resource file
 */
class FlightShopperTest {

    private Map<String, Airport> airports;
    private Table<Airport, Airport, Flight> flights;
    private FlightShopper underTest;

    @BeforeEach
    void init() {
        airports = new HashMap<>();
        flights = HashBasedTable.create();

        loadProvided(airports, flights);
        underTest = new FlightShopper(airports.values(), flights.values());
    }

    class Base {

        List<Flight> flightPlan;
        Itinerary expected, actual;

        protected void initPlan(String... it) {

            checkArgument(it.length >= 2, "invalid itinerary");

            ImmutableList.Builder<Flight> flightPlanBld = ImmutableList.builder();
            for (int i = 1; i < it.length; ++i) {
                final Airport dept = airports.get(it[i-1]);
                final Airport arr = airports.get(it[i]);

                flightPlanBld.add(flights.get(dept, arr));
            }

            flightPlan = flightPlanBld.build();
            expected = new Itinerary(flightPlan);
            actual = underTest.price(airports.get(it[0]), airports.get(it[it.length - 1]));
        }
    }

    @Nested
    class SingleFlight extends Base {

        @BeforeEach
        void init() {
            initPlan("ATL", "ORD");
        }

        @Test
        void cost() {
            assertEquals(94.0, actual.getCost(), 0.001, "Cost is incorrect");
        }

        @Test
        void departure() {
            assertEquals(airports.get("ATL"), actual.getDeparture());
        }

        @Test
        void arrival() {
            assertEquals(airports.get("ORD"), actual.getArrival());
        }

        @Test
        void flightPlan() {
            assertEquals(flightPlan, actual.getFlights(),
                    () -> "Invalid flights in itinerary: " + actual.getFlights());
        }
    }

    @Nested
    class TwoFlights extends Base {

        @BeforeEach
        void init() {
            initPlan("ATL", "ORD", "PEK");
        }

        @Test
        void cost() {
            assertEquals(842.0, actual.getCost(), 0.001, "Cost is incorrect");
        }

        @Test
        void departure() {
            assertEquals(airports.get("ATL"), actual.getDeparture());
        }

        @Test
        void arrival() {
            assertEquals(airports.get("PEK"), actual.getArrival());
        }

        @Test
        void flightPlan() {
            assertEquals(flightPlan, actual.getFlights(),
                    () -> "Invalid flights in itinerary: " + actual.getFlights());
        }
    }

    @Nested
    class MultiFlights extends Base {

        @BeforeEach
        void init() {
            initPlan("ATL", "ORD", "PEK", "PVG");
        }

        @Test
        void cost() {
            assertEquals(1072.0, actual.getCost(), 0.001, "Cost is incorrect");
        }

        @Test
        void departure() {
            assertEquals(airports.get("ATL"), actual.getDeparture());
        }

        @Test
        void arrival() {
            assertEquals(airports.get("PVG"), actual.getArrival());
        }

        @Test
        void flightPlan() {
            assertEquals(flightPlan, actual.getFlights(),
                    () -> "Invalid flights in itinerary: " + actual.getFlights());
        }
    }

    @Nested
    class NoFlights extends Base {

        @BeforeEach
        void init() {
            flightPlan = null;
            expected = null;
            actual = underTest.price(airports.get("PEK"), airports.get("ABC"));
        }

        @Test
        void shouldBeNull() {
            assertNull(actual, "Invalid flight path should return null");
        }
    }

    @Nested
    class OneWay extends Base {

        @BeforeEach
        void init() {

            initPlan("ABC", "ORD", "PEK");
        }

        @Test
        void cost() {
            assertEquals(1248.0, actual.getCost(), 0.001, "Cost is incorrect");
        }

        @Test
        void departure() {
            assertEquals(airports.get("ABC"), actual.getDeparture());
        }

        @Test
        void arrival() {
            assertEquals(airports.get("PEK"), actual.getArrival());
        }

        @Test
        void flightPlan() {
            assertEquals(flightPlan, actual.getFlights(),
                    () -> "Invalid flights in itinerary: " + actual.getFlights());
        }
    }

    private void loadProvided(Map<String, Airport> airportMap, Table<Airport, Airport, Flight> flightsTable) {
        loadAirports(airportMap);
        loadFlights(airportMap, flightsTable);
    }

    private Map<String, Airport> loadAirports(Map<String, Airport> airports) {
        checkNotNull(airports, "airports == null");

        airports.put("ATL", new Airport("ATL", -84.4281006, 33.6366997));
        airports.put("HND", new Airport("HND", 139.7799988, 35.5522995));
        airports.put("LHR", new Airport("LHR", -0.461941, 51.4706001));
        airports.put("ORD", new Airport("ORD", -87.9048004, 41.9785996));
        airports.put("PEK", new Airport("PEK", 116.5849991, 40.080101));
        airports.put("PVG", new Airport("PVG", 121.8050003, 31.1434002));
        airports.put("YYZ", new Airport("YYZ", -79.6306, 43.6772003));
        airports.put("ABC", new Airport("ABC", -79.6306, 43.6772003));

//        // Comment out if the student's removed the lat/long parameters -- they may have, they're not used.
//        airports.put("ATL", new Airport("ATL"));
//        airports.put("HND", new Airport("HND"));
//        airports.put("LHR", new Airport("LHR"));
//        airports.put("ORD", new Airport("ORD"));
//        airports.put("PEK", new Airport("PEK"));
//        airports.put("PVG", new Airport("PVG"));
//        airports.put("YYZ", new Airport("YYZ"));
//        airports.put("ABC", new Airport("ABC"));

        return airports;
    }

    private void loadFlights(Map<String, Airport> airports, Table<Airport, Airport, Flight> flightsTable) {
        checkNotNull(airports, "airports == null");
        checkNotNull(flightsTable, "flights == null");

        try (InputStream resStream = FlightShopperTest.class.getResourceAsStream("/flights.csv");
             InputStreamReader reader = new InputStreamReader(resStream);
             BufferedReader in = new BufferedReader(reader)) {

            // ignore the titles

            String line;
            while ((line = in.readLine()) != null) {
                final String[] cols = line.split(",");

                try {
                    Airport dept = airports.get(cols[0].trim());
                    Airport arv = airports.get(cols[1].trim());
                    double cost = Double.parseDouble(cols[2].trim());

                    // Either adjust the ctor to not use the code or just add a dummy one
                    Flight f = new Flight(dept, arv, cost);

                    flightsTable.put(dept, arv, f);
                } catch (NumberFormatException nfe) {
                    // continue;
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}