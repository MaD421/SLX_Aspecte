$(function() {
    $('#sortSelect').length && $('#sortSelect').on('change', handleSort);
    $('#searchInput').length && $('#searchButton').on('click', handleSearch);
    $('#currencySelect').length && $('#currencySelect').on('change', handleCurrency);
});

function handleCurrency() {
    let value = $('#currencySelect').val();
    let parsedUrl = new URL(window.location.href);
    let newUrl = [window.location.protocol, '//', window.location.host, window.location.pathname].join('');
    let allCurrency = true;

    if (value.toLocaleLowerCase() !== "all") {
        newUrl += "?currency=" + value;
        allCurrency = false;
    }

    for (let [param, value] of parsedUrl.searchParams) {
        if (param === "currency") {
            continue;
        }

        if (allCurrency) {
            newUrl += `?${param}=${value}`;
            allCurrency = false;
        } else {
            newUrl += `&${param}=${value}`;
        }
    }

    window.location.href = newUrl;
}

function handleSort() {
    let value = $('#sortSelect').val();
    let parsedUrl = new URL(window.location.href);
    let newUrl = [window.location.protocol, '//', window.location.host, window.location.pathname].join('');
    let emptySort = true;

    if (value !== "") {
        newUrl += "?sort=" + value;
        emptySort = false;
    }

    for (let [param, value] of parsedUrl.searchParams) {
        if (param === "sort") {
            continue;
        }

        if (emptySort) {
            newUrl += `?${param}=${value}`;
            emptySort = false;
        } else {
            newUrl += `&${param}=${value}`;
        }
    }

    window.location.href = newUrl;
}

function handleSearch() {
    let value = $('#searchInput').val();
    let parsedUrl = new URL(window.location.href);
    let newUrl = [window.location.protocol, '//', window.location.host, window.location.pathname].join('');
    let emptySearch = true;

    if (value !== "") {
        newUrl += "?search=" + value;
        emptySearch = false;
    }

    for (let [param, value] of parsedUrl.searchParams) {
        if (param === "search") {
            continue;
        }

        if (emptySearch) {
            newUrl += `?${param}=${value}`;
            emptySearch = false;
        } else {
            newUrl += `&${param}=${value}`;
        }
    }

    window.location.href = newUrl;
}