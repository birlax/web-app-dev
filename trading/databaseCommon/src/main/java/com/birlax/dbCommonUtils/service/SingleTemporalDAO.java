/**
 *
 */
package com.birlax.dbCommonUtils.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.birlax.dbCommonUtils.util.ReflectionHelper;

/**
 * @author birlax
 */
public interface SingleTemporalDAO {

    public List<String> getDAOKey();

    default public List<String> getDAOFacts() {
        Set<String> facts = new HashSet<>();
        facts.addAll(this.getDAOFlatView().keySet());
        facts.removeAll(getDAOKey());
        return new ArrayList<>(facts);
    }

    default public Map<String, Object> getDAOFlatView() {
        return ReflectionHelper.getFlattenedView(this);
    }

    public String getFullyQualifiedTableName();

}
