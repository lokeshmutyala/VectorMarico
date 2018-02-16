	var flightPlanCoordinates = [];
        var selected_agent = '';
        var selected_date = ''; 
        var selected_to_date = '';
$(document).ready(function(){

          service_call(1);

           
    var view_flag = getURLParameters("view_flag");
    var log_in_agent_username = getURLParameters("log_in_agent_username");
//    alert(log_in_agent_username);
    if(log_in_agent_username != '99')
    {
    $("#agent_select").val(log_in_agent_username);
//    $("#agent_select").prop("disabled", true);
    }
    
     $("input:radio[name='datatype']").change(function(){
         service_call();
    });
    
    

    document.addEventListener("deviceready",onDeviceReady,false);  
});

    function onDeviceReady() 
    {
    	document.addEventListener("backbutton", onBackKeyDown, false);
//        service_call(1);
    
    }
 function date_change(e){
  selected_date = e.target.value;
  selected_agent = document.getElementById("agent_select");
  selected_agent = selected_agent.options[selected_agent.selectedIndex].value;
  if(selected_date == "")
  {
      alert("Please select from date.");
  }
  else if(selected_to_date == "")
  {
      alert("Please select to date.");
  }
  else if(selected_agent == "")
  {
      alert("Please select agent.");
  }
  else
  {
      service_call();
  }
}


 function to_date_change(e){
  selected_to_date = e.target.value;
  selected_agent = document.getElementById("agent_select");
  selected_agent = selected_agent.options[selected_agent.selectedIndex].value;
//  alert(selected_agent+'**'+selected_date+'**'+selected_to_date);
  if(selected_to_date == "")
  {
//      alert("Please select to date.");
  }
  else if(selected_date == "")
  {
     alert("Please select from date.");
  }
  else if(selected_agent == "")
  {
      alert("Please select agent.");
  }
  else
  {
      service_call();
  }
}


var today = new Date();
var dd = today.getDate();
var mm = today.getMonth()+1; //January is 0!
var yyyy = today.getFullYear();
 if(dd<10){
        dd='0'+dd
    } 
    if(mm<10){
        mm='0'+mm
    } 

today = yyyy+'-'+mm+'-'+dd;
document.getElementById("datepicker").setAttribute("max", today);








function getURLParameters(paramName)
{
    var sURL = window.document.URL.toString();
    if (sURL.indexOf("?") > 0)
    {
        var arrParams = sURL.split("?");
        var arrURLParams = arrParams[1].split("&");
        var arrParamNames = new Array(arrURLParams.length);
        var arrParamValues = new Array(arrURLParams.length);

        var i = 0;
        for (i = 0; i < arrURLParams.length; i++)
        {
            var sParam = arrURLParams[i].split("=");
            arrParamNames[i] = sParam[0];
            if (sParam[1] != "")
                arrParamValues[i] = unescape(sParam[1]);
            else
                arrParamValues[i] = "No Value";
        }

        for (i = 0; i < arrURLParams.length; i++)

        {
            if (arrParamNames[i] == paramName)
            {
                //alert("Parameter:" + arrParamValues[i]);
                return arrParamValues[i];
            }
        }
        return "No Parameters Found";
    }
}



function agent_select(agent_select)
{
    
    selected_agent = agent_select.value;
    selected_date = document.getElementById("from_datepicker").value;
    selected_to_date = document.getElementById("to_datepicker").value;
//    alert(agent_select.value+'--'+selected_date);
    if(selected_date == "")
    {
        alert("Please select from date.");
    }
    else if(selected_to_date == "")
    {
        alert("Please select to date.");
    }
    else
    {
        service_call();
    }
}
    
function service_call(flag)
{
//    alert("Service Call Function"+flag);
//    return false;
//    var networkState = navigator.connection.type;
//    if (networkState == Connection.NONE)
//    {
//        alert("You need active internet connection to view agent coordinates on map.");
//    }
//    else
//    {

if(flag == 1){
var audit_id = "total";    
var from_date = moment().format('YYYY/MM/DD');
var to_date = moment().format('YYYY/MM/DD');
$('#from_datepicker').val(from_date);
$('#to_datepicker').val(to_date);
}else{
var audit_id = selected_agent;   
var from_date = selected_date;
var to_date = selected_to_date;
}
            var url='http://54.245.52.20/marico_activity/marico_admin_details.php';
//var url='http://192.168.0.103/marico_admin_details.php';
            $.ajax({
            url:url,
            type: "POST",
            data: {from:from_date,to:to_date,audit_id:audit_id},
            timeout:20000,
            crossDomain: true,
            error: function (jqXHR, textStatus, errorThrown) {
			  alert("Problem while connecting to server. Please try after sometime.");
                         },
              success: function (data) {
//				 alert("Success");
                                  data = JSON.parse(data); 
//                                  var outlet_found_yes = data.outlet_found_yes;
//                                  var outlet_found_no = data.outlet_found_no;
                                  var verified_otp_yes = data.verified_otp_yes;
                                  var verified_otp_no = data.verified_otp_no;
                                  var parachute_visit_yes = data.parachute_visit_yes;
                                  var parachute_visit_no = data.parachute_visit_no;
                                  var sell_hair_oil_yes = data.sell_hair_oil_yes;
                                  var sell_hair_oil_no = data.sell_hair_oil_no;
                                  var poster_pasted_yes = data.poster_pasted_yes;
                                  var poster_pasted_no = data.poster_pasted_no;
                                  var outlet_activated_yes = data.outlet_activated_yes;
                                  var outlet_activated_no = data.outlet_activated_no;
var html_view = '<table class="striped responsive"><thead><tr><th>Name</th><th>Yes</th><th>No</th></tr></thead><tbody><tr><td>OTP Verified</td><td>'+verified_otp_yes+'</td><td>'+verified_otp_no+'</td></tr><tr><td>Parachute Visit</td><td>'+parachute_visit_yes+'</td><td>'+parachute_visit_no+'</td></tr><tr><td>Hair Oil</td><td>'+sell_hair_oil_yes+'</td><td>'+sell_hair_oil_no+'</td></tr><tr><td>Poster Pasted</td><td>'+poster_pasted_yes+'</td><td>'+poster_pasted_no+'</td></tr><tr><td>Outlet Activated</td><td>'+outlet_activated_yes+'</td><td>'+outlet_activated_yes+'</td></tr></tbody></table>';
 $(".tables").html(html_view);

}
	});

}
        
function onBackKeyDown(e) {
	   window.history.back();
	} 
