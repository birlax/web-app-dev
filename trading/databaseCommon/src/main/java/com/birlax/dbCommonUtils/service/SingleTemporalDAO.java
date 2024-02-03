/**
 *
 */
package com.birlax.dbCommonUtils.service;

import com.birlax.dbCommonUtils.util.ReflectionHelper;

import java.util.*;

public interface SingleTemporalDAO {

    List<String> getDAOKey();

    default List<String> getDAOFacts() {
        // Anything which is not a KEY is FACT
        Set<String> facts = new HashSet<>(this.getDAOFlatView().keySet());
        getDAOKey().forEach(facts::remove);
        return new ArrayList<>(facts);
    }

    default Map<String, Object> getDAOFlatView() {
        return ReflectionHelper.getFlattenedView(this);
    }

    String getFullyQualifiedTableName();

}
