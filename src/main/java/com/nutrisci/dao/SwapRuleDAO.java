/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.SwapRule;
import java.sql.SQLException;
import java.util.List;

public interface SwapRuleDAO {
    public List<SwapRule> findAll() throws SQLException;

    public SwapRule findById(int var1) throws SQLException;

    public List<SwapRule> findByGoal(String var1) throws SQLException;

    public void insert(SwapRule swapRule) throws SQLException;
}
