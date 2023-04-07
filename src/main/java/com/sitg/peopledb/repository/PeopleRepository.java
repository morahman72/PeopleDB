package com.sitg.peopledb.repository;

import com.sitg.peopledb.annotation.SQL;
import com.sitg.peopledb.model.Address;
import com.sitg.peopledb.model.Person;
import com.sitg.peopledb.model.Region;

import java.math.BigDecimal;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.sitg.peopledb.model.CrudOperation.*;

public class PeopleRepository extends CrudRepository<Person> {
    private AddressRepository addressRepository = null;
    public static final String SAVE_PERSON_SQL = """
            INSERT INTO PEOPLE
            (FIRST_NAME, LAST_NAME, DOB, SALARY, EMAIL, HOME_ADDRESS, BIZ_ADDRESS) 
            VALUES(?, ?, ?, ?, ?, ?, ?)""";
    public static final String FIND_BY_ID_SQL = """
            SELECT 
            P.ID, P.FIRST_NAME, P.LAST_NAME, P.DOB, P.SALARY, P.HOME_ADDRESS,
            HOME.ID AS HOME_ID, HOME.STREET_ADDRESS AS HOME_STREET_ADDRESS, HOME.ADDRESS2 AS HOME_ADDRESS2, HOME.CITY AS HOME_CITY, HOME.STATE AS HOME_STATE, HOME.POSTCODE AS HOME_POSTCODE, HOME.COUNTY AS HOME_COUNTY, HOME.REGION AS HOME REGION, HOME.COUNTRY AS HOME_COUNTRY, 
            BIZ.ID AS BIZ_ID, BIZ.STREET_ADDRESS AS BIZ_STREET_ADDRESS, BIZ.ADDRESS2 AS BIZ_ADDRESS2, BIZ.CITY AS BIZ_CITY, BIZ.STATE AS BIZ_STATE, BIZ.POSTCODE AS BIZ_POSTCODE, BIZ.COUNTY AS BIZ_COUNTY, BIZ.REGION AS BIZ REGION, BIZ.COUNTRY AS BIZ_COUNTRY, 
            FROM PEOPLE AS P
            LEFT OUTER JOIN ADDRESSES AS HOME ON P.HOME_ADDRESS = HOME.ID
            LEFT OUTER JOIN ADDRESSES AS BIZ ON P.BIZ_ADDRESS = BIZ.ID
            WHERE P.ID=?""";
    public static final String FIND_ALL_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB, SALARY FROM PEOPLE";
    public static final String SELECT_COUNT_SQL = "SELECT COUNT(*) FROM PEOPLE";
    public static final String DELETE_SQL = "DELETE FROM PEOPLE WHERE ID=?";
    public static final String DELETE_IN_SQL = "DELETE FROM PEOPLE WHERE ID IN (:ids)";
    public static final String UPDATE_SQL = "UPDATE PEOPLE SET FIRST_NAME=?, LAST_NAME=?, DOB=?, SALARY=?, WHERE ID=?";

    public PeopleRepository(Connection connection) {
        super(connection);
        addressRepository = new AddressRepository(connection);
    }
    @Override
    @SQL(value = SAVE_PERSON_SQL, operationType = SAVE)
    void mapForSave(Person entity, PreparedStatement ps) throws SQLException {
        Address savedAddress = null;
        ps.setString(1, entity.getFirstName());
        ps.setString(2, entity.getLastName());
        ps.setTimestamp(3, convertDobToTimestamp(entity.getDob()));
        ps.setBigDecimal(4, entity.getSalary());
        ps.setString(5, entity.getEmail());
        associatedAddressWithPerson(ps, entity.getHomeAddress(), 6);
        associatedAddressWithPerson(ps, entity.getBusinessAddress(), 7);
    }

    private void associatedAddressWithPerson(PreparedStatement ps, Optional<Address> address, int parameterIndex) throws SQLException {
        Address savedAddress;
        if (address.isPresent()) {
            savedAddress = addressRepository.save(address.get());
            ps.setLong(parameterIndex, savedAddress.id());
        }else {
            ps.setObject(parameterIndex, null);
        }
    }

