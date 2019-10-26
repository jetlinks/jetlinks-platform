importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "search-box", "miniui-tools"], function (request, SearchBox, tools) {
        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();

        tools.bindOnEnter("#search-box", search);

        function search() {
            tools.searchGrid("#search-box", grid);
        }

    });
});


