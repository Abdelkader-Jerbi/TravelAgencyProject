package services;

import java.sql.SQLException;
import java.util.List;

public interface HotelBooking<T> {
    public void add(T t) throws SQLException;
    public void modify(T t) throws SQLException;
    public void delete(int id) throws SQLException;
    public List<T> afficher() throws SQLException;
}
