import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

public class Data {

	private ArrayList<Event> list;

	public static void main(String[] args) throws IOException {

		System.out.println("STARTING");
		// This puts all the data from the file into a list
		Data data = new Data("covid-data.csv");
		PrintWriter output = new PrintWriter("output.txt");

		// You can use the display method to write your results
		// to a file. This makes it easier to view larger results.
//		Set<String> locations = data.allLocations();
//		display(output, locations);
//		display(output, data.allLocations());
//		System.out.println(data.casesByMonth());
//		display(output, data.locationsByContinent());

//		
		Map<Date, Set<String>> locations = data.mostCasesByDate();
		System.out.println(locations);
		System.out.println(locations.size());
		display(output, locations);
		output.close();
		System.out.println("DONE");
	}

	/*
	 * Returns a set of all location names in the database
	 */
	public Set<String> allLocations() {
		Set<String> locations = new HashSet<String>();
		for (Event event : list) {
			locations.add(event.location);
		}
		return locations;
	}

	/*
	 * Returns a set of all location names in the database
	 */
	public Set<String> allContinents() {
		Set<String> continents = new HashSet<>();
		for (Event event : list) {
			continents.add(event.continent);
		}
		return continents;
	}

	/*
	 * Returns all the locations in a particular continent. The empty set is
	 * returned if the continent does not have any locations.
	 */
	public Set<String> getLocationsInThisContinent(String continent) {
		Set<String> continentLocs = new HashSet<>();
		for (Event event : list) {
			if (event.continent.equals(continent)) {
				continentLocs.add(event.location);
			}
		}
		return continentLocs;
	}

	/*
	 * Returns a map keyed to a continent, where the value is the total number of
	 * deaths in that continent.
	 */
	public Map<String, Integer> deathsByContinent() {
		Map<String, Integer> deaths = new HashMap<>();
		for (Event event : list) {
			if (deaths.containsKey(event.continent)) {
				deaths.put(event.continent, deaths.get(event.continent) + event.deaths);
			} else {
				deaths.put(event.continent, event.deaths);
			}
		}
		return deaths;
	}

	/*
	 * Returns a map keyed to a location, where the value is the total number of
	 * cases for that location. The map should only contain locations that had at
	 * least 1 case.
	 */
	public Map<String, Integer> casesByLocation() {
		Map<String, Integer> cases = new TreeMap<>();
		for (Event event : list) {
			if (event.cases == 0)
				continue;
			else if (cases.containsKey(event.location)) {
				cases.put(event.location, cases.get(event.location) + event.cases);
			} else {
				cases.put(event.location, event.cases);
			}
		}
		return cases;
	}

	/*
	 * For a specified month (0 is January, 1 is February, and so on), returns a map
	 * keyed to a location, where the value is the total number of cases for that
	 * location. The map should only contain locations that had at least one case.
	 * If there are no cases for a given month, returns an empty map. NOTE: Date has
	 * a getMonth() method you can use. You may get a warning that getMonth() is a
	 * deprecated method, but that's ok.
	 */
	@SuppressWarnings("deprecation")
	public Map<String, Integer> casesByLocation(int month) {
		Map<String, Integer> monthlyCases = new TreeMap<>();
		for (Event event : list) {
			if (event.date.getMonth() == month) {
				if (event.cases == 0)
					continue;
				else if (monthlyCases.containsKey(event.location)) {
					monthlyCases.put(event.location, monthlyCases.get(event.location) + event.cases);
				} else {
					monthlyCases.put(event.location, event.cases);
				}
			}
		}
		return monthlyCases;
	}

	/*
	 * Returns a map keyed to the continent, where the value for each continent is a
	 * set of all locations in that continent.
	 */
	public Map<String, Set<String>> locationsByContinent() {
		Map<String, Set<String>> locByContinent = new HashMap<>();
		Set<String> continents = allContinents();
		for (String continent : continents) {
			locByContinent.put(continent, getLocationsInThisContinent(continent));
		}
		return locByContinent;
	}

	/*
	 * Returns a map keyed to a location's 3-letter code, where the value is the
	 * total number of times that code appears in the data.
	 */
	public Map<String, Integer> codeCounts() {
		Map<String, Integer> codeCounts = new TreeMap<>();
		for (Event event : list) {
			if (!codeCounts.containsKey(event.abbreviation))
				codeCounts.put(event.abbreviation, 1);
			else
				codeCounts.put(event.abbreviation, codeCounts.get(event.abbreviation) + 1);

		}
		return codeCounts;
	}

	/*
	 * Returns a map keyed to the date, where the value is the total number of cases
	 * for that date.
	 */
	public Map<Date, Integer> casesByDate() {
		Map<Date, Integer> casesByDate = new TreeMap<>();
		for (Event event : list) {
			if (!casesByDate.containsKey(event.date))
				casesByDate.put(event.date, event.cases);
			else
				casesByDate.put(event.date, casesByDate.get(event.date) + event.cases);
		}
		return casesByDate;
	}

