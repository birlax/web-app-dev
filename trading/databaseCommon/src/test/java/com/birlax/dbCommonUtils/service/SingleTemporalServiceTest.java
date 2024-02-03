/**
 *
 */
package com.birlax.dbCommonUtils.service;

import com.birlax.dbCommonUtils.BaseIntegrationTest;
import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;


public class SingleTemporalServiceTest extends BaseIntegrationTest {

    @Inject
    private SingleTemporalServiceImpl singleTemporalService;

    @Test
    public void testInsertRecords() {
        List<Sector> records = new ArrayList<>();
        Sector s = new Sector();
        s.setSectorId(7);
        s.setSectorNameMajor("TEST");
        s.setIndustryId(1);
        Sector s1 = new Sector();
        s1.setSectorId(8);
        s1.setSectorNameMajor("TEST-2");
        s1.setIndustryId(2);
        records.add(s);
        records.add(s1);
        System.out.println(singleTemporalService.insertRecords(records));
    }

    @Test
    public void testDeleteRecords() {
        List<Sector> records = new ArrayList<>();
        Sector s = new Sector();
        s.setSectorId(5);
        s.setSectorNameMajor("TEST");
        Sector s1 = new Sector();
        s1.setSectorId(6);
        s1.setSectorNameMajor("TEST-2");
        records.add(s);
        records.add(s1);
        System.out.println(singleTemporalService.deleteRecords(records));
    }

    @Test
    public void testSearchRecords() {
        List<Sector> records = new ArrayList<>();
        Sector s = new Sector();

        s.setSectorNameMinor("A");
        Sector s1 = new Sector();
        s1.setSectorNameMinor("B");

        Sector s2 = new Sector();

        records.add(s);
        records.add(s1);
        records.add(s2);
        Set<String> column = new HashSet<>();
        column.add("sector_name_minor");
        System.out.println(singleTemporalService.searchRecords(records, column));
    }

    @Test
    public void testSearchRecordsByDateRange() {
        List<Sector> records = new ArrayList<>();
        Sector s = new Sector();
        s.setSpn(156);
        records.add(s);
        Set<String> searchByColumns = new HashSet<>();
        searchByColumns.add("spn");

        Set<String> retrieveColumns = new HashSet<>();
        retrieveColumns.add("close_price");
        retrieveColumns.add("open_price");

        LocalDate startDate = BirlaxUtil.getDateFromString("20180320");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180323");

        String effectiveDateColName = "trade_date";
        System.out.println(singleTemporalService.searchRecordsForDateRange(records, searchByColumns, retrieveColumns,
                effectiveDateColName, startDate, endDate));
    }

    public class Sector implements SingleTemporalDAO {

        private int industryId;

        private int sectorId;

        private int urlId;

        private int spn;

        private String sectorNameMajor;

        private String sectorNameMinor;

        private String subSectorName;

        /*
         * (non-Javadoc)
         * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getDAOKey()
         */
        @Override
        public List<String> getDAOKey() {
            Set<String> keys = new HashSet<>();
            keys.add(ReflectionHelper.getLowerCaseSnakeCase("sectorNameMajor"));
            keys.add(ReflectionHelper.getLowerCaseSnakeCase("sectorNameMinor"));
            keys.add(ReflectionHelper.getLowerCaseSnakeCase("subSectorName"));
            keys.add(ReflectionHelper.getLowerCaseSnakeCase("industryId"));
            return new ArrayList<>(keys);
        }

        /*
         * (non-Javadoc)
         * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getDAOFacts()
         */
        @Override
        public List<String> getDAOFacts() {
            Set<String> facts = new HashSet<>();
            facts.addAll(getDAOFlatView().keySet());
            facts.removeAll(getDAOKey());
            return new ArrayList<>(facts);
        }

        /*
         * (non-Javadoc)
         * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getDAOFlatView()
         */
        @Override
        public Map<String, Object> getDAOFlatView() {
            /*
             * Map<String, Object> map = new HashMap<>();
             * map.put("sector_id", this.sectorId);
             * map.put("sector_name_major", this.sectorName);
             * map.put("industry_id", this.industryId);
             * map.put("sector_name_minor", this.sectorMinorName);
             * map.put("url_id", 0);
             */
            return ReflectionHelper.getFlattenedView(this);
        }

        /*
         * (non-Javadoc)
         * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getFullyQualifiedTableName()
         */
        @Override
        public String getFullyQualifiedTableName() {
            // return "reference.sector";
            return "trade.nse_historical_price_data";
        }

        /**
         * @return the industryId
         */
        public int getIndustryId() {
            return this.industryId;
        }

        /**
         * @param industryId
         *            the industryId to set
         */
        public void setIndustryId(int industryId) {
            this.industryId = industryId;
        }

        /**
         * @return the sectorId
         */
        public int getSectorId() {
            return this.sectorId;
        }

        /**
         * @param sectorId
         *            the sectorId to set
         */
        public void setSectorId(int sectorId) {
            this.sectorId = sectorId;
        }

        /**
         * @return the urlId
         */
        public int getUrlId() {
            return this.urlId;
        }

        /**
         * @param urlId
         *            the urlId to set
         */
        public void setUrlId(int urlId) {
            this.urlId = urlId;
        }

        /**
         * @return the sectorNameMajor
         */
        public String getSectorNameMajor() {
            return this.sectorNameMajor;
        }

        /**
         * @param sectorNameMajor
         *            the sectorNameMajor to set
         */
        public void setSectorNameMajor(String sectorNameMajor) {
            this.sectorNameMajor = sectorNameMajor;
        }

        /**
         * @return the sectorNameMinor
         */
        public String getSectorNameMinor() {
            return this.sectorNameMinor;
        }

        /**
         * @param sectorNameMinor
         *            the sectorNameMinor to set
         */
        public void setSectorNameMinor(String sectorNameMinor) {
            this.sectorNameMinor = sectorNameMinor;
        }

        /**
         * @return the subSectorName
         */
        public String getSubSectorName() {
            return this.subSectorName;
        }

        /**
         * @param subSectorName
         *            the subSectorName to set
         */
        public void setSubSectorName(String subSectorName) {
            this.subSectorName = subSectorName;
        }

        /**
         * @return the spn
         */
        public int getSpn() {
            return this.spn;
        }

        /**
         * @param spn
         *            the spn to set
         */
        public void setSpn(int spn) {
            this.spn = spn;
        }

    }
}
