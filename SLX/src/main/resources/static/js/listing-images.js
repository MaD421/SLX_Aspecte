import { CONSTANTS } from "./constants.js";

$(function () {
    $('.js-submit-listing').on('click', handleSubmit);
    $('.js-nr-images').length && handleEdit();
});

function handleEdit() {
    let initialNrImages = $('.js-nr-images').data('nr-images');

    $('.js-delete').on('click', function (e) {
        e.preventDefault();

       if (confirm('Are you sure that you want to delete this image?')) {
           let $imageContainer = $(this).closest('.js-image-container');
           $('.js-submit-listing').prop('disabled', 'disabled');

           $.ajax({
               url: '/image/' + $imageContainer.data('image-id'),
               method: 'DELETE',
               dataType: 'text',
           })
               .done(data => {
                   if (data === "success") {
                       initialNrImages -= 1;
                       $('.js-can-upload-img').text(CONSTANTS.maxNrImages - initialNrImages);
                       $('.js-nr-images').data('nr-images', initialNrImages);
                       $imageContainer.remove();
                       $.notify("Image deleted!", "success");
                   } else {
                       $.notify("There was an error!", "error");
                   }
               })
               .always(() => {
                   $('.js-submit-listing').prop('disabled', '');
               });
       }
    });
}

function handleSubmit(e) {
    e.preventDefault();
    let $fileInput = $('input[type="file"]');
    let canUpload = $('.js-can-upload-img').length ? parseInt($('.js-can-upload-img').text()) : CONSTANTS.maxNrImages;

    if ($fileInput.get(0).files.length > canUpload) {
        alert(`You can upload only a maximum of ${canUpload} file|s!`);

        return;
    }

    let checkExtensionsAndSizes = true;

    Array.from($fileInput.get(0).files).forEach(file => {
        if (!CONSTANTS.acceptedFileExtensions.includes(file.type)) {
            alert('You can upload only .png or .jpeg/.jpg files!');
            checkExtensionsAndSizes = false;
        }

        if (file.size / 1000000 > CONSTANTS.maxUploadSize) {
            alert(`Each file must be ${CONSTANTS.maxUploadSize} MB or less!`);
            checkExtensionsAndSizes = false;
        }
    });

    if (!checkExtensionsAndSizes) { return; }

    $('form').submit();
}