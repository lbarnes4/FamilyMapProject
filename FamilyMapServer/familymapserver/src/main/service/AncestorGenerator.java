package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import dao.PersonDao;
import model.Event;
import model.Location;
import model.Person;
import model.User;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public abstract class AncestorGenerator {
    Database db;
    Connection conn;
    Gson gson = new Gson();
    User user;
    EventDao eventDao;
    PersonDao personDao;
    ArrayList<String> maleNames;
    ArrayList<String> femaleNames;
    ArrayList<String> lastNames;
    ArrayList<Location> locations = new ArrayList<>();
    int ancestorCount = 0;

    public AncestorGenerator() throws IOException {
        //initialize json data
        maleNames = gson.fromJson(getData("json/mnames.json"),ArrayList.class);
        femaleNames = gson.fromJson(getData("json/fnames.json"),ArrayList.class);
        lastNames = gson.fromJson(getData("json/snames.json"),ArrayList.class);
        JsonArray jsonLocations = getData("json/locations.json");
        for (int i = 0; i < jsonLocations.size(); i++) {
            locations.add(gson.fromJson(jsonLocations.get(i), Location.class));
        }
    }

    public void generateAncestors(Person person, int childBirthYear, int generations) throws IOException, DataAccessException {
        if (generations == 0) {
            return;
        }
        Random random = new Random();
        Location newLocation;
        Person father = new Person();
        Person mother = new Person();

        //make this person's parents and add them to ancestors

        father = new Person(person.getFatherID(), person.getAssociatedUsername(),
                maleNames.get(random.nextInt(maleNames.size())),
                lastNames.get(random.nextInt(lastNames.size())),
                "m", generations > 1 ? UUID.randomUUID().toString() : null,
                generations > 1 ? UUID.randomUUID().toString() : null,
                person.getMotherID());
        personDao.insert(father);

        ancestorCount++;

        mother = new Person(person.getMotherID(),person.getAssociatedUsername(),
                femaleNames.get(random.nextInt(maleNames.size())),
                lastNames.get(random.nextInt(lastNames.size())),
                "f", generations > 1 ? UUID.randomUUID().toString() : null,
                generations > 1 ? UUID.randomUUID().toString() : null,
                person.getFatherID());
        personDao.insert(mother);
        ancestorCount++;

        //make the parents' births, marriages, and deaths
        newLocation = locations.get(random.nextInt(locations.size()));
        int fatherBirthYear = childBirthYear - 18 - random.nextInt(30);
        eventDao.insert(new Event(
                UUID.randomUUID().toString(),user.getUserName(),father.getPersonID(),
                newLocation.getLatitude(),newLocation.getLongitude(),newLocation.getCountry(),
                newLocation.getCity(),"birth", fatherBirthYear
        ));

        newLocation = locations.get(random.nextInt(locations.size()));
        int motherBirthYear = childBirthYear - 18 - random.nextInt(30);
        eventDao.insert(new Event(
                UUID.randomUUID().toString(),user.getUserName(),mother.getPersonID(),
                newLocation.getLatitude(),newLocation.getLongitude(),newLocation.getCountry(),
                newLocation.getCity(),"birth",motherBirthYear
        ));

        newLocation = locations.get(random.nextInt(locations.size()));
        int marriageYear = Math.max(fatherBirthYear,motherBirthYear) + 18 + random.nextInt(childBirthYear - 17 - Math.max(fatherBirthYear,motherBirthYear));

        eventDao.insert(new Event(
                UUID.randomUUID().toString(),user.getUserName(),father.getPersonID(),
                newLocation.getLatitude(),newLocation.getLongitude(),newLocation.getCountry(),
                newLocation.getCity(),"marriage",marriageYear
        ));

        eventDao.insert(new Event(
                UUID.randomUUID().toString(),user.getUserName(),mother.getPersonID(),
                newLocation.getLatitude(),newLocation.getLongitude(),newLocation.getCountry(),
                newLocation.getCity(),"marriage",marriageYear
        ));

        newLocation = locations.get(random.nextInt(locations.size()));

        int fatherDeathYear= childBirthYear + random.nextInt(fatherBirthYear + 120 - childBirthYear);
        fatherDeathYear = Math.min(fatherDeathYear, 2020);
        eventDao.insert(new Event(
                UUID.randomUUID().toString(),user.getUserName(),father.getPersonID(),
                newLocation.getLatitude(),newLocation.getLongitude(),newLocation.getCountry(),
                newLocation.getCity(),"death",fatherDeathYear
        ));

        newLocation = locations.get(random.nextInt(locations.size()));

        int motherDeathYear = childBirthYear + random.nextInt(motherBirthYear + 120 - childBirthYear);
        motherDeathYear = Math.min(motherDeathYear, 2020);
        eventDao.insert(new Event(
                UUID.randomUUID().toString(),user.getUserName(),mother.getPersonID(),
                newLocation.getLatitude(),newLocation.getLongitude(),newLocation.getCountry(),
                newLocation.getCity(),"death",motherDeathYear
        ));

        generateAncestors(father,fatherBirthYear,generations - 1);
        generateAncestors(mother,motherBirthYear,generations - 1);
    }

    public Person generateUserPersonAndBirthEvent(int childBirthYear) throws DataAccessException {
        String fatherID = UUID.randomUUID().toString();
        String motherID = UUID.randomUUID().toString();
        Person person = new Person(user.getPersonID(),user.getUserName(), user.getFirstName(),
                user.getLastName(),user.getGender(),fatherID,motherID,null);
        personDao.insert(person);

        Random random = new Random();
        Location newLocation = locations.get(random.nextInt(locations.size()));

        eventDao.insert(new Event(
                UUID.randomUUID().toString(),user.getUserName(),user.getPersonID(),
                newLocation.getLatitude(),newLocation.getLongitude(),newLocation.getCountry(),
                newLocation.getCity(),"birth",childBirthYear
        ));
        return person;
    }

    private JsonArray getData(String filePath) throws  IOException {
        File maleNamesFile = new File(filePath);
        FileReader maleNamesReader = new FileReader(maleNamesFile);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(maleNamesReader,JsonObject.class);
        return jsonObject.getAsJsonArray("data");
    }
}
