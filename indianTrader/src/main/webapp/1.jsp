
<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <title>Indian Trader</title>
    <link rel="stylesheet" href="lib/jquery/css/jquery-ui-1.8.16.custom.css" type="text/css"/>
    <link rel="stylesheet" href="lib/slickgrid/css/slick-default-theme.css" type="text/css"/>
</head>
<body>
<div id="myGrid" style="width:1060px;height:450px;"></div>
<div style="width:1060px;height:16px;"></div>
<div id="myGrid1" style="width:1060px;height:450px;"></div>
<script src="lib/firebugx.js"></script>
<script src="lib/jquery/js/jquery-1.7.min.js"></script>
<script src="lib/jquery/js/jquery-ui-1.8.16.custom.min.js"></script>
<script src="lib/jquery/js/jquery.event.drag-2.2.js"></script>
<script src="lib/slickgrid/js/slick.core.js"></script>
<script src="lib/slickgrid/js/slick.editors.js"></script>
<script src="lib/slickgrid/js/slick.grid.js"></script>

<script>

    var grid;
    var defaultRow = 591;
    var defaultSpnRow = 1;
    
    var gridOptions = $.getJSON("service/gridDefinitionService/getGridOptionsByName");
    var gridColumns = $.getJSON("service/gridDefinitionService/getGridColumnOptionsByName?gridName=SecurityViewGrid");
    var gridData = $.getJSON("service/securityController/getAll");

    
    dateFormatter = function(row,cell,data) {
        //console.log(arguments[2]);
         let date1 = new Date(data);
         return date1.toISOString().slice(0,10);
	}
    
    let FormatterFactory = {};
    FormatterFactory["dateFormatter"] =  dateFormatter;

    $.when(gridOptions, gridColumns,gridData).done(function(gridOptions, gridColumns, gridData) {

        let c = gridColumns[0];
        let o = gridOptions[0];
        let d = gridData[0];

        grid = new Slick.Grid("#myGrid", d, c, o);

        grid.onClick
        	.subscribe(function(e) {
        		  	e.preventDefault();
        		    var c = grid.getCellFromEvent(e);
					//console.log(c.row);
					var spn = grid.getData()[c.row]["nseSecurityId"];
					var priceGridColumns = $.getJSON("service/gridDefinitionService/getGridColumnOptionsByName?gridName=PriceVolumDeliveryViewGrid");
					var priceGridData = $.getJSON("service/historicalPriceVolumnService/getPriceVolumnForSecurity?security="+spn+"&startDate=2017-01-01&endDate=2018-01-01");
					console.log(priceGridData)
					
					$.when(priceGridData,priceGridColumns).done(function(priceGridData,priceGridColumns) {
						   let dd = priceGridData[0];
						   let cc = priceGridColumns[0];
						   cc.forEach(function(f){
							   if(f.formatter !=null){
								   f.formatter = FormatterFactory[f.formatter];
							   }
						   });
						   grid1 = new Slick.Grid("#myGrid1",dd,cc, gridOptions);
					}); 

	});
    });
</script>
</body>
</html>
