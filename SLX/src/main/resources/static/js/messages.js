import { formatDate, baseURL} from "./messages-utils.js";

$(function () {
    const addNewMessage = message => {
        messagesContainer.insertAdjacentHTML('beforeend', messageView(message));
    };

    const messageView = message => (
        `<p class="message-container ${message.own
            ? 'own-message-container'
            : 'partner-message-container'}">
            <span class="message ${message.own
            ? 'own-message'
            : 'partner-message'}">${message.body}</span>
            <span class="message-date">${formatDate(new Date(message.createdAt))}</span>
        </p>`
    );

    const sendMessage = async message => {
        return await fetch(`${baseURL}/send/${partnerId}`, {
            method: 'POST',
            body: message,
            headers: {
                'Content-Type': 'text/plain'
            },
        });
    };

    const initialFetch = async (partnerId, nrShowMessages) => {
        const nrMessages = await fetchMessages(partnerId, 'size', nrShowMessages);

        if (!nrMessages) {
            document.getElementsByClassName('js-more-messages')[0].parentElement.remove();
            messagesContainer.insertAdjacentHTML('beforeend', `<p class="text-center h4">No messages!</p>`);
        }
    };

    const fetchMoreMessages = async partnerId => {
        const nrMessages = await fetchMessages(partnerId, 'page', currentPage + 1);

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

    const fetchMessages = async (partnerId, paramName, paramValue) => {
        const response = await fetch(`${baseURL}/getByUser/${partnerId}?${paramName}=${paramValue}`, {
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

    const messagesContainer = document.getElementsByClassName('js-messages-container')[0];
    let currentPage = parseInt(messagesContainer.dataset.currentPage, 10);
    const partnerId = parseInt(messagesContainer.dataset.partnerId, 10);
    const nrShowMessages = parseInt(messagesContainer.dataset.nrShowMessages, 10);
    const messagesPerPage = parseInt(messagesContainer.dataset.messagesPerPage, 10);
    const moreMessagesBtn = document.getElementsByClassName('js-more-messages')[0];

    initialFetch(partnerId, nrShowMessages);

    document.getElementsByClassName('js-send-message')[0].addEventListener('click', async () => {
        const message = document.getElementById('sendMessage').value;

        if (message.trim() === '') {
            $.notify('Message must not be empty!', 'error');
            return;
        }

        const result = await sendMessage(message);

        if (result?.ok === true) {
            $.notify('Message sent!', 'success');
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        } else {
            $.notify('There was an error!', 'error');
        }
    });
    moreMessagesBtn.addEventListener('click', async () => {
        moreMessagesBtn.setAttribute('disabled', '');
        await fetchMoreMessages(partnerId);
        moreMessagesBtn.removeAttribute('disabled');
    });
});

