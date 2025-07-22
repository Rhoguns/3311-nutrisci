package com.nutrisci.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AppliedSwapDAO {
    List<Object> findByProfileAndDateRange(int profileId, LocalDate start, LocalDate end) throws SQLException;
}