    @Override
    @SQL(value = UPDATE_SQL, operationType = UPDATE)
    void mapForUpdate(Person entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getFirstName());
        ps.setString(2, entity.getLastName());
        ps.setTimestamp(3, convertDobToTimestamp(entity.getDob()));
        ps.setBigDecimal(4, entity.getSalary());
    }
    @Override
    @SQL(value = FIND_BY_ID_SQL, operationType = FIND_BY_ID)
    @SQL(value = FIND_ALL_SQL, operationType = FIND_ALL)
    @SQL(value = SELECT_COUNT_SQL, operationType = COUNT)
    @SQL(value = DELETE_SQL, operationType = DELETE_ONE)
    Person extractEntityFromResultSet(ResultSet rs) throws SQLException {
        long personId = rs.getLong("ID");
        String firstName = rs.getString("FIRST_NAME");
        String lastName = rs.getString("LAST_NAME");
        ZonedDateTime dob = ZonedDateTime.of(rs.getTimestamp("DOB").toLocalDateTime(), ZoneId.of("+0"));
        BigDecimal salary = rs.getBigDecimal("SALARY");
        long homeAddressId = rs.getLong("HOME_ADDRESS");

        Address homeAddress = extractAddress(rs, "HOME_");
        Address bizAddress = extractAddress(rs, "BIZ_");

        Person person = new Person(personId, firstName, lastName, dob, salary);
        person.setHomeAddress(homeAddress);
        person.setBusinessAddress(bizAddress);
        return person;
    }

//        Optional<Address> homeAddress = addressRepository.findById(homeAddressId);
//        Person person = new Person(personId, firstName, lastName, dob, salary);
//        person.setHomeAddress(homeAddress.orElse(null));
//        return person;

    private Address extractAddress(ResultSet rs, String aliasPrefix) throws SQLException {
        if (rs.getObject(aliasPrefix + "ID")== null) {return null;}
        Long addrId = rs.getLong(aliasPrefix + "ID");
        String streetAddress = rs.getString (aliasPrefix + "STREET_ADDRESS");
        String address2 = rs.getString(aliasPrefix + "ADDRESS2");
        String city = rs.getString(aliasPrefix + "CITY");
        String state = rs.getString(aliasPrefix + "STATE");
        String postcode = rs.getString(aliasPrefix + "POSTCODE");
        String county = rs.getString(aliasPrefix + "COUNTY");
        Region region = Region.valueOf(rs.getString(aliasPrefix + "REGION").toUpperCase());
        String country = rs.getString(aliasPrefix + "COUNTRY");
        Address address = new Address(addrId, streetAddress, address2, city, state, postcode, country, county, region);
        return address;
    }


//    private Address extractAddress(ResultSet rs, String aliasPrefix) throws SQLException {
//        Long addrId = getValueByAlias(aliasPrefix + "ID", rs, Long.class);
//        if (addrId == null) return null;
//        String streetAddress = getValueByAlias(aliasPrefix + "STREET_ADDRESS",rs, String.class);
//        String address2 = getValueByAlias(aliasPrefix + "ADDRESS2",rs, String.class);
//        String city = getValueByAlias(aliasPrefix + "CITY",rs, String.class);
//        String state = getValueByAlias(aliasPrefix + "STATE",rs, String.class);
//        String postcode = getValueByAlias(aliasPrefix + "POSTCODE",rs, String.class);
//        String county = getValueByAlias(aliasPrefix + "COUNTY",rs, String.class);
//        Region region = Region.valueOf(getValueByAlias(aliasPrefix + "REGION",rs, String.class).toUpperCase());
//        String country = getValueByAlias(aliasPrefix + "COUNTRY",rs, String.class);
//        Address address = new Address(addrId, streetAddress, address2, city, state, postcode, country, county, region);
//        return address;
//    }
//
//    private <T> T getValueByAlias(String alias, ResultSet rs, Class<T> clazz) throws SQLException {
//        int columnCount = rs.getMetaData().getColumnCount();
//        for (int colIdx = 1; colIdx <= columnCount; colIdx++) {
//            if (alias.equals(rs.getMetaData().getColumnLabel(colIdx))) {
//                return (T) rs.getObject(colIdx);
//            }
//        }
//        throw new SQLException(String.format("Column not found for alias: '%s'", alias));
//    }

    private static Timestamp convertDobToTimestamp(ZonedDateTime dob) {
        return Timestamp.valueOf(dob.withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime());
    }
}
