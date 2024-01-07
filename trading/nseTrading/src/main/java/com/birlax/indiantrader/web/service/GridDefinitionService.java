package com.birlax.indiantrader.web.service;

import java.util.List;

import com.birlax.indiantrader.web.domain.SlickGridColumnOption;
import com.birlax.indiantrader.web.domain.SlickGridOption;

public interface GridDefinitionService {

    public SlickGridOption getGridOptionsByName();

    public List<SlickGridColumnOption> getGridColumnOptionsByName(String gridName);
}
