
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
    , sector_id varchar
    , sub_sector_name varchar
    , open_price VARCHAR
    , close_price VARCHAR
    , series VARCHAR
    , trade_date TIMESTAMP(3)
    , url_id VARCHAR
)
;

DROP TABLE IF EXISTS public.sector
;
CREATE TABLE IF NOT EXISTS public.sector (
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
