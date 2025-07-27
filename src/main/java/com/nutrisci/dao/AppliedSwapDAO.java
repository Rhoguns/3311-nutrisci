package com.nutrisci.dao;

import com.nutrisci.model.AppliedSwap;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AppliedSwapDAO {
    AppliedSwap insert(AppliedSwap appliedSwap) throws SQLException;
    
    AppliedSwap findById(int id) throws SQLException;
    
    List<AppliedSwap> findByProfile(int profileId) throws SQLException;
    
    List<AppliedSwap> findByProfileAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException;
    
    AppliedSwap update(AppliedSwap appliedSwap) throws SQLException;
    
    boolean delete(int id) throws SQLException;
}