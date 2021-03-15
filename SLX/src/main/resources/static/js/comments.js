$(function () {
    $('.js-add-comment').on('click', addComment);
    $('.js-update-comment').on('click', updateComment);
    $('.js-delete-comment').on('click', deleteComment);
});

const baseURL = `${window.location.protocol}//${window.location.host}${window.location.pathname
    .replace(/\/pg\/\d+/gi, '')
}`;
const onSuccess = () => {
    window.location.reload();
};
const onSuccessTimeout = 2000;

const addComment = () => {
    const text = $('#addComment').val().trim();
    const url = `${baseURL}/comment`;
    const data = JSON.stringify({
        text
    });

    $.ajax({
       method: 'POST',
       url,
       contentType: 'application/json',
       dataType: 'json',
       data
    })
        .done(response => {
            if (response.status === 'success') {
                $.notify('Comment added!', 'success');
                setTimeout(onSuccess, onSuccessTimeout);
            } else {
                $.notify('There was an error!', 'error');
            }
        })
        .catch(() => {
            $.notify('There was an error!', 'error');
        });
};

const updateComment = e => {
    const target = e.target;
    const text = $(target).parent().find('.js-comment-text').val().trim();
    const commentId = $(target).parent().data('id');
    const url = `${baseURL}/comment/${commentId}`;
    const data = JSON.stringify({
        text
    });

    $.ajax({
        method: 'PUT',
        url,
        contentType: 'application/json',
        dataType: 'json',
        data
    })
        .done(response => {
            if (response.status === 'success') {
                $.notify('Comment edited!', 'success');
                setTimeout(onSuccess, onSuccessTimeout);
            } else {
                $.notify('There was an error!', 'error');
            }
        })
        .fail(() => {
            $.notify('There was an error!', 'error');
        });
};

const deleteComment = e => {
    const commentId = $(e.target).parent().data('id');
    const url = `${baseURL}/comment/${commentId}`;

    $.ajax({
        method: 'DELETE',
        url,
        contentType: 'application/json',
    })
        .done(response => {
            if (response.status === 'success') {
                $.notify('Comment deleted!', 'success');
                setTimeout(onSuccess, onSuccessTimeout);
            } else {
                $.notify('There was an error!', 'error');
            }
        })
        .catch(() => {
            $.notify('There was an error!', 'error');
        });
};