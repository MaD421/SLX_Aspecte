// Main entry point, conditional imports are written here

// Default imports
import "./libs/jquery-3.4.1.min.js";
import "./libs/bootstrap.bundle.min.js";
import "./libs/notify.min.js";
// End default imports

document.addEventListener('readystatechange', () => {
    if (document.readyState !== "loading") {
        // Pages with pagination
        if ($('#searchInput').length || $('.js-comments-container').length) {
            import("./libs/simplePagination.js").then(() => {
                import("./pagination.js");
            });
        }

        // Pages with listings
        if ($('#searchInput').length) {
            import("./listings.js");
        }

        // Pages with listings images
        if ($('.js-submit-listing').length || $('.js-nr-images').length) {
            import("./listing-images.js");
        }

        //Listing page
        if (
            $('.js-add-comment').length ||
            $('.js-update-comment').length
        ) {
            import("./comments.js");
        }

        // Messages page
        if ($('.js-messages-container').length) {
            import("./messages.js");
        }

        // Inbox page
        if ($('.js-inbox-container').length) {
            import("./inbox.js");
        }
    }
});

$(function() {
    createGoToTopBtn();
    createGoBackBtn();
});

function createGoBackBtn() {
    if (document.referrer.indexOf(window.location.host) !== -1) {
        $('.main-container').prepend(`<a class="js-back btn btn-outline-primary mb-3" href="${document.referrer}">Back</a>`);

        $('.js-back').on('click', function() {
            history.back();
        });
    }
}

function createGoToTopBtn() {
    $('body').append("<a id='goToTop'></a>");
    let btn = $('#goToTop');

    $(window).scroll(function() {
        if ($(window).scrollTop() > 500) {
            btn.addClass('show');
        } else {
            btn.removeClass('show');
        }
    });

    btn.on('click', function(e) {
        e.preventDefault();
        $('html, body').animate({scrollTop:0}, '300');
    });
}

function confirmDelete() {
    if (confirm("Do you want to delete this item?") === false) {
        return false;
    }
}