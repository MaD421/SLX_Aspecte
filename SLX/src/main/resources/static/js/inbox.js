import { formatDate, baseURL} from "./messages-utils.js";

$(function () {
    const addNewMessage = message => {
        inboxContainer.insertAdjacentHTML('beforeend', messageView(message));
    };

    const messageView = message => (
        `<div class="inbox-message mt-4 mb-4 mr-auto ml-auto">
            <a class="inbox-message__link" href="user/${message.partnerId}/messages">
                <span class="inbox-message__username">${message.partnerUsername}</span>
                <span class="inbox-message__text${message.own
                    ? '--own'
                    : '--partner'}">
                    ${(message.own
                        ? 'You: '
                        : '')
                            + message.body
                    }
                </span>
                <span class="inbox-message__date">
                    ${formatDate(new Date(message.createdAt))}
                </span>
            </a>
        </div>`
    );

    const initialFetch = async nrShowMessages => {
        const nrMessages = await fetchInbox('size', nrShowMessages);

        if (!nrMessages) {
            document.getElementsByClassName('js-more-messages')[0].parentElement.remove();
            document.getElementsByClassName('js-no-messages')[0].insertAdjacentHTML('beforeend', `<p class="text-center h4">No messages!</p>`);
        }
    };

    const fetchMoreMessages = async () => {
        const nrMessages = await fetchInbox('page', currentPage + 1);

        if (nrMessages) {
            currentPage += 1;
            window.history.pushState('', '', `?page=${currentPage}`);

            if (nrMessages < messagesPerPage) {
                document.getElementsByClassName('js-more-messages')[0].remove();
            }
        } else {
            document.getElementsByClassName('js-more-messages')[0].remove();
            $.notify('No more messages!', 'info');
        }
    };

    const fetchInbox = async (paramName, paramValue) => {
        const response = await fetch(`${baseURL}/inbox?${paramName}=${paramValue}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const messages = await response.json();

        if (messages.length) {
            messages.forEach(message => {
                addNewMessage(message);
            });
        }

        return messages.length;
    };

    const inboxContainer = document.getElementsByClassName('js-inbox-container')[0];
    let currentPage = parseInt(inboxContainer.dataset.currentPage, 10);
    const nrShowMessages = parseInt(inboxContainer.dataset.nrShowMessages, 10);
    const messagesPerPage = parseInt(inboxContainer.dataset.messagesPerPage, 10);
    const moreMessagesBtn = document.getElementsByClassName('js-more-messages')[0];

    initialFetch(nrShowMessages);

    moreMessagesBtn.addEventListener('click', async () => {
        moreMessagesBtn.setAttribute('disabled', '');
        try {
            await fetchMoreMessages(currentPage);
        } catch (error) {
            $.notify('There was an error!', 'error');
        }
        moreMessagesBtn.removeAttribute('disabled');
    });
});

