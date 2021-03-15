$(function () {
    $('#paginationContainer').length && paginationInit();
});

function paginationInit() {
    let $paginationContainer = $('#paginationContainer');

    $paginationContainer.pagination({
        pages: $paginationContainer.data('total-pages'),
        currentPage: $paginationContainer.data('current-page'),
        hrefTextPrefix: (typeof $paginationContainer.data('page-prefix') != "undefined" && $paginationContainer.data('page-prefix') !== '') ? $paginationContainer.data('page-prefix') : '',
        hrefTextSuffix: (typeof $paginationContainer.data('page-suffix') != "undefined" && $paginationContainer.data('page-suffix') !== '') ? $paginationContainer.data('page-suffix') : '',
    });
}