/**
 *
 */
package com.birlax.dbCommonUtils.service;

import com.birlax.dbCommonUtils.util.ReflectionHelper;

import java.util.*;

public interface SingleTemporalDAO {

    List<String> getDAOKey();

    default List<String> getDAOFacts() {
        Set<String> facts = new HashSet<>();
        facts.addAll(this.getDAOFlatView().keySet());
        facts.removeAll(getDAOKey());
        return new ArrayList<>(facts);
    }

    default Map<String, Object> getDAOFlatView() {
        return ReflectionHelper.getFlattenedView(this);
    }

    String getFullyQualifiedTableName();

}
