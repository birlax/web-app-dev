CREATE SCHEMA IF NOT EXISTS sec_master
;
CREATE SCHEMA IF NOT EXISTS trade
;
DROP TABLE IF EXISTS trade.nse_historical_price_data
;

CREATE TABLE IF NOT EXISTS trade.nse_historical_price_data(
                                                              id VARCHAR
    , symbol VARCHAR
    , industry_id VARCHAR
    , spn VARCHAR
    , sector_name_major varchar
    , sector_name_minor varchar
    , underlying_name varchar
    , asset_type varchar
    , underlying_symbol varchar
    , sector_id varchar
    , sub_sector_name varchar
    , open_price VARCHAR
    , close_price VARCHAR
    , high_price NUMERIC(38,8)
    , low_price NUMERIC(38,8)
    , last_price  NUMERIC(38,8)
    , average_price NUMERIC(38,8)
    , previous_close_price NUMERIC(38,8)
    , total_traded_quantity NUMERIC(38,2)
    , deliverable_quantity NUMERIC(38,2)
    , turnover NUMERIC(38,8)
    , pct_deliverable_qty_to_trade_qty NUMERIC(38,2)
    , series VARCHAR
    , no_of_trades BIGINT
    , trade_date TIMESTAMP(3)
    , url_id VARCHAR
    )
;

DROP TABLE IF EXISTS sec_master.sector
;
CREATE TABLE IF NOT EXISTS sec_master.sector (
                                                 id VARCHAR
    , spn VARCHAR
    , industry_id VARCHAR
    , sector_name_major VARCHAR
    , sector_name_minor VARCHAR
    , sector_id VARCHAR
    , sub_sector_name VARCHAR
    , url_id VARCHAR
)
;


INSERT INTO sec_master.sector
(id, spn, industry_id, sector_name_major, sector_name_minor, sector_id, sub_sector_name, url_id)
VALUES
     ('123', '123', 'Beverages', 'Chai/Coffee', 'Tea', '123', '123', 'abc') ,
     ('123', '123', 'Beverages', 'Chai/Coffee', 'Coffee', '123', '123', 'abc')
;


DROP TABLE IF EXISTS sec_master.securities
;
CREATE TABLE IF NOT EXISTS sec_master.securities (
                                                     id VARCHAR
    , spn VARCHAR
    , symbol VARCHAR
    , isin VARCHAR
    , short_name VARCHAR
)
;

INSERT INTO sec_master.securities
(id, spn, symbol, isin, short_name)
VALUES('123', '123', 'HEG', 'ISIN123', 'HEG-HEG');



insert
into
    trade.nse_historical_price_data
(id,
 symbol,
 industry_id,
 spn,
 sector_name_major,
 sector_name_minor,
 underlying_name,
 asset_type,
 underlying_symbol,
 sector_id,
 sub_sector_name,
 open_price,
 close_price,
 high_price,
 low_price,
 last_price,
 average_price,
 previous_close_price,
 total_traded_quantity,
 deliverable_quantity,
 turnover,
 pct_deliverable_qty_to_trade_qty,
 series,
 no_of_trades,
 trade_date,
 url_id)
values('123',
       'HEG',
       'Coal',
       '123',
       'Mining',
       '',
       '',
       'STOCK',
       'N/A',
       '',
       '',
       '',
       '',
       0,
       0,
       0,
       0,
       0,
       0,
       0,
       0,
       0,
       'EQ',
       0,
       '2018-04-28',
       '12');