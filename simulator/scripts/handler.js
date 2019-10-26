
var _logger = logger;
var _simulator = simulator;

simulator.bindHandler("/invoke-function", function (message, session) {
    var messageId = message.messageId;
    var functionId = message.function;


    session.sendMessage("/invoke-function-reply", JSON.stringify({
        messageId: messageId,
        output: "success",
        timestamp: new Date().getTime(),
        success: true
    }))
});

simulator.bindHandler("/read-property", function (message, session) {
    var messageId = message.messageId;
   //for(var i=0;i<2;i++){
        session.sendMessage("/read-property-reply", JSON.stringify({
            messageId: messageId,
            timestamp: new Date().getTime(),
            properties: {"name": "test"},
          //  headers:{
              //  frag_msg_id:messageId,
              //  frg_num:2
          //  },
            success: true
        }))
   //}

    // _simulator.runDelay(function(){
    //     session.sendMessage("/read-property-reply", JSON.stringify({
    //         messageId: messageId,
    //         timestamp: new Date().getTime(),
    //         properties: {"name": "1234"},
    //         success: true
    //     }))
    // },200);

});


simulator.onEvent(function (index, session) {
    session.sendMessage("/event",JSON.stringify({
        messageId: new Date().getTime() + "" + Math.round((Math.random() * 100000)),
        event: "properties",
        timestamp: new Date().getTime(),
        headers:{"report-property":true},
        data:{
            "temperature": ((Math.random() * 20) + 30).toFixed(2)
        }
    }))
});

simulator.onConnect(function (session) {
   // _logger.info("[{}]:连接成功",session.auth.clientId)
});

// simulator.onAuth(function(auth,index){
//     auth.setClientId("simulator-device-"+index);
//     auth.setUsername("simulator-device-"+index);
//     auth.setPassword("simulator-device-"+index);
//
// });