	/*
	 * Returns a list of the locations in the database, sorted by the total number
	 * of cases reported for that location. The location with the fewest cases
	 * should be first, and the location with the most cases should be last.
	 */
	public List<String> locationsSortedByCaseCount() {
		List<String> sortedLocations = new LinkedList<>();
		Map<String, Integer> cases = casesByLocation();
		Map<String, Integer> sortedCases = sortByValues(cases);
		for (String location : sortedCases.keySet()) {
			sortedLocations.add(location);
		}
		return sortedLocations;
	}

	/**
	 * This private helper method sort the map by the values in natural order.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param unsortedMap the unsorted Map
	 * @return the sorted Map.
	 */
	private <K, V> Map<K, V> sortByValues(Map<K, V> unsortedMap) {
		List<Map.Entry<K, V>> entriesList = new LinkedList<>(unsortedMap.entrySet());

		entriesList.sort(new Comparator<Map.Entry<K, V>>() {

			@SuppressWarnings("unchecked")
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				// TODO Auto-generated method stub
				if (o1.getValue().equals(o2.getValue()))
					return ((Comparable<K>) o1.getKey()).compareTo(o2.getKey());
				return ((Comparable<V>) o1.getValue()).compareTo(o2.getValue());
			}
		});
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : entriesList) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	/*
	 * Returns a map keyed to the date, where the value for that date is the
	 * location (or locations) that had the highest number of cases on that date.
	 * It's possible that the set will contain only one location. But if there are
	 * ties, the set will contain more than one location.
	 */
	public Map<Date, Set<String>> mostCasesByDate() {
		Map<Date, Set<String>> result = new TreeMap<>();
		Map<Date, Integer> mostCasesByDate = new TreeMap<>();
		Set<String> locations = new HashSet<>();
		for (Event event : list) {
			if (!mostCasesByDate.containsKey(event.date)) {
				mostCasesByDate.put(event.date, event.cases);
			} else if (event.cases > mostCasesByDate.get(event.date))
				mostCasesByDate.put(event.date, event.cases);
		}

		for (Event event : list) {
			if (!result.containsKey(event.date)) {
				result.put(event.date, locations);
			} else if (event.cases == mostCasesByDate.get(event.date)) {
				locations.add(event.location);
				result.put(event.date, locations);
			}

		}
		return result;

	}

	/*
	 * Returns a map keyed to a month (0=January, 1=February, and so on), where the
	 * value is the number of reported cases for that month. Only include months
	 * that are listed in the data. NOTE: Date has a getMonth() method you can use.
	 * You may get a warning that getMonth() is a deprecated method, but that's ok.
	 */
	@SuppressWarnings("deprecation")
	public Map<Integer, Integer> casesByMonth() {
		Map<Integer, Integer> result = new TreeMap<>();
		for (Event event : list) {
			if (!result.containsKey(event.date.getMonth())) {
				result.put(event.date.getMonth(), event.cases);
			} else {
				result.put(event.date.getMonth(), result.get(event.date.getMonth()) + event.cases);
			}
		}
		return result;
	}

	/********** DON'T MODIFY ANY OF THE CODE BELOW ************/

	// Reads file data into an ArrayList of Event objects.
	public Data(String filename) throws IOException {
		Scanner in = new Scanner(new File(filename));
		list = new ArrayList<Event>();
		in.nextLine();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		while (in.hasNextLine()) {
			String input = in.nextLine();
			String[] line = input.split(",");
			String abbreviation = line[0];
			String continent = line[1];
			String location = line[2];
			Date date = simpleDateFormat.parse(line[3], new ParsePosition(0));
			int cases = Integer.parseInt(line[4]);
			int deaths = Integer.parseInt(line[5]);
			Event d = new Event(abbreviation, continent, location, date, cases, deaths);
			list.add(d);
		}

		in.close();
	}

	/*
	 * Writes a collection (list, set) to a specified file.
	 */
	public static <T> void display(PrintWriter output, Collection<T> items) {
		if (items == null) {
			output.println("null");
			return;
		}
		int LEN = 80;
		String line = "[";
		for (T item : items) {
			line += item.toString() + ",";
			if (line.length() > LEN) {
				output.println(line);
				line = "";
			}
		}
		output.println(line + "]");
	}

	/*
	 * Writes a map to a specified file.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> void display(PrintWriter output, Map<K, V> items) {
		if (items == null) {
			output.println("null");
			return;
		}

		for (K key : items.keySet()) {
			output.print(key + "---------->");
			Object o = items.get(key);
			if (o instanceof Collection) {
				output.println();
				display(output, (Collection<Object>) items.get(key));
			} else {
				output.println(items.get(key));
			}
		}
	}

	/*
	 * Inner class for organizing event information. DON'T CHANGE THIS.
	 */
	private class Event {
		private String abbreviation;
		private String continent;
		private String location;
		private Date date;
		private int cases;
		private int deaths;

		private Event(String abbreviation, String continent, String location, Date date, int cases, int deaths) {
			this.abbreviation = abbreviation;
			this.continent = continent;
			this.location = location;
			this.date = date;
			this.cases = cases;
			this.deaths = deaths;
		}

		@Override
		public String toString() {
			return "[" + abbreviation + "," + continent + "," + location + "," + date + "," + cases + "," + deaths
					+ "]";
		}

	}

}
