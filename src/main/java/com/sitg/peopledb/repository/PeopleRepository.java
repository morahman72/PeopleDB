package com.sitg.peopledb.repository;

import com.sitg.peopledb.model.Person;

import java.math.BigDecimal;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PeopleRepository extends CRUDRepository<Person> {
    public static final String SAVE_PERSON_SQL = "INSERT INTO PEOPLE (FIRST_NAME, LAST_NAME, DOB) VALUES(?, ?, ?)";
    public static final String FIND_BY_ID_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB, SALARY FROM PEOPLE WHERE ID=?";
    public static final String FIND_ALL_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB, SALARY FROM PEOPLE";
    public static final String SELECT_COUNT_SQL = "SELECT COUNT(*) FROM PEOPLE";
    public static final String DELETE_SQL = "DELETE FROM PEOPLE WHERE ID=?";
    public static final String DELETE_IN_SQL = "DELETE FROM PEOPLE WHERE ID IN (:ids)";
    public static final String UPDATE_SQL = "UPDATE PEOPLE SET FIRST_NAME=?, LAST_NAME=?, DOB=?, SALARY=?, WHERE ID=?";
//    private Connection connection;
    public PeopleRepository(Connection connection) {
        super(connection);
//        this.connection = connection;
    }

    @Override
    String getSaveSql() {
        return SAVE_PERSON_SQL;
    }
    @Override
    void mapForSave(Person entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getFirstName());
        ps.setString(2, entity.getLastName());
        ps.setTimestamp(3, convertDobToTimestamp(entity.getDob()));
    }

    @Override
    Person extractEntityFromResultSet(ResultSet rs) throws SQLException {
        long personId = rs.getLong("ID");
        String firstName = rs.getString("FIRST_NAME");
        String lastName = rs.getString("LAST_NAME");
        ZonedDateTime dob = ZonedDateTime.of(rs.getTimestamp("DOB").toLocalDateTime(), ZoneId.of("+0"));
        BigDecimal salary = rs.getBigDecimal("SALARY");
        return new Person(personId, firstName, lastName, dob, salary);
    }

    @Override
    protected String getFindByIdSql() {
        return FIND_BY_ID_SQL;
    }

    @Override
    protected String getFindAllSql() {
        return FIND_ALL_SQL;
    }

    @Override
    protected String getCountSql() {
        return SELECT_COUNT_SQL;
    }

    @Override
    protected String getDeleteSql() {
        return DELETE_SQL;
    }

    @Override
    protected String getDeleteInSql() {
        return DELETE_IN_SQL;
    }

    @Override
    protected String getUpdateSql() {
        return UPDATE_SQL;
    }

    @Override
    void mapForUpdate(Person entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getFirstName());
        ps.setString(2, entity.getLastName());
        ps.setTimestamp(3, convertDobToTimestamp(entity.getDob()));
        ps.setBigDecimal(4, entity.getSalary());

    }
    //    public Person save(Person entity) {
//        try {
//            PreparedStatement ps = connection.prepareStatement(SAVE_PERSON_SQL, Statement.RETURN_GENERATED_KEYS);
//            ps.setString(1, entity.getFirstName());
//            ps.setString(2, entity.getLastName());
//            ps.setTimestamp(3, convertDobToTimestamp(entity.getDob()));
//            int recordsAffected = ps.executeUpdate();
//            ResultSet rs = ps.getGeneratedKeys();
//            while (rs.next()) {
//                long id = rs.getLong(1);
//                entity.setId(id);
//                System.out.println(entity);
//            }
//            System.out.printf("Records affected: %d%n", recordsAffected);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return entity;
//    }
//    public Optional<Person> findById(Long id) {
//        Person person = null;
//
//        try {
//            PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL);
//            ps.setLong(1, id);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                person = extractEntityFromResultSet(rs);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return Optional.ofNullable(person);
//    }

//    public List<Person> findAll() {
//        List<Person> people = new ArrayList<>();
//        try {
//            PreparedStatement ps = connection.prepareStatement(FIND_ALL_SQL);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                people.add(extractEntityFromResultSet(rs));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return people;
//    }

//    private Person extractPersonFormResultSet(ResultSet rs) throws SQLException {
//        long personId = rs.getLong("ID");
//        String firstName = rs.getString("FIRST_NAME");
//        String lastName = rs.getString("LAST_NAME");
//        ZonedDateTime dob = ZonedDateTime.of(rs.getTimestamp("DOB").toLocalDateTime(), ZoneId.of("+0"));
//        BigDecimal salary = rs.getBigDecimal("SALARY");
//        return new Person(personId, firstName, lastName, dob, salary);
//    }

//    public long count() {
//        long count = 0;
//        try {
//            PreparedStatement ps = connection.prepareStatement(SELECT_COUNT_SQL);
//            ResultSet rs = ps.executeQuery();
//            if(rs.next()) {
//                count = rs.getLong(1);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return count;
//    }

//    public void delete(Person person) {
//        try {
//            PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
//            ps.setLong(1, person.getId());
//            int affectedRecordCount = ps.executeUpdate();
//            System.out.println(affectedRecordCount);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public void delete(Person...entities) { //like Person[] people
//        try {
//            Statement stmt = connection.createStatement();
//            String ids = Arrays.stream(entities).map(Person::getId).map(String::valueOf).collect(joining(","));
//            int affectedRecordCount = stmt.executeUpdate(DELETE_IN_SQL.replace(":ids", ids));
//            System.out.println(affectedRecordCount);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public void update(Person entity) {
//        try {
//            PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
//            ps.setString(1, entity.getFirstName());
//            ps.setString(2, entity.getLastName());
//            ps.setTimestamp(3, convertDobToTimestamp(entity.getDob()));
//            ps.setBigDecimal(4, entity.getSalary());
//            ps.setLong(5, entity.getId());
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    private static Timestamp convertDobToTimestamp(ZonedDateTime dob) {
        return Timestamp.valueOf(dob.withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime());
    }
}
