//组件信息
var info = {
    groupId: "org.jetlinks",
    artifactId: "jetlinks-platform",
    version: "1.0.0",
    website: "http://github.com/jetlinks/jetlinks-platform",
    comment: "jetlinks"
};

var menus = [{
    "describe": " ",
    "icon": "fa fa-cogs",
    "id": "e9dc96d6b677cbae865670e6813f5e8b",
    "name": "系统设置",
    "parentId": "-1",
    "path": "sOrB",
    "permissionExpression": "",
    "sortIndex": 1,
    "status": 1,
    "url": ""
}, {
    "icon": "fa fa-book",
    "id": "915f4a85cbbac4b757956a99333ae2a7",
    "name": "数据字典",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-fhFG",
    "permissionExpression": "resource:dictionary",
    "sortIndex": 101,
    "status": 1,
    "url": "admin/dictionary/list.html"
}, {
    "icon": "fa fa-navicon",
    "id": "8db17b9ba28dd949c926b329af477a08",
    "name": "菜单管理",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-i2ea",
    "permissionExpression": "resource:menu",
    "sortIndex": 102,
    "status": 1,
    "url": "admin/menu/list.html"
}, {
    "icon": "fa fa-briefcase",
    "id": "a52df62b69e21fd756523faf8f0bd986",
    "name": "权限管理",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-X27v",
    "permissionExpression": "resource:permission",
    "sortIndex": 103,
    "status": 1,
    "url": "admin/permission/list.html"
}, {
    "icon": "fa fa-user",
    "id": "58eba1a4371dd3c0da24fac5da8cadc2",
    "name": "用户管理",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-Dz7b",
    "permissionExpression": "resource:user",
    "sortIndex": 105,
    "status": 1,
    "url": "admin/user/list.html"
}, {
    "icon": "fa fa-users",
    "id": "42fc4f83d12cc172e4690937eb15752a",
    "name": "维度管理",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-rBBu",
    "permissionExpression": "resource:dimension",
    "sortIndex": 103,
    "status": 1,
    "url": "admin/dimension/list.html"
}, {
    "icon": "fa fa-fighter-jet",
    "id": "1191174249033035776",
    "name": "协议管理",
    "parentId": "",
    "path": "eIah",
    "permissionExpression": "",
    "sortIndex": 3,
    "status": 1,
    "url": ""
}, {
    "icon": "fa fa-exchange",
    "id": "1191174514977075200",
    "name": "协议管理",
    "parentId": "1191174249033035776",
    "path": "eIah0-vXS7",
    "permissionExpression": "resource:protocol-supports",
    "sortIndex": 301,
    "status": 1,
    "url": "admin/protocol/list.html"
}, {
    "icon": "fa fa-puzzle-piece",
    "id": "1190175990379909120",
    "name": "设备管理",
    "parentId": "",
    "path": "Y8ea",
    "permissionExpression": "",
    "sortIndex": 2,
    "status": 1,
    "url": ""
}, {
    "icon": "fa fa-female",
    "id": "1190181865777729536",
    "name": "设备型号",
    "parentId": "1190175990379909120",
    "path": "Y8ea-B1HL",
    "permissionExpression": "resource:device-product",
    "sortIndex": 201,
    "status": 1,
    "url": "admin/device/product/list.html"
}, {
    "icon": "fa fa-print",
    "id": "1190181869565186048",
    "name": "设备实例",
    "parentId": "1190175990379909120",
    "path": "Y8ea-fZut",
    "permissionExpression": "resource:device-instance",
    "sortIndex": 202,
    "status": 1,
    "url": "admin/device/instance/list.html"
}];
var autzSettings = [{
    "permission":"protocol-supports",
    "id":"638a9d26f31890b7d59544251bc638db",
    "actions":["query","save","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"file",
    "id":"41ef31347373c3020573b5975569de05",
    "actions":["upload-static"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"user-token",
    "id":"a76877d99938197151ba679af228977b",
    "actions":["query","save"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"device-product",
    "id":"f4754ac5a714ec97a4e5d6ef60083975",
    "actions":["query","save","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"device-instance",
    "id":"ef85383c8adb52fcbeb7b4fe6686c6c6",
    "actions":["query","save","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"device-properties",
    "id":"936f59a0ef4cabb17b5028f8339895d3",
    "actions":["query","save","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"dictionary",
    "id":"7dc2cb54ddd22053368c84d8ad8362f3",
    "actions":["query","save","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"user",
    "id":"5f59ccf52ea4c79f5eacc10cbf40d02",
    "actions":["add","query","get","save","update","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"dimension",
    "id":"89884beecc62035bc9b8d6e6b2b6a593",
    "actions":["add","import","query","get","save","update","delete","export"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"permission",
    "id":"31e57a1df89c14607758e3dbe618912a",
    "actions":["add","query","get","save","update","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"menu",
    "id":"7ebc7d1b4316ba444bc64ae6059cd201",
    "actions":["add","query","get","save","update","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
},{
    "permission":"autz-setting",
    "id":"4e03a0db0a50c678f6b6e32cf9c31583",
    "actions":["query","save","delete"],
    "dimensionType":"user",
    "dimensionTarget":"admin",
    "dimensionTargetName":"超级管理员",
    "dimensionTypeName":"用户"
}];
//版本更新信息
var versions = [
    {
        version: "3.0.0",
        upgrade: function (context) {

        }
    }
];

function initialize(context) {
    var database = context.database;

    database.dml().insert("s_menu").values(menus).execute().sync();
    database.dml().insert("s_autz_setting_info").values(autzSettings).execute().sync();
}

function install(context) {


}


//设置依赖
dependency.setup(info)
    .onInstall(install)
    .onUpgrade(function (context) { //更新时执行
        var upgrader = context.upgrader;
        upgrader.filter(versions)
            .upgrade(function (newVer) {
                newVer.upgrade(context);
            });
    })
    .onUninstall(function (context) { //卸载时执行

    }).onInitialize(initialize);