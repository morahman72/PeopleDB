package com.sitg.peopledb.repository;

import com.sitg.peopledb.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PeopleRepositoryTests {

    private Connection connection;
    private PeopleRepository repo;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:C:\\Users\\Owner\\Desktop\\Java\\Terry\\dBeaver\\peopletest");
        connection.setAutoCommit(false);
        repo = new PeopleRepository(connection);
    }
    //like a try-catch block
    //try { open connection } catch(Exception ex) {handle exception} finally {cleanup or recover}
    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null ) {
            connection.close();
        }
    }

    @Test
    public void canSaveOnePerson() throws SQLException {
        Person john = new Person("John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6") ));
        Person savedPerson = repo.save(john);
        assertThat(savedPerson.getId()).isGreaterThan(0);
    }
    @Test
    public void canSaveTwoPerson() {
        Person john = new Person("John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6") ));
        Person bobby = new Person("Bobby", "Smith", ZonedDateTime.of(1982, 9, 13, 13, 13, 0, 0, ZoneId.of("-6") ));
        Person savedPerson1 = repo.save(john);
        Person savedPerson2 = repo.save(bobby);
        assertThat(savedPerson1.getId()).isNotEqualTo(savedPerson2.getId());
    }
    @Test
    public void canFindPersonById() {
        Person savedPerson = repo.save(new Person("test", "jackson", ZonedDateTime.now()));
        Person foundPerson = repo.findById(savedPerson.getId()).get();
        assertThat(foundPerson).isEqualTo(savedPerson);
    }
    @Test
    public void testPersonIdNotFound() {
        Optional<Person> foundPerson = repo.findById(-1L);
        assertThat(foundPerson).isEmpty();
    }
//    @Test
//    public void canFindAll() {
//        repo.save(new Person("John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John2", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John3", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John4", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John5", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John6", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John7", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John8", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//
//        List<Person> people = repo.findAll();
//        assertThat(people.size()).isGreaterThanOrEqualTo(10);
//    }
//    @Test
//    public void canGetCount() {
//        long startCount = repo.count();
//        repo.save(new Person("John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        repo.save(new Person("John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
//        long endCount = repo.count();
//        assertThat(endCount).isEqualTo(startCount + 2);
//    }
}
