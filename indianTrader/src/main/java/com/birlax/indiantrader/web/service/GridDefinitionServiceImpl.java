package com.birlax.indiantrader.web.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birlax.indiantrader.mapper.GridDefinitionMapper;
import com.birlax.indiantrader.web.domain.SlickGridColumnOption;
import com.birlax.indiantrader.web.domain.SlickGridOption;

@RestController
@RequestMapping(
        path = "/gridDefinitionService",
        produces = { "application/json" })
public class GridDefinitionServiceImpl implements GridDefinitionService {

    @Inject
    private GridDefinitionMapper gridDefinitionMapper;

    @Override
    @RequestMapping(
            path = "getGridOptionsByName",
            produces = { "application/json" })
    public SlickGridOption getGridOptionsByName() {
        return new SlickGridOption();
    }

    @Override
    @RequestMapping(
            path = "getGridColumnOptionsByName",
            produces = { "application/json" })
    public List<SlickGridColumnOption> getGridColumnOptionsByName(String gridName) {

        return gridDefinitionMapper.getGridColumnOptionsByName(gridName);
    }

}
