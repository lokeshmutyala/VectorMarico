
$(document).ready(function () {
sync_info();
//    document.addEventListener("deviceready", onDeviceReady_admin, false);

});

function onDeviceReady_admin()
{

    document.addEventListener("backbutton", onBackKeyDown, false);
    sync_info();
}
function sync_info()
{
var url = 'http://54.245.52.20/marico_activity/marico_activity_data_sync_info.php';
    $.ajax({
        url: url,
        type: "post",
        timeout: 20000,
        crossDomain: true,
        error: function (jqXHR, textStatus, errorThrown) {
			  alert("Error while syncing.");
                          alert(jqXHR.status);
        },
        success: function (data) {
//                  alert("Data Success"+data);
            var json = JSON.parse(data);


            var data = '';

            
            var agent_eleven_today_sync = json.MAR21.today_sync;
            var agent_eleven_month_sync = json.MAR21.month_sync;
            var agent_eleven_total_sync = json.MAR21.total_sync;
            var agent_eleven_last_sync = json.MAR21.last_sync;
            agent_eleven_last_sync = moment(agent_eleven_last_sync).add(5.5, 'hours');
            agent_eleven_last_sync = moment(agent_eleven_last_sync).format("DD-MM-YYYY HH:mm:ss");

            var agent_twelve_today_sync = json.MAR22.today_sync;
            var agent_twelve_month_sync = json.MAR22.month_sync;
            var agent_twelve_total_sync = json.MAR22.total_sync;
            var agent_twelve_last_sync = json.MAR22.last_sync;
            agent_twelve_last_sync = moment(agent_twelve_last_sync).add(5.5, 'hours');
            agent_twelve_last_sync = moment(agent_twelve_last_sync).format("DD-MM-YYYY HH:mm:ss");

            var agent_thirteen_today_sync = json.MAR23.today_sync;
            var agent_thirteen_month_sync = json.MAR23.month_sync;
            var agent_thirteen_total_sync = json.MAR23.total_sync;
            var agent_thirteen_last_sync = json.MAR23.last_sync;
            agent_thirteen_last_sync = moment(agent_thirteen_last_sync).add(5.5, 'hours');
            agent_thirteen_last_sync = moment(agent_thirteen_last_sync).format("DD-MM-YYYY HH:mm:ss");

            var agent_fourteen_today_sync = json.MAR24.today_sync;
            var agent_fourteen_month_sync = json.MAR24.month_sync;
            var agent_fourteen_total_sync = json.MAR24.total_sync;
            var agent_fourteen_last_sync = json.MAR24.last_sync;
            agent_fourteen_last_sync = moment(agent_fourteen_last_sync).add(5.5, 'hours');
            agent_fourteen_last_sync = moment(agent_fourteen_last_sync).format("DD-MM-YYYY HH:mm:ss");

            var agent_fifteen_today_sync = json.MAR25.today_sync;
            var agent_fifteen_month_sync = json.MAR25.month_sync;
            var agent_fifteen_total_sync = json.MAR25.total_sync;
            var agent_fifteen_last_sync = json.MAR25.last_sync;
            agent_fifteen_last_sync = moment(agent_fifteen_last_sync).add(5.5, 'hours');
            agent_fifteen_last_sync = moment(agent_fifteen_last_sync).format("DD-MM-YYYY HH:mm:ss");

            var agent_sixteen_today_sync = json.MAR26.today_sync;
            var agent_sixteen_month_sync = json.MAR26.month_sync;
            var agent_sixteen_total_sync = json.MAR26.total_sync;
            var agent_sixteen_last_sync = json.MAR26.last_sync;
            agent_sixteen_last_sync = moment(agent_sixteen_last_sync).add(5.5, 'hours');
            agent_sixteen_last_sync = moment(agent_sixteen_last_sync).format("DD-MM-YYYY HH:mm:ss");

            var agent_seventeen_today_sync = json.MAR27.today_sync;
            var agent_seventeen_month_sync = json.MAR27.month_sync;
            var agent_seventeen_total_sync = json.MAR27.total_sync;
            var agent_seventeen_last_sync = json.MAR27.last_sync;
            agent_seventeen_last_sync = moment(agent_seventeen_last_sync).add(5.5, 'hours');
            agent_seventeen_last_sync = moment(agent_seventeen_last_sync).format("DD-MM-YYYY HH:mm:ss");


            var agent_eighteen_today_sync = json.MAR28.today_sync;
            var agent_eighteen_month_sync = json.MAR28.month_sync;
            var agent_eighteen_total_sync = json.MAR28.total_sync;
            var agent_eighteen_last_sync = json.MAR28.last_sync;
            agent_eighteen_last_sync = moment(agent_eighteen_last_sync).add(5.5, 'hours');
            agent_eighteen_last_sync = moment(agent_eighteen_last_sync).format("DD-MM-YYYY HH:mm:ss");

            var agent_nineteen_today_sync = json.MAR29.today_sync;
            var agent_nineteen_month_sync = json.MAR29.month_sync;
            var agent_nineteen_total_sync = json.MAR29.total_sync;
            var agent_nineteen_last_sync = json.MAR29.last_sync;
            agent_nineteen_last_sync = moment(agent_nineteen_last_sync).add(5.5, 'hours');
            agent_nineteen_last_sync = moment(agent_nineteen_last_sync).format("DD-MM-YYYY HH:mm:ss");

            var agent_twenty_today_sync = json.MAR30.today_sync;
            var agent_twenty_month_sync = json.MAR30.month_sync;
            var agent_twenty_total_sync = json.MAR30.total_sync;
            var agent_twenty_last_sync = json.MAR30.last_sync;
            agent_twenty_last_sync = moment(agent_twenty_last_sync).add(5.5, 'hours');
            agent_twenty_last_sync = moment(agent_twenty_last_sync).format("DD-MM-YYYY HH:mm:ss");

            
data += '<table class="striped responsive"><thead><tr><th>Agent No</th><th>Today Sync</th><th>Month Sync</th><th>Total Sync</th><th>Last Sync</th></tr></thead><tbody><tr><td>MAR21</td><td>' + agent_eleven_today_sync + '</td><td>' + agent_eleven_month_sync + '</td><td>' + agent_eleven_total_sync + '</td><td>' + agent_eleven_last_sync + '</td></tr><tr><td>MAR22</td><td>' + agent_twelve_today_sync + '</td><td>' + agent_twelve_month_sync + '</td><td>' + agent_twelve_total_sync + '</td><td>' + agent_twelve_last_sync + '</td></tr><tr><td>MAR23</td><td>' + agent_thirteen_today_sync + '</td><td>' + agent_thirteen_month_sync + '</td><td>' + agent_thirteen_total_sync + '</td><td>' + agent_thirteen_last_sync + '</td></tr><tr><td>MAR24</td><td>' + agent_fourteen_today_sync + '</td><td>' + agent_fourteen_month_sync + '</td><td>' + agent_fourteen_total_sync + '</td><td>' + agent_fourteen_last_sync + '</td></tr><tr><td>MAR25</td><td>' + agent_fifteen_today_sync + '</td><td>' + agent_fifteen_month_sync + '</td><td>' + agent_fifteen_total_sync + '</td><td>' + agent_fifteen_last_sync + '</td></tr><tr><td>MAR26</td><td>' + agent_sixteen_today_sync + '</td><td>' + agent_sixteen_month_sync + '</td><td>' + agent_sixteen_total_sync + '</td><td>' + agent_sixteen_last_sync + '</td></tr><tr><td>MAR27</td><td>' + agent_seventeen_today_sync + '</td><td>' + agent_seventeen_month_sync + '</td><td>' + agent_seventeen_total_sync + '</td><td>' + agent_seventeen_last_sync + '</td></tr><tr><td>MAR28</td><td>' + agent_eighteen_today_sync + '</td><td>' + agent_eighteen_month_sync + '</td><td>' + agent_eighteen_total_sync + '</td><td>' + agent_eighteen_last_sync + '</td></tr><tr><td>MAR29</td><td>' + agent_nineteen_today_sync + '</td><td>' + agent_nineteen_month_sync + '</td><td>' + agent_nineteen_total_sync + '</td><td>' + agent_nineteen_last_sync + '</td></tr><tr><td>MAR30</td><td>' + agent_twenty_today_sync + '</td><td>' + agent_twenty_month_sync + '</td><td>' + agent_twenty_total_sync + '</td><td>' + agent_twenty_last_sync + '</td></tr></tbody></table>';

            
            $(".tables").html(data);

        }
    });
}

function details_button()
{
    var view_flag = '1';
    var log_in_agent_username = '99';
    window.location = './admin_audit_details.html';

}

function checker_details()
{
    window.location = './admin_checker.html';
}




function onBackKeyDown() {

    navigator.app.exitApp();

}

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

