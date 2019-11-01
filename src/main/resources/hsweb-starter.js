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
    "permissionId": "",
    "sortIndex": 1,
    "status": 1,
    "url": ""
}, {
    "icon": "fa fa-book",
    "id": "915f4a85cbbac4b757956a99333ae2a7",
    "name": "数据字典",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-fhFG",
    "permissionId": "dictionary",
    "sortIndex": 101,
    "status": 1,
    "url": "admin/dictionary/list.html"
}, {
    "icon": "fa fa-navicon",
    "id": "8db17b9ba28dd949c926b329af477a08",
    "name": "菜单管理",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-i2ea",
    "permissionId": "menu",
    "sortIndex": 102,
    "status": 1,
    "url": "admin/menu/list.html"
}, {
    "icon": "fa fa-briefcase",
    "id": "a52df62b69e21fd756523faf8f0bd986",
    "name": "权限管理",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-X27v",
    "permissionId": "permission,autz-setting",
    "sortIndex": 103,
    "status": 1,
    "url": "admin/permission/list.html"
}, {
    "icon": "fa fa-user",
    "id": "58eba1a4371dd3c0da24fac5da8cadc2",
    "name": "用户管理",
    "parentId": "e9dc96d6b677cbae865670e6813f5e8b",
    "path": "sOrB-Dz7b",
    "permissionId": "user",
    "sortIndex": 105,
    "status": 1,
    "url": "admin/user/list.html"
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

}

function install(context) {
    var database = context.database;

    database.dml().insert("s_menu").values(menus).execute().sync();

     
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