

drop table sandbox.brokerage_charges_rule_system
;

CREATE TABLE sandbox.brokerage_charges_rule_system (
    id serial8 NOT NULL,
    rule_id int8 NOT NULL,
    rule_name varchar(128) NOT NULL,
    broker_code varchar(128) NOT NULL,
    exchange_code varchar(128) NOT NULL,
    product_type varchar(64) NOT NULL,
    trade_type varchar(64) NOT NULL,
    brokerage NUMERIC(32,8) not null,
    call_and_trade_charge  NUMERIC(32,8) not null,
    comments varchar(256) NOT NULL
)
;


drop table sandbox.govt_and_exchange_charges_rule_system
;

CREATE TABLE sandbox.govt_and_exchange_charges_rule_system (
    id serial8 NOT NULL,
    rule_id int8 NOT NULL,
    country_code varchar(128) NOT NULL,
    exchange_code varchar(128) NOT NULL,
    product_type varchar(64) NOT NULL,
    trade_type varchar(64) NOT NULL,
    txn_code varchar(64) NOT NULL,
    security_commodity_transaction_charge  NUMERIC(32,8) not null,
    transaction_or_turnover_charge  NUMERIC(32,8) not null,
    good_and_service_charge  NUMERIC(32,8) not null,
    sebi_regulatory_charge  NUMERIC(32,8) not null,

    depository_participant_charges   NUMERIC(32,8) not null,
    comments varchar(256) NOT NULL
)
;



drop table sandbox.stamp_charges_rule_system
;

CREATE TABLE sandbox.stamp_charges_rule_system (
    id serial8 NOT NULL,
    rule_id INT not null,
    indian_state_code varchar(128) NOT null,
    stamp_duty_charge  NUMERIC(32,8) not null
    max_stamp_duty_charge NUMERIC(32,8) not null
)


insert into sandbox.govt_and_exchange_charges_rule_system (rule_id
, country_code
, exchange_code
, product_type
, trade_type
, txn_code
, security_commodity_transaction_charge
, transaction_or_turnover_charge
, good_and_service_charge
, sebi_regulatory_charge
, depository_participant_charges
, comments) values
(1,'INDIA','NSE','EQUITY','DELIVERY','BUY'  ,0.0010,0.00003250,0.180,0.000001500,0.00,'default')
,(1,'INDIA','NSE','EQUITY','DELIVERY','SELL'    ,0.0010,0.00003250,0.180,0.000001500,0.00,'default')


,(1,'INDIA','NSE','EQUITY','INTRADAY','BUY' ,0.000,0.00003250,0.180,0.000001500,0.00,'default')
,(1,'INDIA','NSE','EQUITY','INTRADAY','SELL'    ,0.000250,0.00003250,0.180,0.000001500,0.00,'default')

,(1,'INDIA','NSE','EQUITY_FUTURES','DELIVERY','BUY' ,0.0000,0.0000210,0.180,0.000001500,0.00,'default')
,(1,'INDIA','NSE','EQUITY_FUTURES','DELIVERY','SELL'    ,0.00010,0.0000210,0.180,0.000001500,0.00,'default')

,(1,'INDIA','NSE','EQUITY_OPTIONS','DELIVERY','BUY' ,0.0000,0.0000210,0.180,0.000001500,0.00,'default')
,(1,'INDIA','NSE','EQUITY_OPTIONS','DELIVERY','SELL'    ,0.00010,0.0000210,0.180,0.000001500,0.00,'default')

select * from sandbox.govt_and_exchange_charges_rule_system
select 15.0 / 10000000.0